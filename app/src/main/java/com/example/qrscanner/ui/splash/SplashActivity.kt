package com.example.qrscanner.ui.splash

import android.animation.Animator
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import com.example.qrscanner.Const.AUTH_TOKEN_NAME
import com.example.qrscanner.Const.SHARED_PREF_NAME
import com.example.qrscanner.applications.QrScannerApplication
import com.example.qrscanner.R
import com.example.qrscanner.ui.home.HomeActivity
import com.example.qrscanner.ui.login.LoginActivity
import com.example.qrscanner.ui.user.UserActivity
import com.google.gson.Gson

class SplashActivity : AppCompatActivity() {
    private var shortAnimationDuration: Int = 0
    private var currentAnimator: Animator? = null
    private val splashViewModel: SplashViewModel = SplashViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        if (isNetworkAvailable(this)) {
            Log.d("tag", "burdayim")
            val sharedPref: SharedPreferences? = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val token = sharedPref?.getString(AUTH_TOKEN_NAME, null)
            if (token == null) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                Log.d("meTag", token)
                QrScannerApplication.getInstance().setToken(token)
                switchToFragment()
            }
        }

    }

    private fun switchToFragment() {
        splashViewModel.getMe { me ->
            Log.d("merTAg",Gson().toJson(me))
            if (me == null) {
                unAuthenticated()
            } else {
                when (me.role) {
                    1 -> {
                        val intent = Intent(this, UserActivity::class.java)
                        startActivity(intent)
                    }
                    2 -> {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }
                    else -> {
                        createToast(null)
                    }
                }
            }

        }
    }

    private fun unAuthenticated() {
        val sharedPref: SharedPreferences? = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPref?.edit {
            this.remove(AUTH_TOKEN_NAME)
        }
        QrScannerApplication.getInstance().setToken("")
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun createToast(textMessage: String?) {
        if (textMessage != null)
            Toast.makeText(this, textMessage, Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, "An error occurred please contact to the programmer", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivity: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as (ConnectivityManager)
        val info = connectivity.activeNetwork
        if (info == null) {
            Log.d("networkTAG", "burdayim")
            noInternetDialog()
            return false
        }
        return true
    }

    private fun noInternetDialog() {
        val alertDialog: AlertDialog = AlertDialog.Builder(this).create();
        alertDialog.setTitle("No Internet Connection");
        alertDialog.setMessage("Cannot found internet connection please connect to an internet");

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
            finishAffinity();
            finish();
        });
        alertDialog.show();

    }
}