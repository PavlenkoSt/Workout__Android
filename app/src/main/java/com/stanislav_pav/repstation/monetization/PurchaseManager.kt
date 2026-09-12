package com.stanislav_pav.repstation.monetization

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Owns access policy independently of Google Play and the activity lifecycle. */
class PurchaseManager(
    private val store: StoreGateway,
    private val cache: AccessCache,
    private val productId: String,
    private val accessCode: String
) {
    private val mutableState = MutableStateFlow(
        MonetizationState(isPro = cache.purchased || cache.localUnlocked,
            isLocalProUnlocked = cache.localUnlocked)
    )
    val state = mutableState.asStateFlow()
    private val ownershipMutex = Mutex()
    private val acknowledgedTokens = mutableSetOf<String>()
    private var refreshing = false

    suspend fun refresh(restoring: Boolean = false) {
        if (refreshing) return
        refreshing = true
        mutableState.update { it.copy(isLoading = true, isRestoring = restoring, message = null) }
        try {
            // Price lookup must never prevent restoring or revoking an entitlement.
            ownershipMutex.withLock {
                attempt {
                    applyPurchases(store.queryPurchases(), authoritative = true)
                    if (restoring && state.value.message == null) {
                        message(when {
                            state.value.isPro -> "Pro access restored"
                            state.value.isPending -> pendingMessage
                            else -> "No Pro purchase found. Check that Google Play uses the account you purchased with."
                        })
                    }
                }
            }
            try {
                val product = store.loadProduct()
                mutableState.update { it.copy(product = product) }
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                mutableState.update { it.copy(product = null, message = it.message ?: error.userMessage()) }
            }
        } finally {
            refreshing = false
            mutableState.update { it.copy(isLoading = false, isRestoring = false) }
        }
    }

    /** The launcher is provided by the foreground activity; it is never retained. */
    suspend fun purchase(launch: (StoreProduct) -> StoreUpdate?) {
        val current = state.value
        if (current.isPro || current.isLoading || current.isPurchasing || current.isPending) return
        val displayedProduct = current.product ?: return
        mutableState.update { it.copy(isPurchasing = true, message = null) }
        try {
            val freshProduct = store.loadProduct()
            mutableState.update { it.copy(product = freshProduct) }
            if (freshProduct != displayedProduct) {
                mutableState.update { it.copy(isPurchasing = false) }
                message("The price or offer has changed. Review the updated price and tap Unlock Pro again.")
                return
            }
            launch(freshProduct)?.let { onPurchasesUpdated(it) }
        } catch (error: Exception) {
            mutableState.update { it.copy(isPurchasing = false) }
            if (error is CancellationException) throw error
            message(error.userMessage())
        }
    }

    suspend fun onPurchasesUpdated(update: StoreUpdate) {
        try {
            when (update) {
                is StoreUpdate.Completed -> ownershipMutex.withLock {
                    attempt { applyPurchases(update.purchases, authoritative = false) }
                }
                StoreUpdate.Cancelled -> message("Purchase cancelled. You have not been charged.")
                StoreUpdate.AlreadyOwned -> ownershipMutex.withLock {
                    // Checkout can return this while an on-resume refresh is loading the price.
                    // Always re-query ownership; coalescing this into that refresh can lose it.
                    attempt { applyPurchases(store.queryPurchases(), authoritative = true) }
                }
                is StoreUpdate.Failed -> message(update.message)
            }
        } finally {
            mutableState.update { it.copy(isPurchasing = false) }
        }
    }

    private suspend fun applyPurchases(purchases: List<StorePurchase>, authoritative: Boolean) {
        val matching = purchases.filter { productId in it.products }
        val completed = matching.filter { it.status == PurchaseStatus.PURCHASED }
        val pending = matching.any { it.status == PurchaseStatus.PENDING }
        if (authoritative || completed.isNotEmpty()) cache.purchased = completed.isNotEmpty()
        mutableState.update {
            it.copy(isPro = cache.purchased || cache.localUnlocked,
                isLocalProUnlocked = cache.localUnlocked,
                isPending = if (authoritative || matching.isNotEmpty()) pending else it.isPending,
                message = if (pending) pendingMessage else null)
        }
        // Non-consumable: acknowledge, NEVER consume. Retry failures at the next refresh.
        for (purchase in completed) {
            if (!purchase.acknowledged && purchase.token !in acknowledgedTokens) {
                try {
                    store.acknowledge(purchase.token)
                    acknowledgedTokens += purchase.token
                } catch (error: Exception) {
                    if (error is CancellationException) throw error
                    message("Pro is unlocked, but Google Play confirmation is still needed. Reconnect and tap Retry to finish your purchase.")
                }
            }
        }
    }

    fun unlockWithCode(code: String): Boolean {
        val valid = accessCode.isNotBlank() && code.trim() == accessCode.trim()
        if (valid) cache.localUnlocked = true
        mutableState.update {
            it.copy(isPro = cache.purchased || cache.localUnlocked,
                isLocalProUnlocked = cache.localUnlocked,
                message = if (valid) "Pro unlocked" else "Invalid access code")
        }
        return valid
    }

    fun clearMessage() = mutableState.update { it.copy(message = null) }

    private fun message(value: String) = mutableState.update { it.copy(message = value) }

    private suspend fun attempt(block: suspend () -> Unit) {
        try { block() } catch (error: Exception) {
            if (error is CancellationException) throw error
            message(error.userMessage())
        }
    }

    private fun Exception.userMessage() = message ?: "Google Play is unavailable. Please try again."

    private companion object {
        const val pendingMessage = "Payment is pending. Pro will unlock once Google Play confirms payment."
    }
}
