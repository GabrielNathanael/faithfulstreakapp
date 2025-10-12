package com.faithfulstreak.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.faithfulstreak.app.v1.ui.screen.AppNavHost
import com.faithfulstreak.app.v1.ui.theme.FaithTheme
import com.faithfulstreak.app.v1.viewmodel.StreakViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FaithTheme {
                val nav = rememberNavController()
                val vm: StreakViewModel = viewModel(factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application))
                AppNavHost(nav = nav, vm = vm)
            }
        }
    }
}