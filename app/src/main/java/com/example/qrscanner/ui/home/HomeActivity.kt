package com.example.qrscanner.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.qrscanner.R
import com.example.qrscanner.data.api.models.profile.ProfileModel
import com.example.qrscanner.databinding.ActivityHomeBinding
import com.example.qrscanner.isNumeric
import com.example.qrscanner.ui.admin.AdminActivity
import com.example.qrscanner.ui.admin.profile.ProfileFragment
import com.example.qrscanner.ui.admin.scanner.ScannerQrViewModel
import com.example.qrscanner.ui.user.UserActivity
import com.google.gson.Gson

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: ScannerQrViewModel
    private var token = ""

    private val cTimer = object : CountDownTimer(300, 100) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            viewModel.clearLiveData()
            viewModel.qrResult.postValue("")
            token = ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction().replace(R.id.frameLayoutHome, HomeFragment()).commit()
        viewModel = ScannerQrViewModel()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        mutableDataSetObserver()
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        var pressedKey = ""
        if (event?.action == KeyEvent.ACTION_DOWN)
            pressedKey = event.unicodeChar.toChar().toString()
        Log.d("KeyEventsStringLOG", pressedKey)
        if (pressedKey != "\n") {
            viewModel.qrResult.value += pressedKey
        }
        Log.d("KeyEventsTAG", Gson().toJson(event))

        return true
    }

    override fun onStop() {
        super.onStop()
        Log.d("StopTAG", "girdim")
    }

    private fun sendQuery(): ProfileModel? {
        viewModel = ViewModelProvider(this).get(ScannerQrViewModel::class.java)
        return viewModel.getProfile()?.get(7)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.clearLiveData()
        mutableDataSetObserver()
        supportFragmentManager.beginTransaction().replace(R.id.frameLayoutHome, HomeFragment()).commit()
    }

    private fun mutableDataSetObserver() {
        viewModel.qrResult.observe(this) {
            Log.d("liveDataValueTAG", it)
            if (it != null && it.toString().isNumeric()) {
                when (it.toString().length) {
                    in 1..10 -> {
                        cTimer.cancel()
                        cTimer.start()
                        Log.d("1to10TAG", "log")
                    }
                    11 -> {
                        val id = viewModel.qrResult.value
                        Log.d("idTAG", id.toString())
                        val profile = sendQuery()
                        cTimer.cancel()
                        viewModel.clearLiveData()
                        if (id != null) {
                            profile?.idNumber = id
                        }
                        beginTransactionProfile(profile)
                    }
                }
            } else {
                Log.d("lengthOfLiveData", it.length.toString() + "<" + it.toString() + ">")
            }
        }
    }

    private var transaction: FragmentTransaction? = null
    private var profileFragment: ProfileFragment? = null
    private fun beginTransactionProfile(profile: ProfileModel?) {
        if (profileFragment == null) {
            profileFragment = ProfileFragment()
            profileFragment?.setProfileModel(profile)
        }
        transaction = supportFragmentManager.beginTransaction().replace(R.id.frameLayoutHome, profileFragment!!)
        Log.d("profileTAG", profile?.name.toString())
        transaction?.addToBackStack(this@HomeActivity.localClassName)
        commitTransaction()
    }

    private fun commitTransaction() {
        transaction?.commit().also {
            binding.progressBarHome.visibility = View.INVISIBLE
        }
    }

/*
    private var textWatcher: TextWatcher? = null
    private fun getTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("textTAG", s.toString())
                if (s?.isNotEmpty()!! && s.isNotBlank())
                    token += s[start] + ""
                when (token.length) {
                    in 1..10 -> {
                        cTimer.cancel()
                        cTimer.start()
                    }
                    11 -> {
                        binding.progressBarHome.visibility = View.VISIBLE
                        val profile = sendQuery()
                        cTimer.cancel()
                        profile?.idNumber = token
                        token = ""
                        beginTransactionProfile(profile)
                    }
                    else -> {
                        Log.e("Error", "something is going wrong" + token.length)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
    }*/


    /*  private fun setBottomNavigation() {
          val navView: BottomNavigationView? = null// = binding.navView

          val navController = findNavController(R.id.nav_host_fragment_activity_main)

          val appBarConfiguration = AppBarConfiguration(
              setOf(
                  R.id.navigation_home, R.id.navigation_scanner, R.id.navigation_generator
              )
          )
          setupActionBarWithNavController(navController, appBarConfiguration)
          navView?.setupWithNavController(navController)
      }
      }*/

}