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

    fun setProfileModel(profile: ProfileModel?) {
        profileModel = profile
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var profile = arguments?.getSerializable("profileData") as ProfileModel?
        if (profile == null) {
            profile = profileModel
        }
        Log.d("profileTAG", profile?.name.toString())
        binding.profileData = profile
        Glide.with(requireActivity()).load(profile?.avatar)
            .placeholder(R.drawable.image_placeholder)
            .override(600, 600).into(binding.photoProfile)

        if (profile?.avatar != null) {
            binding.photoProfile.setOnClickListener {
                val intent = Intent(requireContext(), ZoomActivity::class.java)
                intent.putExtra("photoData", profile.avatar)
                startActivity(intent)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }


}