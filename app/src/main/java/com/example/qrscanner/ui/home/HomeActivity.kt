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
import com.example.qrscanner.data.api.models.barcodeResponse.BarcodeResponse
import com.example.qrscanner.data.api.models.profile.ProfileModel
import com.example.qrscanner.databinding.ActivityHomeBinding
import com.example.qrscanner.isNumeric
import com.example.qrscanner.ui.admin.profile.ProfileFragment
import com.example.qrscanner.ui.admin.scanner.ScannerQrViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.math.BigInteger
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: ScannerQrViewModel = ScannerQrViewModel()
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
            barcode += pressedKey
        } else {
            viewModel.qrResult.postValue(barcode)
            barcode= ""
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
        val tokenLength = 30
        viewModel.qrResult.observe(this) { qrLiveData ->
            Log.d("liveDataValueTAG", qrLiveData)
            if (qrLiveData != null && qrLiveData.toString().isNumeric()) {
                when (qrLiveData.toString().length) {
                    in 1 until tokenLength -> {
                        cTimer.cancel()
                        cTimer.start()
                        Log.d("1to10TAG", "log")
                    }
                    tokenLength -> {
                        binding.progressBarHome.visibility = View.VISIBLE
                        val token = qrLiveData
                        Log.d( "barcodeTAG", barcode)
                        viewModel.getProfile(token){barcodeResponse->
                            if(barcodeResponse!=null)
                                beginTransactionProfile(barcodeResponse)
                            else {
                                binding.progressBarHome.visibility = View.GONE
                                showToastMsg(barcode)
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
    private fun beginTransactionProfile(response: BarcodeResponse?) {
        if (profileFragment == null) {
            profileFragment = ProfileFragment()
           // profileFragment?.setProfileModel(profile)
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


    private fun unvalidBarcodeBottomSheet(status: Int) {
        val message = giveDeniedMessage(status)
    }

    private fun giveDeniedMessage(status: Int): String {
        return when(status){
            1-> "You do not have executive's permission"
            2-> "This barcode is expired"
            3-> "You do not have controller mission"
            4-> "There is none person in the list with this uuid"
            5-> "Permission is not available"
            6-> "Your permission is denied"
            else-> "Unknown error is occurred"
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