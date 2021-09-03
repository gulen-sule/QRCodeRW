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
import com.example.qrscanner.R
import com.example.qrscanner.databinding.ActivityHomeBinding
import com.example.qrscanner.ui.executive.profile.ProfileFragment
import com.example.qrscanner.ui.executive.scanner.ScannerQrViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: ScannerQrViewModel = ScannerQrViewModel()
    private var token = ""
    private var barcode = ""
    private var toastCalled = false
    private val tokenLength = 30

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
            if (event.metaState == 0 && event.keyCode != KeyEvent.KEYCODE_SHIFT_LEFT)
                pressedKey = event.unicodeChar.toChar().toString()
        }
        // Log.d("KeyEventsStringLOG", event.toString())
        if (pressedKey != "\n") {
            barcode += pressedKey
        } else {
            viewModel.qrResult.postValue(barcode)
        }
        return true
    }


    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.clearLiveData()
        mutableDataSetObserver()
        supportFragmentManager.beginTransaction().replace(R.id.frameLayoutHome, HomeFragment()).commit()
    }


    private fun mutableDataSetObserver() {
        viewModel.qrResult.observe(this) { qrLiveData ->

            Log.d("liveDataValueTAG", qrLiveData)

            if (qrLiveData != null) {
                when (qrLiveData.toString().length) {
                    in 1 until tokenLength -> {
                        cTimer.cancel()
                        cTimer.start()
                        Log.d("1to10TAG", "log")
                    }
                    tokenLength -> {
                        getProfile(qrLiveData).also {
                            cTimer.cancel()
                            viewModel.clearLiveData()
                        }
                    }
                }
            } else {
                showToastMsg(null)
            }
        }
    }

    private fun getProfile(qrLiveData: String) {
        binding.progressBarHome.visibility = View.VISIBLE
        Log.d("barcodeTAG", barcode)
        viewModel.getBarcodeResponse(qrLiveData) { barcodeResponse ->
            if (barcodeResponse != null)
                beginTransactionProfile()
            else {
                binding.progressBarHome.visibility = View.GONE
                showToastMsg(barcode)
            }
        }
    }

    private fun showToastMsg(wrongCode: String?) {
        if (wrongCode != null) {
            if (barcode != wrongCode) {
                barcode = wrongCode
                val toast = Toast.makeText(this, "Please hold the camera to the right barcode", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        } else if (!toastCalled) {
            toastCalled = true
            GlobalScope.async {
                Thread.sleep(2000);
                toastCalled = false
            }
            val toast = Toast.makeText(this, "Please hold the camera to the right barcode", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }

    }


    private var transaction: FragmentTransaction? = null
    private var profileFragment: ProfileFragment? = null
    private fun beginTransactionProfile() {
        if (profileFragment == null) {
            profileFragment = ProfileFragment()
        }
        transaction = supportFragmentManager.beginTransaction().replace(R.id.frameLayoutHome, profileFragment!!)
        //  Log.d("profileTAG", profile?.name.toString())
        transaction?.addToBackStack(this@HomeActivity.localClassName)
        commitTransaction()
    }

    private fun commitTransaction() {
        transaction?.commit().also {
            binding.progressBarHome.visibility = View.GONE
        }
    }


    private fun AlertDialogInvalidBarcode(status: Int) {
        val message = giveDeniedMessage(status)
    }

    private fun giveDeniedMessage(status: Int): String {
        return when (status) {
            1 -> "You do not have executive's permission"
            2 -> "This barcode is expired"
            3 -> "You do not have controller mission"
            4 -> "There is none person in the list with this uuid"
            5 -> "Permission is not available"
            6 -> "Your permission is denied"
            else -> "Unknown error is occurred"
        }
    }


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