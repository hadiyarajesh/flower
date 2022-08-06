package com.hadiyarajesh.compose_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.paging.ExperimentalPagingApi
import com.hadiyarajesh.compose_app.ui.home.HomeScreen
import com.hadiyarajesh.compose_app.viewmodel.HomeViewModel

@ExperimentalPagingApi
@Composable
fun FlowerSampleNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screens.Home.route
    ) {
        composable(route = Screens.Home.route) {
            val homViewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(
                navController = navController,
                homeViewModel = homViewModel
            )
        }
    }
}
