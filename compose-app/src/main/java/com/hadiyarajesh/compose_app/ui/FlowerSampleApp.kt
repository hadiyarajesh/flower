package com.hadiyarajesh.compose_app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.ExperimentalPagingApi
import com.hadiyarajesh.compose_app.ui.navigation.FlowerSampleNavigation
import com.hadiyarajesh.compose_app.ui.theme.FlowerSampleTheme

@ExperimentalPagingApi
@Composable
fun FlowerSampleApp() {
    FlowerSampleTheme {
        val navController = rememberNavController()
        FlowerSampleNavigation(navController = navController)
    }
}
