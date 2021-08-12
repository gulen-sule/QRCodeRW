package com.example.qrscanner.ui.myAccount

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.qrscanner.Const
import com.example.qrscanner.applications.QrScannerApplication
import com.example.qrscanner.databinding.FragmentMyAccountBinding
import com.example.qrscanner.ui.login.LoginActivity

class MyAccountFragment() : Fragment() {

    private lateinit var binding: FragmentMyAccountBinding
    private var viewModel: MyAccountViewModel = MyAccountViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getMe { myProfile ->
            if (myProfile == null) {
                unAuthenticated()
            } else {
                binding.profileData = myProfile
                Glide.with(requireContext()).load(myProfile.avatar).into(binding.photoProfile)
                binding.photoProfile
            }
        }
        binding.cardViewLogOut.setOnClickListener {
            unAuthenticated()
        }
        binding.actionBtnClose.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun unAuthenticated() {
        val sharedPref: SharedPreferences? = requireContext().getSharedPreferences(Const.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPref?.edit {
            this.remove(Const.AUTH_TOKEN_NAME)
        }
        QrScannerApplication.getInstance().setToken("")
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

}