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

package com.example.xml_app.fragment.home

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
import com.example.xml_app.R
import com.example.xml_app.databinding.FragmentProfileListBinding
import com.example.xml_app.fragment.ProfileAdapter
import com.example.xml_app.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileListFragment : Fragment() {

    private val viewModel: ProfileListViewModel by viewModels()

    private lateinit var binding: FragmentProfileListBinding

    lateinit var profileAdapter: ProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        setupSwipeRefreshListener()

        fetchData()
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
                viewModel.images.collectLatest { profileListState ->
                    when (profileListState) {
                        is UiState.Loading -> {
                            showProgressBar()
                        }

                        is UiState.Success -> {
                            hideProgressBar()
                            profileListState.data?.let {
                                profileAdapter.submitList(it)
                            }
                        }

                        UiState.Empty -> {
                            showNoUserInfoFound("") // initial state is set to empty.
                        }

                        is UiState.Error -> {
                            showNoUserInfoFound(getString(R.string.loading_error_msg))
                        }

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
        binding.swipeRefreshLayout.visibility = View.GONE
        binding.noInfoMsg.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.swipeRefreshLayout.visibility = View.VISIBLE
        binding.noInfoMsg.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun showNoUserInfoFound(message: String) {
        binding.swipeRefreshLayout.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.tvErrMsg.text = message
        binding.noInfoMsg.visibility = View.VISIBLE
    }

    private fun setUpRecyclerView() {
        profileAdapter = ProfileAdapter()
        binding.recyclerView.adapter = profileAdapter
    }

}
