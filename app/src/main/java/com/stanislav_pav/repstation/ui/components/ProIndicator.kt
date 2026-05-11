package com.stanislav_pav.repstation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stanislav_pav.repstation.LocalMonetizationState
import com.stanislav_pav.repstation.LocalPresentPaywall

@Composable
fun ProIndicator(modifier: Modifier = Modifier) {
    val monetization = LocalMonetizationState.current
    val presentPaywall = LocalPresentPaywall.current

    if (monetization.isLoading) return
    if (!monetization.isRevenueCatConfigured) return
    if (monetization.isPro) return

    Surface(
        modifier = modifier.clickable { presentPaywall() },
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 6.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Unlock Pro",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Unlock Pro",
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
    }
}
