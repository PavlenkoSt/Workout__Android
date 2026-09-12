package com.stanislav_pav.repstation.monetization

data class MonetizationState(
    val isLoading: Boolean = true,
    val isPurchasing: Boolean = false,
    val isRestoring: Boolean = false,
    val isPro: Boolean = false,
    val isLocalProUnlocked: Boolean = false,
    val product: StoreProduct? = null,
    val isPending: Boolean = false,
    val message: String? = null
)
