package com.stanislav_pav.repstation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.ui.revenuecatui.Paywall
import com.revenuecat.purchases.ui.revenuecatui.PaywallListener
import com.revenuecat.purchases.ui.revenuecatui.PaywallOptions
import com.stanislav_pav.repstation.monetization.MonetizationConfig
import com.stanislav_pav.repstation.monetization.RevenueCatBillingRepository
import com.stanislav_pav.repstation.ui.theme.RepStationTheme

class PaywallHostActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Purchases.isConfigured) {
            finish()
            return
        }

        enableEdgeToEdge()
        setContent {
            RepStationTheme {
                PaywallHostContent(onClose = { finish() })
            }
        }
    }
}

@Composable
private fun PaywallHostContent(onClose: () -> Unit) {
    val context = LocalContext.current
    var showAccessCodeDialog by remember { mutableStateOf(false) }
    var accessCode by remember { mutableStateOf("") }
    var accessCodeError by remember { mutableStateOf<String?>(null) }
    val paywallOptions = remember(onClose) {
        PaywallOptions.Builder(dismissRequest = onClose)
            .setListener(object : PaywallListener {
                override fun onPurchaseCompleted(
                    customerInfo: CustomerInfo,
                    storeTransaction: StoreTransaction
                ) {
                    if (customerInfo.hasProEntitlement()) {
                        onClose()
                    }
                }

                override fun onRestoreCompleted(customerInfo: CustomerInfo) {
                    if (customerInfo.hasProEntitlement()) {
                        onClose()
                    }
                }
            })
            .build()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Paywall(options = paywallOptions)

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(top = 4.dp, end = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.45f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        TextButton(
            onClick = {
                accessCode = ""
                accessCodeError = null
                showAccessCodeDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            Text("Access code")
        }
    }

    if (showAccessCodeDialog) {
        AlertDialog(
            onDismissRequest = { showAccessCodeDialog = false },
            title = { Text("Access code") },
            text = {
                TextField(
                    value = accessCode,
                    onValueChange = {
                        accessCode = it
                        accessCodeError = null
                    },
                    label = { Text("Code") },
                    isError = accessCodeError != null,
                    supportingText = {
                        accessCodeError?.let { Text(it) }
                    },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val unlocked = RevenueCatBillingRepository(context).unlockWithCode(accessCode)
                        if (unlocked) {
                            showAccessCodeDialog = false
                            onClose()
                        } else {
                            accessCodeError = "Invalid code"
                        }
                    }
                ) {
                    Text("Unlock")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAccessCodeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun CustomerInfo.hasProEntitlement(): Boolean {
    return entitlements.get(MonetizationConfig.PRO_ENTITLEMENT_ID)?.isActive == true
}
