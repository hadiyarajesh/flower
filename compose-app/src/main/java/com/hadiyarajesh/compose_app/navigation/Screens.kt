/*
 *  Copyright (C) 2023 Rajesh Hadiya
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

package com.hadiyarajesh.compose_app.navigation

import androidx.annotation.DrawableRes
import com.hadiyarajesh.compose_app.R

sealed class Screens(
    val route: String,
    @DrawableRes val icon: Int
) {
    data object Home : Screens(
        route = "Home",
        icon = R.drawable.ic_outlined_home
    )

    data object ImageDetails : Screens(
        route = "ImageDetails",
        icon = R.drawable.ic_outlined_image
    )

    fun withArgs(vararg args: Any): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
