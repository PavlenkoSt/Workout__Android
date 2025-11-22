package com.learning.workout__android.ui.screens.records

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learning.workout__android.ui.theme.Workout__AndroidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsHeader() {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset((-2).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Exercise",
                    modifier = Modifier.weight(1f),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Result",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Date",
                    modifier = Modifier
                        .weight(1f),
                    textAlign = TextAlign.End,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        actions = {
            Spacer(modifier = Modifier.width(12.dp))
        }
    )

}

@Composable
@Preview(showBackground = true)
private fun RecordsHeaderPreview() {
    Workout__AndroidTheme {
        RecordsHeader(
        )
    }
}