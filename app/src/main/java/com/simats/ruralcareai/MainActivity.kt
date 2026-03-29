package com.simats.ruralcareai

import android.os.Bundle
import androidx.activity.viewModels
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.simats.ruralcareai.ui.RuralCareApp
import com.simats.ruralcareai.ui.theme.RuralCareAITheme
import com.simats.ruralcareai.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RuralCareAITheme {
                RuralCareApp(viewModel = mainViewModel)
            }
        }
    }
}