package com.hadiyarajesh.flowersample.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import com.hadiyarajesh.flowersample.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private var anim: Animator? = null
    private val viewModel: MainActivityViewModel by viewModel()

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.currentPageNo.observe(this, Observer {
            currentPageNoTv.text = it.toString()
        })
        viewModel.events.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is MainActivityViewModel.Event.Error -> {
                    showError(it.message)
                }
            }
        }.launchIn(lifecycleScope)
        viewModel.quotes.observe(this, Observer {
            when (it) {
                is MainActivityViewModel.State.LoadingState -> {
                    quoteCard.hide { animator -> updateQuoteCardAnimator(animator) }
                    progressBar.show()
                    nextBtn.isEnabled = false
                    prevBtn.isEnabled = false
                }
                is MainActivityViewModel.State.UIState -> {
                    progressBar.hide()
                    showError("")
                    quoteCard.show { animator -> updateQuoteCardAnimator(animator) }

                    nextBtn.isEnabled = true
                    prevBtn.isEnabled = it.currentPage > 1

                    quoteTv.text = Html.fromHtml(it.quote.quote)
                    quoteAuthorTv.text = it.quote.title
                }
                is MainActivityViewModel.State.ErrorState -> {
                    progressBar.hide()
                    quoteCard.hide { animator -> updateQuoteCardAnimator(animator) }
                    nextBtn.isEnabled = true
                    prevBtn.isEnabled = true
                    showError(it.errorMessage)
                }
                else -> {
                }
            }
        })
    }

    fun updateQuoteCardAnimator(animator: Animator?) {
        anim = animator
    }

    override fun onPause() {
        super.onPause()
        anim?.cancel()
    }

    fun View.hide() {
        this.visibility = View.INVISIBLE
    }

    fun View.show() {
        this.visibility = View.VISIBLE
    }

    fun CircularRevealCardView.hide(updateCurrentAnimator:(Animator?) -> Unit) {
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

    fun CircularRevealCardView.show(updateCurrentAnimator:(Animator?) -> Unit) {
        this.post {
            // Check if the runtime version is at least Lollipop
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
            } else {
                // set the view to invisible without a circular reveal animation below Lollipop
                this@show.visibility = View.INVISIBLE
            }
        }
    }

    private fun showError(msg: String) {
        errorMessageTv.visibility = View.VISIBLE
        errorMessageTv.text = msg
    }

    fun nextPage(view: View) {
        viewModel.currentPageNo.value?.plus(1)?.let { viewModel.changePage(it) }
    }

    fun prevPage(view: View) {
        viewModel.currentPageNo.value?.minus(1)?.let { viewModel.changePage(it) }
    }
}
