package com.example.xml_app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.xml_app.ProfileListViewModel
import com.example.xml_app.R
import com.example.xml_app.databinding.FragmentProfileListBinding
import com.example.xml_app.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileListFragment : Fragment(R.layout.fragment_profile_list) {

    private val viewModel: ProfileListViewModel by viewModels()

    private lateinit var lBinding: FragmentProfileListBinding

   lateinit var profileAdapter: ProfileAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lBinding = FragmentProfileListBinding.bind(view)

        setUpRecyclerView()

        setupSwipeRefreshListener()

        fetchData()
    }

    private fun setupSwipeRefreshListener() {
        lBinding.swipeRefreshLayout.setOnRefreshListener {
            lBinding.swipeRefreshLayout.isRefreshing = false
            viewModel.refreshImages()

        }
    }

    private fun fetchData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.images.collectLatest { profileListState ->
                    when(profileListState) {
                        is UiState.Loading -> {
                            // Show progress bar..
                        }

                        is UiState.Success -> {
                            (profileListState as UiState.Success).data.let {
                                profileAdapter.submitList(it)
                            }
                        }

                        UiState.Empty -> {

                        }
                        is UiState.Error -> TODO()

                    }
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        profileAdapter = ProfileAdapter()
        lBinding.recyclerView.adapter = profileAdapter
    }

}
