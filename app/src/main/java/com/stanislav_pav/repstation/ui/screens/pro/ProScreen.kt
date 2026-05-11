package com.stanislav_pav.repstation.ui.screens.pro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stanislav_pav.repstation.LocalSnackbarHostState
import com.stanislav_pav.repstation.monetization.MonetizationConfig
import com.stanislav_pav.repstation.utils.findActivity
import com.stanislav_pav.repstation.viewModel.MonetizationViewModel

@Composable
fun ProScreen(
    modifier: Modifier = Modifier,
    monetizationViewModel: MonetizationViewModel = viewModel(factory = MonetizationViewModel.provideFactory())
) {
    val ui by monetizationViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(ui.message) {
        ui.message?.let {
            snackbarHostState.showSnackbar(it)
            monetizationViewModel.clearMessage()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (ui.isPro) "RepStation Pro is active" else "RepStation Pro",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Unlock unlimited presets, goals, records, advanced statistics, and training history.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Free includes")
                Text("- ${MonetizationConfig.FREE_PRESET_LIMIT} presets")
                Text("- ${MonetizationConfig.FREE_GOAL_LIMIT} goals")
                Text("- ${MonetizationConfig.FREE_RECORD_LIMIT} records")
                Text("- Basic workout planning")
                Spacer(modifier = Modifier.height(12.dp))
                Text("Pro adds")
                Text("- Unlimited presets, goals, and records")
                Text("- Advanced workout statistics")
                Text("- Full training history")
                Text("- Future export/import tools")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            ui.isLoading -> CircularProgressIndicator()
            ui.isPro -> Text("Thanks for supporting the app.")
            !ui.isRevenueCatConfigured -> Text(
                text = "RevenueCat is not configured yet. Add REVENUECAT_API_KEY to Gradle properties before testing purchases.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )

            ui.packages.isEmpty() -> Text(
                text = "No Pro product is available yet. Create a lifetime package in RevenueCat and set it on the current offering.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )

            else -> {
                val packageToBuy = ui.packages.first()
                Button(
                    onClick = {
                        context.findActivity()?.let { activity ->
                            monetizationViewModel.purchase(activity, packageToBuy)
                        }
                    },
                    enabled = !ui.isPurchasing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (ui.isPurchasing) {
                            "Opening Google Play..."
                        } else {
                            "Unlock Pro - ${packageToBuy.product.price.formatted}"
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = { monetizationViewModel.restore() },
            enabled = !ui.isRestoring && ui.isRevenueCatConfigured
        ) {
            Text(if (ui.isRestoring) "Restoring..." else "Restore purchase")
        }
    }
}
