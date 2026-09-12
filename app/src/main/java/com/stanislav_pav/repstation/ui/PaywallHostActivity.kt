package com.stanislav_pav.repstation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stanislav_pav.repstation.BuildConfig
import com.stanislav_pav.repstation.R
import com.stanislav_pav.repstation.monetization.MonetizationState
import com.stanislav_pav.repstation.monetization.StoreProduct
import com.stanislav_pav.repstation.ui.theme.RepStationTheme
import com.stanislav_pav.repstation.viewModel.MonetizationViewModel

class PaywallHostActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RepStationTheme {
                val model: MonetizationViewModel = viewModel(factory = MonetizationViewModel.provideFactory(this))
                val state by model.uiState.collectAsState()
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner, model) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) model.refresh()
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                }
                var showCode by rememberSaveable { mutableStateOf(false) }
                PaywallScreen(
                    state = state,
                    accessCodeEnabled = BuildConfig.PRO_UNLOCK_CODE.isNotBlank(),
                    onClose = { finish() },
                    onPurchase = { model.purchase(this) },
                    onRestore = model::restore,
                    onRetry = model::refresh,
                    onAccessCode = { showCode = true }
                )
                if (showCode) {
                    AccessCodeDialog(
                        onDismiss = { showCode = false },
                        onUnlock = { code -> model.unlockWithCode(code).also { if (it) showCode = false } }
                    )
                }
            }
        }
    }
}

@Composable
internal fun PaywallScreen(
    state: MonetizationState,
    accessCodeEnabled: Boolean,
    onClose: () -> Unit,
    onPurchase: () -> Unit,
    onRestore: () -> Unit,
    onRetry: () -> Unit,
    onAccessCode: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val uriHandler = LocalUriHandler.current
    val privacyUrl = stringResource(R.string.pro_privacy_url)
    val termsUrl = stringResource(R.string.pro_terms_url)
    val busy = state.isLoading || state.isPurchasing || state.isRestoring
    Surface(color = colors.surface, modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .background(Brush.verticalGradient(listOf(colors.primaryContainer.copy(alpha = 0.5f), colors.surface)))
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, stringResource(R.string.pro_close))
                    }
                }
                Column(
                    modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(72.dp).background(colors.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FitnessCenter, null, tint = colors.onPrimaryContainer,
                            modifier = Modifier.size(36.dp))
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(stringResource(R.string.pro_badge), color = colors.primary,
                        style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Text(stringResource(if (state.isPro) R.string.pro_unlocked_title else R.string.pro_title),
                        style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center)
                    Spacer(Modifier.height(12.dp))
                    Text(stringResource(R.string.pro_subtitle), style = MaterialTheme.typography.bodyLarge,
                        color = colors.onSurfaceVariant, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(28.dp))
                    Surface(shape = RoundedCornerShape(24.dp), color = colors.surfaceContainerLow) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            ProBenefit(stringResource(R.string.pro_presets_title), stringResource(R.string.pro_presets_detail))
                            ProBenefit(stringResource(R.string.pro_goals_title), stringResource(R.string.pro_goals_detail))
                            ProBenefit(stringResource(R.string.pro_records_title), stringResource(R.string.pro_records_detail))
                            ProBenefit(stringResource(R.string.pro_stats_title), stringResource(R.string.pro_stats_detail))
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    if (!state.isPro) {
                        Text(stringResource(R.string.pro_lifetime), style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            state.product?.formattedPrice ?: stringResource(
                                if (state.isLoading) R.string.pro_loading_price else R.string.pro_price_unavailable),
                            style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(stringResource(R.string.pro_no_subscription), style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant)
                        Spacer(Modifier.height(20.dp))
                    }
                    if (state.isPending) {
                        Text(stringResource(R.string.pro_pending), color = colors.primary,
                            textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 16.dp))
                    }
                    state.message?.let { message ->
                        Surface(shape = RoundedCornerShape(16.dp), color = colors.surfaceContainerHigh) {
                            Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(message, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                                TextButton(onClick = onRetry, enabled = !busy) { Text(stringResource(R.string.pro_retry)) }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                    if (state.isPro) {
                        Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.pro_continue), modifier = Modifier.padding(vertical = 8.dp))
                        }
                    } else {
                        Button(
                            onClick = onPurchase,
                            enabled = !busy && !state.isPending && state.product != null,
                            modifier = Modifier.fillMaxWidth().testTag("buy_pro")
                        ) {
                            if (state.isPurchasing) {
                                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.width(10.dp))
                            }
                            Text(
                                when {
                                    state.isPurchasing -> stringResource(R.string.pro_processing)
                                    state.isPending -> stringResource(R.string.pro_payment_pending)
                                    state.product != null -> stringResource(R.string.pro_buy_price, state.product.formattedPrice)
                                    else -> stringResource(R.string.pro_buy)
                                },
                                modifier = Modifier.padding(vertical = 8.dp), textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.pro_google_play), style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant, textAlign = TextAlign.Center)
                    TextButton(onClick = onRestore, enabled = !busy) {
                        Text(stringResource(if (state.isRestoring) R.string.pro_restoring else R.string.pro_restore))
                    }
                    Row(horizontalArrangement = Arrangement.Center) {
                        TextButton(onClick = { uriHandler.openUri(privacyUrl) }) {
                            Text(stringResource(R.string.pro_privacy))
                        }
                        TextButton(onClick = { uriHandler.openUri(termsUrl) }) {
                            Text(stringResource(R.string.pro_terms))
                        }
                    }
                    if (accessCodeEnabled) {
                        TextButton(onClick = onAccessCode, enabled = !state.isPurchasing) {
                            Text(stringResource(R.string.pro_access_code))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProBenefit(title: String, detail: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(3.dp))
            Text(detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AccessCodeDialog(onDismiss: () -> Unit, onUnlock: (String) -> Boolean) {
    var code by rememberSaveable { mutableStateOf("") }
    var invalid by rememberSaveable { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.pro_access_code)) },
        text = {
            OutlinedTextField(value = code, onValueChange = { code = it; invalid = false },
                label = { Text(stringResource(R.string.pro_code_label)) }, singleLine = true, isError = invalid,
                supportingText = { if (invalid) Text(stringResource(R.string.pro_invalid_code)) })
        },
        confirmButton = {
            TextButton(onClick = { invalid = !onUnlock(code) }, enabled = code.isNotBlank()) {
                Text(stringResource(R.string.pro_unlock))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.pro_cancel)) } }
    )
}

@Preview(showBackground = true)
@Composable
private fun PaywallPreview() {
    RepStationTheme(dynamicColor = false) {
        PaywallScreen(MonetizationState(isLoading = false,
            product = StoreProduct("preview", "$4.99", "")), true, {}, {}, {}, {}, {})
    }
}
