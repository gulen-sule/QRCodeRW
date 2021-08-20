package com.example.qrscanner.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.qrscanner.R
import com.example.qrscanner.databinding.FragmentHomeBinding
import com.example.qrscanner.ui.executive.AdminActivity
import com.example.qrscanner.ui.myAccount.MyAccountFragment
import com.example.qrscanner.ui.user.UserActivity

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private var myAccountFragment: MyAccountFragment? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    private fun setActions() {
        binding.buttonController.setOnClickListener {
            val intent = Intent(requireContext(), AdminActivity::class.java)
            startActivity(intent)
        }
        binding.buttonUser.setOnClickListener {
            val intent = Intent(requireContext(), UserActivity::class.java)
            startActivity(intent)
        }
        binding.profileButton.setOnClickListener {
            beginTransactionProfile()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActions()
    }

    private fun beginTransactionProfile() {
        if (myAccountFragment == null) {
            myAccountFragment = MyAccountFragment()

        }
        val transaction = requireActivity().supportFragmentManager.beginTransaction().replace(R.id.frameLayoutHome, myAccountFragment!!)

        transaction.addToBackStack(requireActivity().localClassName)
        transaction.commit()
    }
}