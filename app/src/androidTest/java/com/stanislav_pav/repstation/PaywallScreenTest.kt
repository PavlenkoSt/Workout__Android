package com.stanislav_pav.repstation

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.stanislav_pav.repstation.monetization.MonetizationState
import com.stanislav_pav.repstation.monetization.StoreProduct
import com.stanislav_pav.repstation.ui.PaywallScreen
import com.stanislav_pav.repstation.ui.theme.RepStationTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PaywallScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test fun unavailablePriceDisablesBuy() {
        render(MonetizationState(isLoading = false))
        compose.onNodeWithTag("buy_pro").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Restore purchases").performScrollTo().assertIsEnabled()
    }

    @Test fun pendingPaymentDisablesBuy() {
        render(MonetizationState(isLoading = false, isPending = true, product = product))
        compose.onNodeWithTag("buy_pro").performScrollTo().assertIsNotEnabled()
    }

    @Test fun localizedPriceLaunchesPurchase() {
        var purchased = false
        render(MonetizationState(isLoading = false, product = product), buy = { purchased = true })
        compose.onNodeWithText("Unlock Pro · €4.99").performScrollTo().performClick()
        assertTrue(purchased)
    }

    @Test fun errorExposesRetry() {
        var retried = false
        render(MonetizationState(isLoading = false, message = "Offline"), retry = { retried = true })
        compose.onNodeWithText("Retry").performScrollTo().performClick()
        assertTrue(retried)
    }

    private fun render(state: MonetizationState, buy: () -> Unit = {}, retry: () -> Unit = {}) {
        compose.setContent {
            RepStationTheme {
                PaywallScreen(state, false, {}, buy, {}, retry, {})
            }
        }
    }

    private val product = StoreProduct("lifetime", "€4.99", "offer")
}
