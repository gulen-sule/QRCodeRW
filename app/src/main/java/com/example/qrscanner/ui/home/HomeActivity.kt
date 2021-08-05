package com.example.qrscanner.ui.home

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.qrscanner.R
import com.example.qrscanner.data.api.models.profile.ProfileModel
import com.example.qrscanner.data.api.models.profile.UserResponse
import com.example.qrscanner.databinding.ActivityHomeBinding
import com.example.qrscanner.isNumeric
import com.example.qrscanner.ui.admin.profile.ProfileFragment
import com.example.qrscanner.ui.admin.scanner.ScannerQrViewModel
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.math.BigInteger
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: ScannerQrViewModel
    private var token = ""
    private var barcode = ""
    private var toastCalled = false
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
        if (event?.action == KeyEvent.ACTION_UP) {
            if (event.keyCode == KeyEvent.KEYCODE_BACK)
                onBackPressed()

            pressedKey = event.unicodeChar.toChar().toString()
        }
        // Log.d("KeyEventsStringLOG", pressedKey)
        if (pressedKey != "\n") {
            viewModel.qrResult.value += pressedKey
        }
        // Log.d("KeyEventsTAG", event.toString())

        return true
    }

    private fun sendQuery(id_number: BigInteger, completed: (ProfileModel?) -> Unit) {
        viewModel = ViewModelProvider(this).get(ScannerQrViewModel::class.java)
        viewModel.getProfile(id_number) {
            completed(it)
        }
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
                        binding.progressBarHome.visibility = View.VISIBLE
                        val id = viewModel.qrResult.value
                        Log.d("idTAG", id.toString())
                        sendQuery(id!!.toBigInteger()) {
                            val profile = it
                            if (profile != null) {
                                beginTransactionProfile(profile)
                            } else {
                                binding.progressBarHome.visibility = View.GONE
                                showToastMsg(id)
                            }
                        }.also {
                            cTimer.cancel()
                            viewModel.clearLiveData()
                        }
                    }
                }
            } else {
                showToastMsg()
            }
        }
    }

    private fun showToastMsg(wrongCode: String) {
        if (barcode != wrongCode) {
            barcode = wrongCode
            val toast = Toast.makeText(this, "Please hold the camera to the right barcode", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
    }

    private fun showToastMsg() {
        if (!toastCalled) {
            toastCalled = true
            GlobalScope.async {

                toastCalled = false
            }
            val toast = Toast.makeText(this, "Please hold the camera to the right barcode", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
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
            binding.progressBarHome.visibility = View.GONE
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