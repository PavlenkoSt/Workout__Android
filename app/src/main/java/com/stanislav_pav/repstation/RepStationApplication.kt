package com.stanislav_pav.repstation

import android.app.Application
import com.stanislav_pav.repstation.monetization.PlayBillingRepository

class RepStationApplication : Application() {
    val billingRepository by lazy { PlayBillingRepository(this) }
}
