package com.hadiyarajesh.flowersample.ui

import android.animation.Animator
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.hadiyarajesh.flowersample.R
import com.hadiyarajesh.flowersample.extensions.hide
import com.hadiyarajesh.flowersample.extensions.show
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private var anim: Animator? = null
    private val viewModel: MainActivityViewModel by viewModel()

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.currentPageNo.observe(this, {
            currentPageNoTv.text = it.toString()
        })
        viewModel.events.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is MainActivityViewModel.Event.Error -> {
                    showError(it.message)
                }
            }
        }.launchIn(lifecycleScope)
        viewModel.quotes.observe(this, {
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

                    @Suppress("DEPRECATION")
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
