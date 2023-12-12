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

package com.hadiyarajesh.xml_app.fragment

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
import com.hadiyarajesh.xml_app.databinding.ProfileItemBinding
import com.hadiyarajesh.xml_app.fragment.home.HomeScreenFragmentDirections


class ImageAdapter : ListAdapter<Image, ImageAdapter.ImageViewHolder>(ProfileDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ProfileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = getItem(position)
        holder.bind(image)
    }

    class ImageViewHolder(
        private val binding: ProfileItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setClickListener { view ->
                binding.profile?.let { profile ->
                    navigateToProfileDetails(profile, view)
                }
            }
        }

        private fun navigateToProfileDetails(profile: Image, view: View) {
            val direction =
                HomeScreenFragmentDirections.actionProfileListFragmentToProfileDetailFragment(
                    profileId = profile.id
                )
            view.findNavController().navigate(direction)
        }

        fun bind(item: Image) {
            binding.apply {
                profile = item

                val circularProgressDrawable = CircularProgressDrawable(itemView.context)
                circularProgressDrawable.strokeWidth = 8f
                circularProgressDrawable.centerRadius = 30f
                circularProgressDrawable.start()

                binding.profileImage.load(item.downloadUrl) {
                    crossfade(300)
                    placeholder(circularProgressDrawable)
                    transformations(RoundedCornersTransformation(4f))
                }
                executePendingBindings()
            }
        }
    }
}

private class ProfileDiffCallback : DiffUtil.ItemCallback<Image>() {
    override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem == newItem
    }
}
