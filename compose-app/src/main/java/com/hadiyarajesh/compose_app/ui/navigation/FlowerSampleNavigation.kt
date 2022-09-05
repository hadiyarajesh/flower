/*
 *  Copyright (C) 2022 Rajesh Hadiya
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.hadiyarajesh.compose_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hadiyarajesh.compose_app.ui.detail.ImageDetailsScreen
import com.hadiyarajesh.compose_app.ui.detail.ImageDetailsViewModel
import com.hadiyarajesh.compose_app.ui.home.HomeScreen
import com.hadiyarajesh.compose_app.ui.home.HomeViewModel

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

        composable(
            route = Screens.ImageDetails.route + "/{imageId}",
            arguments = listOf(
                navArgument(name = "imageId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val imageDetailsViewModel = hiltViewModel<ImageDetailsViewModel>()

            ImageDetailsScreen(
                navController = navController,
                imageDetailsViewModel = imageDetailsViewModel,
                imageId = backStackEntry.arguments?.getLong("imageId")
            )
        }
    }
}
