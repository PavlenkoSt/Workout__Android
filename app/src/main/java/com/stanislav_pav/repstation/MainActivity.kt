package com.stanislav_pav.repstation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.stanislav_pav.repstation.ui.PaywallHostActivity
import com.stanislav_pav.repstation.ui.theme.RepStationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RepStationTheme {
                App(
                    presentPaywall = {
                        startActivity(Intent(this, PaywallHostActivity::class.java))
                    }
                )
            }
        }
    }
}
