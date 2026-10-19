package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.OceanCommandApp
import com.example.ui.OceanViewModel
import com.example.ui.theme.OceanCommandTheme

class MainActivity : ComponentActivity() {
    private val oceanViewModel: OceanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OceanCommandTheme {
                OceanCommandApp(viewModel = oceanViewModel)
            }
        }
    }
}
