package com.stanislav_pav.repstation.monetization

import android.app.Activity
import android.content.Context
import com.stanislav_pav.repstation.BuildConfig
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesException
import com.revenuecat.purchases.PurchasesTransactionException
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitOfferings
import com.revenuecat.purchases.awaitPurchase
import com.revenuecat.purchases.awaitRestore
import com.revenuecat.purchases.Package as RevenueCatPackage

class RevenueCatBillingRepository(context: Context) {
    private val sharedPreferences = context.applicationContext.getSharedPreferences(
        "monetization_prefs",
        Context.MODE_PRIVATE
    )

    val isConfigured: Boolean
        get() = Purchases.isConfigured

    val isLocalProUnlocked: Boolean
        get() = sharedPreferences.getBoolean(LocalProUnlockedKey, false)

    suspend fun getCustomerInfo(): CustomerInfo? {
        if (!isConfigured) return null
        return Purchases.sharedInstance.awaitCustomerInfo()
    }

    suspend fun getPackages(): List<RevenueCatPackage> {
        if (!isConfigured) return emptyList()
        return Purchases.sharedInstance.awaitOfferings().current?.availablePackages.orEmpty()
    }

    suspend fun purchase(activity: Activity, packageToPurchase: RevenueCatPackage): CustomerInfo {
        val result = Purchases.sharedInstance.awaitPurchase(
            PurchaseParams.Builder(activity, packageToPurchase).build()
        )
        return result.customerInfo
    }

    suspend fun restorePurchases(): CustomerInfo? {
        if (!isConfigured) return null
        return Purchases.sharedInstance.awaitRestore()
    }

    fun isPro(customerInfo: CustomerInfo?): Boolean {
        if (isLocalProUnlocked) return true

        return customerInfo
            ?.entitlements
            ?.get(MonetizationConfig.PRO_ENTITLEMENT_ID)
            ?.isActive == true
    }

    fun unlockWithCode(code: String): Boolean {
        val configuredCode = BuildConfig.PRO_UNLOCK_CODE.trim()
        if (configuredCode.isEmpty()) return false
        if (code.trim() != configuredCode) return false

        sharedPreferences.edit()
            .putBoolean(LocalProUnlockedKey, true)
            .apply()
        return true
    }

    fun errorMessage(throwable: Throwable): String {
        return when (throwable) {
            is PurchasesTransactionException -> {
                if (throwable.userCancelled) "Purchase cancelled"
                else throwable.error.message
            }

            is PurchasesException -> throwable.error.message
            else -> throwable.message ?: "Billing is unavailable"
        }
    }

    private companion object {
        const val LocalProUnlockedKey = "local_pro_unlocked"
    }
}
