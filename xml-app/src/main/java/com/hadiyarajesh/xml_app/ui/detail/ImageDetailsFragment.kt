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

package com.hadiyarajesh.xml_app.ui.detail

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.RoundedCornersTransformation
import com.hadiyarajesh.xml_app.R
import com.hadiyarajesh.xml_app.database.entity.Image
import com.hadiyarajesh.xml_app.databinding.FragmentImageDetailsBinding
import com.hadiyarajesh.xml_app.util.LoadResourceFrom
import com.hadiyarajesh.xml_app.util.UiState
import com.hadiyarajesh.xml_app.util.debugLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ImageDetailsFragment : Fragment() {
    private val args: ImageDetailsFragmentArgs by navArgs()
    private val viewModel: ImageDetailsViewModel by viewModels()
    private lateinit var binding: FragmentImageDetailsBinding
    private lateinit var image: Image
    private var isNetworkLoad: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_image_details, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageId = args.imageId.trim().toLong()
        getImageDetails(imageId)
        initClickListener()
    }

    override fun onResume() {
        super.onResume()
        updateNetworkLoadBtnVisibility()
    }

    private fun initClickListener() {
        binding.loadFromNetwork.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    isNetworkLoad = true
                    viewModel.getImage(
                        image.id.toLong(),
                        loadFrom = LoadResourceFrom.Network
                    )
                }
            }
        }

        binding.imageUrl.highlightColor = Color.BLUE
        binding.imageUrl.setOnClickListener { imageUrl ->
            try {
                val openURL = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(imageUrl.tag.toString())
                }
                startActivity(openURL)
            } catch (e: Exception) {
                debugLog("Something went wrong while opening image url", e)
            }
        }
    }

    private fun getImageDetails(imageId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                isNetworkLoad = false
                viewModel.getImage(imageId, loadFrom = LoadResourceFrom.Db)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.image.collectLatest { imageState ->
                    when (imageState) {
                        is UiState.Loading -> {
                            showProgressBar()
                        }

                        is UiState.Success -> {
                            image = imageState.data
                            updateImageUI()
                        }

                        UiState.Empty -> {}

                        is UiState.Error -> {
                            showNoUserInfoFound(getString(R.string.something_went_wrong))
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadFrom.collectLatest { loadingFrom ->
                    isNetworkLoad = when (loadingFrom) {
                        LoadResourceFrom.Db -> {
                            false
                        }

                        LoadResourceFrom.Network -> {
                            true
                        }
                    }
                    updateNetworkLoadBtnVisibility()
                }
            }
        }
    }

    private fun updateImageUI() {
        hideProgressBar()

        with(binding) {
            wallpaperImage.load(image.downloadUrl) {
                crossfade(300)
                transformations(RoundedCornersTransformation(4f))
            }
            authorName.text = image.author
            imageUrl.paint?.isUnderlineText = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                binding.imageUrl.paint.underlineColor = Color.BLUE
            }
            imageUrl.text = image.url
            imageUrl.tag = image.url
        }

        updateNetworkLoadBtnVisibility()
    }

    private fun updateNetworkLoadBtnVisibility() {
        binding.imageDestInfo.visibility = if (isNetworkLoad) View.GONE else View.VISIBLE
    }

    private fun showProgressBar() {
        with(binding) {
            imageDetailsScreen.visibility = View.GONE
            noInfoMsg.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun hideProgressBar() {
        with(binding) {
            imageDetailsScreen.visibility = View.VISIBLE
            noInfoMsg.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun showNoUserInfoFound(message: String) {
        with(binding) {
            imageDetailsScreen.visibility = View.GONE
            progressBar.visibility = View.GONE
            tvErrMsg.text = message
            noInfoMsg.visibility = View.VISIBLE
        }
    }
}