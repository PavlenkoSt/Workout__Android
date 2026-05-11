package com.stanislav_pav.repstation

import android.app.Application
import com.stanislav_pav.repstation.monetization.RevenueCatInitializer

class RepStationApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RevenueCatInitializer.configure(this)
    }
}
