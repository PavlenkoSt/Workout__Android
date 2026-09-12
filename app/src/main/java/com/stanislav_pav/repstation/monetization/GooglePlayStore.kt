package com.stanislav_pav.repstation.monetization

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** One process-wide client. Only Google Play's INAPP products are queried. */
class GooglePlayStore(
    context: Context,
    private val productId: String,
    private val onUpdate: (StoreUpdate) -> Unit
) : StoreGateway {
    private val applicationContext = context.applicationContext
    private val connectionMutex = Mutex()
    private var client = createClient()
    private var currentDetails: ProductDetails? = null

    private fun createClient() = BillingClient.newBuilder(applicationContext)
        .setListener { result, purchases ->
            onUpdate(when (result.responseCode) {
                BillingClient.BillingResponseCode.OK -> if (purchases == null) {
                    StoreUpdate.Failed("Google Play did not return the purchase. Tap Restore purchases to check again.")
                } else StoreUpdate.Completed(purchases.map { it.toStorePurchase() })
                BillingClient.BillingResponseCode.USER_CANCELED -> StoreUpdate.Cancelled
                BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> StoreUpdate.AlreadyOwned
                else -> StoreUpdate.Failed(result.userMessage())
            })
        }
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .enableAutoServiceReconnection()
        .build()

    private suspend fun connect() = connectionMutex.withLock {
        if (client.isReady) return@withLock
        try {
            timedRequest {
                suspendCancellableCoroutine { continuation ->
                    client.startConnection(object : BillingClientStateListener {
                        override fun onBillingSetupFinished(result: BillingResult) {
                            if (!continuation.isActive) return
                            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                                continuation.resume(Unit)
                            } else continuation.resumeWithException(IllegalStateException(result.userMessage()))
                        }
                        override fun onBillingServiceDisconnected() {
                            if (continuation.isActive) continuation.resumeWithException(
                                IllegalStateException("Google Play disconnected. Please try again.")
                            )
                        }
                    })
                }
            }
        } catch (error: Exception) {
            client.endConnection()
            client = createClient()
            throw error
        }
    }

    override suspend fun queryPurchases(): List<StorePurchase> {
        connect()
        return timedRequest {
            suspendCancellableCoroutine { continuation ->
                client.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build()
                ) { result, purchases ->
                    if (!continuation.isActive) return@queryPurchasesAsync
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        continuation.resume(purchases.map { it.toStorePurchase() })
                    } else continuation.resumeWithException(IllegalStateException(result.userMessage()))
                }
            }
        }
    }

    override suspend fun loadProduct(): StoreProduct {
        connect()
        val details: ProductDetails = timedRequest {
            suspendCancellableCoroutine { continuation ->
                val params = QueryProductDetailsParams.newBuilder().setProductList(listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId).setProductType(BillingClient.ProductType.INAPP).build()
                )).build()
                client.queryProductDetailsAsync(params) { result, response ->
                    if (!continuation.isActive) return@queryProductDetailsAsync
                    val product = response.productDetailsList.firstOrNull { it.productId == productId }
                    if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                        continuation.resumeWithException(IllegalStateException(result.userMessage()))
                    } else if (product == null) {
                        continuation.resumeWithException(IllegalStateException(
                            "Pro is currently unavailable for this Google Play account. Please try again later."
                        ))
                    } else continuation.resume(product)
                }
            }
        }
        // Only a regular buy option is suitable for a permanent unlock. Never select rentals,
        // preorders, or promotional offers whose conditions this paywall does not display.
        val offer = details.oneTimePurchaseOfferDetailsList.orEmpty().firstOrNull {
            it.rentalDetails == null && it.preorderDetails == null && it.offerId == null
        } ?: throw IllegalStateException("The lifetime unlock is currently unavailable. Please try again later.")
        currentDetails = details
        return StoreProduct(details.productId, offer.formattedPrice, offer.offerToken)
    }

    fun launchPurchase(activity: Activity, product: StoreProduct): StoreUpdate? {
        if (activity.isFinishing || activity.isDestroyed) {
            return StoreUpdate.Failed("Reopen the Pro screen to complete your purchase.")
        }
        val details = currentDetails
        if (details == null || details.productId != product.id) {
            return StoreUpdate.Failed("Reload the price and try again.")
        }
        val params = BillingFlowParams.newBuilder().setProductDetailsParamsList(listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(details)
                .apply { product.offerToken?.let { setOfferToken(it) } }.build()
        )).build()
        val result = client.launchBillingFlow(activity, params)
        return when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> null // Completion arrives through the listener.
            BillingClient.BillingResponseCode.USER_CANCELED -> StoreUpdate.Cancelled
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> StoreUpdate.AlreadyOwned
            else -> StoreUpdate.Failed(result.userMessage())
        }
    }

    override suspend fun acknowledge(token: String) {
        connect()
        timedRequest {
            suspendCancellableCoroutine { continuation ->
                client.acknowledgePurchase(
                    AcknowledgePurchaseParams.newBuilder().setPurchaseToken(token).build()
                ) { result ->
                    if (!continuation.isActive) return@acknowledgePurchase
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) continuation.resume(Unit)
                    else continuation.resumeWithException(IllegalStateException(result.userMessage()))
                }
            }
        }
    }

    private suspend fun <T> timedRequest(block: suspend () -> T): T = try {
        withTimeout(15_000) { block() }
    } catch (_: TimeoutCancellationException) {
        throw IllegalStateException("Google Play took too long to respond. Check your connection and try again.")
    }

    private fun Purchase.toStorePurchase() = StorePurchase(products, purchaseToken, when (purchaseState) {
        Purchase.PurchaseState.PURCHASED -> PurchaseStatus.PURCHASED
        Purchase.PurchaseState.PENDING -> PurchaseStatus.PENDING
        else -> PurchaseStatus.UNSPECIFIED
    }, isAcknowledged)

    private fun BillingResult.userMessage() = when (responseCode) {
        BillingClient.BillingResponseCode.NETWORK_ERROR,
        BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
        BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> "Cannot reach Google Play. Check your connection and try again."
        BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> "Purchases are unavailable. Sign in to the Google Play Store and check that it is up to date."
        BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> "Pro is unavailable for this Google Play account. Please try again later."
        else -> "Google Play could not complete the request. Please try again."
    }
}
