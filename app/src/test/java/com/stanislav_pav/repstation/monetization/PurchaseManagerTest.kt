package com.stanislav_pav.repstation.monetization

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import org.junit.Assert.*
import org.junit.Test

class PurchaseManagerTest {
    private val productId = "lifetime"
    private val product = StoreProduct(productId, "$4.99", "offer")
    private val store = FakeStore()
    private val cache = MemoryAccess()
    private fun manager() = PurchaseManager(store, cache, productId, "friends")
    private fun purchase(acknowledged: Boolean = false, pending: Boolean = false,
                         id: String = productId) = StorePurchase(
        listOf(id), "token", if (pending) PurchaseStatus.PENDING else PurchaseStatus.PURCHASED,
        acknowledged
    )

    @Test fun completedPurchaseUnlocksPro() = runBlocking {
        val manager = manager()
        manager.onPurchasesUpdated(StoreUpdate.Completed(listOf(purchase())))
        assertTrue(manager.state.value.isPro)
        assertTrue(cache.purchased)
        assertEquals(listOf("token"), store.acknowledged)
    }

    @Test fun acknowledgedPurchaseRestoresWithoutAnotherAcknowledgement() = runBlocking {
        store.purchases = listOf(purchase(acknowledged = true))
        val manager = manager()
        manager.refresh(restoring = true)
        assertTrue(manager.state.value.isPro)
        assertTrue(store.acknowledged.isEmpty())
    }

    @Test fun pendingPurchaseDoesNotUnlockPro() = runBlocking {
        val manager = manager()
        manager.onPurchasesUpdated(StoreUpdate.Completed(listOf(purchase(pending = true))))
        assertFalse(manager.state.value.isPro)
        assertTrue(manager.state.value.isPending)
        assertTrue(store.acknowledged.isEmpty())
    }

    @Test fun unrelatedProductDoesNotUnlockPro() = runBlocking {
        val manager = manager()
        manager.onPurchasesUpdated(StoreUpdate.Completed(listOf(purchase(id = "other"))))
        assertFalse(manager.state.value.isPro)
        assertTrue(store.acknowledged.isEmpty())
    }

    @Test fun queryFailureKeepsAccessAcrossProcessRestart() = runBlocking {
        cache.purchased = true
        store.queryError = IllegalStateException("Offline")
        val manager = manager()
        manager.refresh()
        assertTrue(manager.state.value.isPro)
        assertNotNull(manager.state.value.message)
    }

    @Test fun successfulEmptyQueryRevokesPaidAccess() = runBlocking {
        cache.purchased = true
        val manager = manager()
        manager.refresh()
        assertFalse(manager.state.value.isPro)
        assertFalse(cache.purchased)
    }

    @Test fun localUnlockSurvivesEmptyQuery() = runBlocking {
        val manager = manager()
        assertTrue(manager.unlockWithCode(" friends "))
        manager.refresh()
        assertTrue(manager.state.value.isPro)
    }

    @Test fun invalidCodeDoesNotRevokePaidAccess() = runBlocking {
        cache.purchased = true
        val manager = manager()
        assertFalse(manager.unlockWithCode("wrong"))
        assertTrue(manager.state.value.isPro)
    }

    @Test fun acknowledgementFailureIsRetriedWithoutLosingAccess() = runBlocking {
        store.purchases = listOf(purchase())
        store.ackError = IllegalStateException("Offline")
        val manager = manager()
        manager.refresh()
        assertTrue(manager.state.value.isPro)
        assertNotNull(manager.state.value.message)
        store.ackError = null
        manager.refresh()
        assertEquals(listOf("token"), store.acknowledged)
    }

    @Test fun priceFailureDoesNotBlockRestoration() = runBlocking {
        store.productError = IllegalStateException("Price unavailable")
        store.purchases = listOf(purchase(acknowledged = true))
        val manager = manager()
        manager.refresh(restoring = true)
        assertTrue(manager.state.value.isPro)
    }

    @Test fun cancellationClearsCheckoutProgress() = runBlocking {
        val manager = manager()
        manager.refresh()
        manager.purchase { null }
        assertTrue(manager.state.value.isPurchasing)
        manager.onPurchasesUpdated(StoreUpdate.Cancelled)
        assertFalse(manager.state.value.isPurchasing)
        assertFalse(manager.state.value.isPro)
    }

    @Test fun changedPriceMustBeReviewedBeforeCheckout() = runBlocking {
        val manager = manager()
        manager.refresh()
        store.product = product.copy(formattedPrice = "$5.99")
        var launched = false
        manager.purchase { launched = true; null }
        assertFalse(launched)
        assertEquals("$5.99", manager.state.value.product?.formattedPrice)
        assertFalse(manager.state.value.isPurchasing)
    }

    @Test fun duplicateCallbackAcknowledgesOnlyOnce() = runBlocking {
        val manager = manager()
        repeat(2) { manager.onPurchasesUpdated(StoreUpdate.Completed(listOf(purchase()))) }
        assertEquals(listOf("token"), store.acknowledged)
    }

    @Test fun pendingPurchaseBecomesPurchasedOnRefresh() = runBlocking {
        store.purchases = listOf(purchase(pending = true))
        val manager = manager()
        manager.refresh()
        store.purchases = listOf(purchase())
        manager.refresh()
        assertTrue(manager.state.value.isPro)
        assertFalse(manager.state.value.isPending)
    }

    @Test fun alreadyOwnedRestoresEvenDuringPriceRefresh() = runBlocking {
        val priceStarted = CompletableDeferred<Unit>()
        val finishPrice = CompletableDeferred<Unit>()
        store.beforeProduct = { priceStarted.complete(Unit); finishPrice.await() }
        val manager = manager()
        val refreshing = launch { manager.refresh() }
        priceStarted.await()
        store.purchases = listOf(purchase(acknowledged = true))
        manager.onPurchasesUpdated(StoreUpdate.AlreadyOwned)
        finishPrice.complete(Unit)
        refreshing.join()
        assertTrue(manager.state.value.isPro)
    }

    @Test fun duplicateBuyDoesNotLaunchAnotherCheckout() = runBlocking {
        val manager = manager()
        manager.refresh()
        var launches = 0
        repeat(2) { manager.purchase { launches++; null } }
        assertEquals(1, launches)
    }

    @Test fun launchFailureAllowsRetry() = runBlocking {
        val manager = manager()
        manager.refresh()
        manager.purchase { StoreUpdate.Failed("Unavailable") }
        assertFalse(manager.state.value.isPurchasing)
        var launched = false
        manager.purchase { launched = true; null }
        assertTrue(launched)
    }

    private inner class FakeStore : StoreGateway {
        var product: StoreProduct = this@PurchaseManagerTest.product
        var purchases = emptyList<StorePurchase>()
        var queryError: Exception? = null
        var productError: Exception? = null
        var ackError: Exception? = null
        var beforeProduct: suspend () -> Unit = {}
        val acknowledged = mutableListOf<String>()
        override suspend fun queryPurchases(): List<StorePurchase> {
            queryError?.let { throw it }
            return purchases
        }
        override suspend fun loadProduct(): StoreProduct {
            beforeProduct()
            productError?.let { throw it }
            return product
        }
        override suspend fun acknowledge(token: String) {
            ackError?.let { throw it }
            acknowledged += token
        }
    }

    private class MemoryAccess : AccessCache {
        override var purchased = false
        override var localUnlocked = false
    }
}
