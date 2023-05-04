package com.example.xml_app.fragment

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
import com.example.xml_app.database.entity.Image
import com.example.xml_app.databinding.ProfileItemBinding


class ProfileAdapter : ListAdapter<Image, ProfileAdapter.ProfileViewHolder>(ProfileDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        return ProfileViewHolder(
            ProfileItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        )
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val image = getItem(position)
        holder.bind(image)
    }


    class ProfileViewHolder(
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
                ProfileListFragmentDirections.actionProfileListFragmentToProfileDetailFragment(
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