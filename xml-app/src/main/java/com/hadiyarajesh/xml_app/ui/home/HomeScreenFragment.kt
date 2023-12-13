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

package com.hadiyarajesh.xml_app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hadiyarajesh.xml_app.R
import com.hadiyarajesh.xml_app.databinding.FragmentImageListBinding
import com.hadiyarajesh.xml_app.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeScreenFragment : Fragment() {
    private lateinit var binding: FragmentImageListBinding
    private lateinit var imageAdapter: ImageAdapter
    private val viewModel: HomeScreenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_image_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        setupSwipeRefreshListener()
        fetchData()
    }

    private fun setUpRecyclerView() {
        imageAdapter = ImageAdapter()
        binding.recyclerView.adapter = imageAdapter
    }

    private fun setupSwipeRefreshListener() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            viewModel.refreshImages()
        }
    }

    private fun fetchData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.images.collectLatest { imagesState ->
                    when (imagesState) {
                        is UiState.Loading -> {
                            showProgressBar()
                        }

                        is UiState.Success -> {
                            hideProgressBar()
                            imageAdapter.submitList(imagesState.data)
                        }

                        is UiState.Error -> {
                            showErrorUi(getString(R.string.something_went_wrong))
                        }

                        is UiState.Empty -> {}
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collectLatest { loadingState ->
                    binding.swipeRefreshLayout.isRefreshing = loadingState
                }
            }
        }
    }

    private fun showProgressBar() {
        with(binding) {
            swipeRefreshLayout.visibility = View.GONE
            noInfoMsg.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun hideProgressBar() {
        with(binding) {
            swipeRefreshLayout.visibility = View.VISIBLE
            noInfoMsg.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun showErrorUi(message: String) {
        with(binding) {
            swipeRefreshLayout.visibility = View.GONE
            progressBar.visibility = View.GONE
            tvErrMsg.text = message
            noInfoMsg.visibility = View.VISIBLE
        }
    }
}
