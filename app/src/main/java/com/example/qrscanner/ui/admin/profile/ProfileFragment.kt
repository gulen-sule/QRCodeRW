package com.example.qrscanner.ui.admin.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.qrscanner.R
import com.example.qrscanner.data.api.models.profile.ProfileModel
import com.example.qrscanner.databinding.FragmentProfileBinding
import com.example.qrscanner.ui.admin.zoom.ZoomActivity

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private var profileModel: ProfileModel? = null
    private val viewModel = ProfileViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val token = arguments?.getSerializable("queryToken") as String?

        Log.d("profileTAG", token.toString())
        viewModel.getProfile(token) { profileModel ->
            binding.profileData = profileModel

            Glide.with(requireActivity()).load(profileModel?.avatar)
                .placeholder(R.drawable.image_placeholder)
                .override(600, 600).into(binding.photoProfile)
        }

        if (profileModel?.avatar != null) {
            binding.photoProfile.setOnClickListener {
                val intent = Intent(requireContext(), ZoomActivity::class.java)
                intent.putExtra("photoData", profileModel?.avatar)
                startActivity(intent)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }


}