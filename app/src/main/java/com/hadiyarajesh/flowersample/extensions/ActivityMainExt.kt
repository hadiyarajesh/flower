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

package com.hadiyarajesh.flowersample.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import com.hadiyarajesh.flower_core.Resource
import com.hadiyarajesh.flowersample.ui.MainActivityViewModel
import kotlinx.coroutines.flow.Flow

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun CircularRevealCardView.hide(updateCurrentAnimator: (Animator?) -> Unit) {
    this.post {
        // Check if the runtime version is at least Lollipop
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            val cx = this@hide.width / 2
            val cy = this@hide.height / 2

            // get the initial radius for the clipping circle
            val initialRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

            // create the animation (the final radius is zero)
            val anim = ViewAnimationUtils.createCircularReveal(this@hide, cx, cy, initialRadius, 0f)
            updateCurrentAnimator(anim)

            // make the view invisible when the animation is done
            anim?.addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    this@hide.visibility = View.INVISIBLE
                }
            })

            // start the animation
            anim?.start()
        } else {
            // set the view to visible without a circular reveal animation below Lollipop
            this@hide.visibility = View.VISIBLE
        }
    }
}

fun CircularRevealCardView.show(updateCurrentAnimator: (Animator?) -> Unit) {
    this.post {
        // Check if the runtime version is at least Lollipop
        // get the center for the clipping circle
        val cx = this@show.width / 2
        val cy = this@show.height / 2

        // get the final radius for the clipping circle
        val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

        // create the animator for this view (the start radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(this@show, cx, cy, 0f, finalRadius)
        updateCurrentAnimator(anim)
        // make the view visible and start the animation
        this@show.visibility = View.VISIBLE
        anim?.start()
    }
}

suspend fun <T> Flow<Resource<T>>.foldApiStates(
    onSuccess: suspend (T) -> Unit,
    onLoading: suspend (MainActivityViewModel.State.LoadingState) -> Unit,
    onError: suspend (MainActivityViewModel.State.ErrorState) -> Unit
) {
    this.collect { resource: Resource<T> ->
        when (resource.status) {
            is Resource.Status.Loading -> {
                onLoading(MainActivityViewModel.State.LoadingState())
            }

            is Resource.Status.Success -> {
                onSuccess((resource.status as Resource.Status.Success).data)
                MainActivityViewModel.State.SuccessState(resource)
            }

            is Resource.Status.EmptySuccess -> {
                MainActivityViewModel.State.SuccessState(resource)
            }
            
            is Resource.Status.Error -> {
                val error = resource.status as Resource.Status.Error
                onError(MainActivityViewModel.State.ErrorState(error.message, error.statusCode))
            }
        }
    }
}
