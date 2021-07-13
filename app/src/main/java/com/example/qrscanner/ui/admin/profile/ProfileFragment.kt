package com.example.qrscanner.ui.admin.profile

import android.animation.Animator
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.qrscanner.R
import com.example.qrscanner.ui.admin.zoom.ZoomActivity
import com.example.qrscanner.data.api.models.profile.ProfileModel
import com.example.qrscanner.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private var shortAnimationDuration: Int = 0
    private var currentAnimator: Animator? = null

    companion object {
        fun newInstance() = ProfileFragment()
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
        val profile = arguments?.getSerializable("profileData") as ProfileModel?
        binding.profileData = profile
        Glide.with(requireActivity()).load(profile?.avatar).override(600, 600).into(binding.photoProfile)
        if (profile?.avatar != null) {
            binding.photoProfile.setOnClickListener {
                val intent = Intent(requireContext(), ZoomActivity::class.java)
                intent.putExtra("photoData", profile.avatar)
                startActivity(intent)
            }
        }
    }


}