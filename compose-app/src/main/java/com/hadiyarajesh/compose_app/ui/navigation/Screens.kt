package com.hadiyarajesh.compose_app.ui.navigation

import androidx.annotation.DrawableRes
import com.hadiyarajesh.compose_app.R

sealed class Screens(
    val route: String,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int
) {
    object Home : Screens(
        route = "Home",
        icon = R.drawable.ic_outlined_home,
        selectedIcon = R.drawable.ic_outlined_home
    )

    object Orders : Screens(
        route = "Orders",
        icon = R.drawable.ic_outlined_image,
        selectedIcon = R.drawable.ic_outlined_image
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
