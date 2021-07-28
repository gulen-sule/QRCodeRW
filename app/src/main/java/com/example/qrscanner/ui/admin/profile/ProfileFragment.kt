package com.example.qrscanner.ui.admin.profile

import android.animation.Animator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.qrscanner.R
import com.example.qrscanner.data.api.models.profile.ProfileModel
import com.example.qrscanner.databinding.FragmentProfileBinding
import com.example.qrscanner.ui.admin.zoom.ZoomActivity
import com.squareup.picasso.Picasso

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
        profile?.let { Log.d("TAGidNumber", it.idNumber) }
        binding.profileData = profile
        Log.e("TAG", profile?.avatar.toString())
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

//        }
//        binding.floatingActionButton.setOnClickListener {
//            requireActivity().onBackPressed()
//        }
    }


}