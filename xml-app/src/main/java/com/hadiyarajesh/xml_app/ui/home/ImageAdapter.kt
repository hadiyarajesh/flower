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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
import coil.transform.RoundedCornersTransformation
import com.hadiyarajesh.xml_app.database.entity.Image
import com.hadiyarajesh.xml_app.databinding.ImageItemBinding

class ImageAdapter : ListAdapter<Image, ImageAdapter.ImageViewHolder>(ImageDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = getItem(position)
        holder.bind(image)
    }

    class ImageViewHolder(
        private val binding: ImageItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setClickListener { view ->
                binding.image?.let { image ->
                    navigateToImageDetails(image, view)
                }
            }
        }

        private fun navigateToImageDetails(image: Image, view: View) {
            val direction =
                HomeScreenFragmentDirections.actionImageListFragmentToImageDetailsFragment(
                    imageId = image.id
                )
            view.findNavController().navigate(direction)
        }

        fun bind(item: Image) {
            binding.apply {
                image = item

                val circularProgressDrawable = CircularProgressDrawable(itemView.context)
                circularProgressDrawable.strokeWidth = 8f
                circularProgressDrawable.centerRadius = 30f
                circularProgressDrawable.start()

                binding.wallpaperImage.load(item.downloadUrl) {
                    crossfade(300)
                    placeholder(circularProgressDrawable)
                    transformations(RoundedCornersTransformation(4f))
                }
                executePendingBindings()
            }
        }
    }
}

private class ImageDiffCallback : DiffUtil.ItemCallback<Image>() {
    override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem == newItem
    }
}
