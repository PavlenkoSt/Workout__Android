package com.learning.workout__android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissItemBackground(dismissDirection: SwipeToDismissBoxValue) {
    when (dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit exercise",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(ShapeDefaults.Medium)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 24.dp)
                    .wrapContentSize(Alignment.CenterStart),
                tint = Color.White
            )
        }

        SwipeToDismissBoxValue.EndToStart -> {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove exercise",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(ShapeDefaults.Medium)
                    .background(Color.Red)
                    .padding(horizontal = 24.dp)
                    .wrapContentSize(Alignment.CenterEnd),
                tint = Color.White
            )
        }

        SwipeToDismissBoxValue.Settled -> {}
    }
}