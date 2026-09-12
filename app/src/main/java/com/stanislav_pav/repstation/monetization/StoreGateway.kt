package com.stanislav_pav.repstation.monetization

data class StoreProduct(val id: String, val formattedPrice: String, val offerToken: String?)

enum class PurchaseStatus { PURCHASED, PENDING, UNSPECIFIED }

data class StorePurchase(
    val products: List<String>,
    val token: String,
    val status: PurchaseStatus,
    val acknowledged: Boolean
)

sealed interface StoreUpdate {
    data class Completed(val purchases: List<StorePurchase>) : StoreUpdate
    data object Cancelled : StoreUpdate
    data object AlreadyOwned : StoreUpdate
    data class Failed(val message: String) : StoreUpdate
}

interface StoreGateway {
    suspend fun queryPurchases(): List<StorePurchase>
    suspend fun loadProduct(): StoreProduct
    suspend fun acknowledge(token: String)
}

interface AccessCache {
    var purchased: Boolean
    var localUnlocked: Boolean
}
