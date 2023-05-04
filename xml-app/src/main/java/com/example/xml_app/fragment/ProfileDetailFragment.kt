package com.example.xml_app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.xml_app.ProfileDetailViewModel
import com.example.xml_app.R
import com.example.xml_app.databinding.FragmentProfileDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDetailFragment : Fragment(R.layout.fragment_profile_detail) {

   private val args: ProfileDetailFragmentArgs by navArgs()

    private val viewModel: ProfileDetailViewModel by viewModels()

    private lateinit var lBinding: FragmentProfileDetailBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lBinding = FragmentProfileDetailBinding.bind(view)
    }


}