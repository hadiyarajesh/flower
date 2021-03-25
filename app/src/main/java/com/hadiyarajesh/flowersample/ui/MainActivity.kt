package com.hadiyarajesh.flowersample.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import com.hadiyarajesh.flower.Resource
import com.hadiyarajesh.flowersample.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModel()
    
    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.randomQuote.observe(this, {
            when (it.status) {
                Resource.Status.LOADING -> {
                    showLoading()
                }
                Resource.Status.SUCCESS -> {
                    hideLoading()
                    quoteTitle.text = it.data?.quote?.let { it1 -> HtmlCompat.fromHtml(it1,0) }
                }
                Resource.Status.ERROR -> {
                    hideLoading()
                    showError(it.message!!)
                }
            }
        })
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    private fun showError(msg: String) {
        errorMessage.visibility = View.VISIBLE
        errorMessage.text = msg
    }
}