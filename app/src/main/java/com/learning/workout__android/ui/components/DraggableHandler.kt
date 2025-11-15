package com.learning.workout__android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.learning.workout__android.R

@Composable
fun DraggableHandler(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(R.drawable.hand),
        contentDescription = "Handle",
        tint = Color.Gray.copy(alpha = 0.6f),
        modifier = modifier
            .padding(vertical = 8.dp)
            .size(24.dp)
            .clip(ShapeDefaults.Large)
            .background(MaterialTheme.colorScheme.surface)
    )
}
