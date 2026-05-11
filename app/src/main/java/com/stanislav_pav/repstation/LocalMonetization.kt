package com.stanislav_pav.repstation

import androidx.compose.runtime.compositionLocalOf
import com.stanislav_pav.repstation.monetization.MonetizationState

val LocalMonetizationState = compositionLocalOf { MonetizationState(isLoading = false) }
