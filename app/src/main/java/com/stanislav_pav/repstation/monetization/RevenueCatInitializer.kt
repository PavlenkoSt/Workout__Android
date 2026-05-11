package com.stanislav_pav.repstation.monetization

import android.app.Application
import com.stanislav_pav.repstation.BuildConfig
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration

object RevenueCatInitializer {
    fun configure(application: Application) {
        val apiKey = BuildConfig.REVENUECAT_API_KEY.trim()
        if (apiKey.isEmpty() || Purchases.isConfigured) return

        if (BuildConfig.DEBUG) {
            Purchases.logLevel = LogLevel.DEBUG
        }

        Purchases.configure(
            PurchasesConfiguration.Builder(application, apiKey)
                .appUserID(null)
                .build()
        )
    }
}
