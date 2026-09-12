package com.stanislav_pav.repstation.monetization

import android.app.Activity
import android.content.Context
import androidx.core.content.edit
import com.stanislav_pav.repstation.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PlayBillingRepository(context: Context) {
    // Application lifetime: purchase completion must survive paywall dismissal and recreation.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val store: GooglePlayStore = GooglePlayStore(context, BuildConfig.PLAY_PRO_PRODUCT_ID) { update ->
        scope.launch { manager.onPurchasesUpdated(update) }
    }
    private val manager: PurchaseManager = PurchaseManager(
        store, PreferencesAccessCache(context), BuildConfig.PLAY_PRO_PRODUCT_ID, BuildConfig.PRO_UNLOCK_CODE
    )
    val state = manager.state

    fun refresh(restoring: Boolean = false) { scope.launch { manager.refresh(restoring) } }
    fun purchase(activity: Activity) { scope.launch { manager.purchase { store.launchPurchase(activity, it) } } }
    fun unlockWithCode(code: String) = manager.unlockWithCode(code)
    fun clearMessage() = manager.clearMessage()
}

private class PreferencesAccessCache(context: Context) : AccessCache {
    private val paid = context.applicationContext.getSharedPreferences("play_billing_cache", Context.MODE_PRIVATE)
    // Preserve the existing private access-code unlock through this migration.
    private val local = context.applicationContext.getSharedPreferences("monetization_prefs", Context.MODE_PRIVATE)
    override var purchased: Boolean
        get() = paid.getBoolean("pro_purchased", false)
        set(value) { paid.edit { putBoolean("pro_purchased", value) } }
    override var localUnlocked: Boolean
        get() = local.getBoolean("local_pro_unlocked", false)
        set(value) { local.edit { putBoolean("local_pro_unlocked", value) } }
}
