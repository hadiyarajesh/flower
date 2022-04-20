package com.hadiyarajesh.flowersample.ui

import android.animation.Animator
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.hadiyarajesh.flowersample.databinding.ActivityMainBinding
import com.hadiyarajesh.flowersample.extensions.hide
import com.hadiyarajesh.flowersample.extensions.show
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var anim: Animator? = null
    private val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.currentPageNo.observe(this) {
            binding.currentPageNoTv.text = it.toString()
        }

        viewModel.events.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is MainActivityViewModel.Event.Error -> {
                    showError(it.message)
                }
            }
        }.launchIn(lifecycleScope)

        viewModel.quotes.observe(this) {
            when (it) {
                is MainActivityViewModel.State.LoadingState -> {
                    binding.quoteCard.hide { animator -> updateQuoteCardAnimator(animator) }
                    binding.progressBar.show()
                    binding.nextBtn.isEnabled = false
                    binding.prevBtn.isEnabled = false
                }
                is MainActivityViewModel.State.UIState -> {
                    binding.progressBar.hide()
                    showError("")
                    binding.quoteCard.show { animator -> updateQuoteCardAnimator(animator) }
                    binding.nextBtn.isEnabled = true
                    binding.prevBtn.isEnabled = it.currentPage > 1

                    @Suppress("DEPRECATION")
                    binding.quoteTv.text = Html.fromHtml(it.quote.quote)
                    binding.quoteAuthorTv.text = it.quote.title
                }
                is MainActivityViewModel.State.ErrorState -> {
                    binding.progressBar.hide()
                    binding.quoteCard.hide { animator -> updateQuoteCardAnimator(animator) }
                    binding.nextBtn.isEnabled = true
                    binding.prevBtn.isEnabled = true
                    showError(it.errorMessage)
                }
                else -> {
                }
            }
        }
    }

    private fun updateQuoteCardAnimator(animator: Animator?) {
        anim = animator
    }

    override fun onPause() {
        super.onPause()
        anim?.cancel()
    }

    private fun showError(msg: String) {
        binding.errorMessageTv.visibility = View.VISIBLE
        binding.errorMessageTv.text = msg
    }

    fun nextPage(view: View) {
        viewModel.currentPageNo.value?.plus(1)?.let { viewModel.changePage(it) }
    }

    fun prevPage(view: View) {
        viewModel.currentPageNo.value?.minus(1)?.let { viewModel.changePage(it) }
    }
}
