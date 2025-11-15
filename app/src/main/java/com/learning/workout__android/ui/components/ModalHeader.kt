package com.learning.workout__android.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ModalHeader(
    text: String
) {
    Text(
        text = text,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(12.dp))
}