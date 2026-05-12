package com.stanislav_pav.repstation.monetization

import com.revenuecat.purchases.Package as RevenueCatPackage

data class MonetizationState(
    val isLoading: Boolean = true,
    val isPurchasing: Boolean = false,
    val isRestoring: Boolean = false,
    val isPro: Boolean = false,
    val isLocalProUnlocked: Boolean = false,
    val packages: List<RevenueCatPackage> = emptyList(),
    val message: String? = null,
    val isRevenueCatConfigured: Boolean = false
)
