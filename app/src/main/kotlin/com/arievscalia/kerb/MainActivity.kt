package com.arievscalia.kerb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.arievscalia.kerb.ui.navigation.KerbNavGraph
import com.arievscalia.kerb.ui.theme.KerbTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KerbTheme {
                val navController = rememberNavController()
                KerbNavGraph(navController)
            }
        }
    }
}
