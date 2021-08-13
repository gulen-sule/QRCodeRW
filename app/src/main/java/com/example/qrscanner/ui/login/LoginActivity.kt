package com.example.qrscanner.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qrscanner.Const
import com.example.qrscanner.applications.QrScannerApplication
import com.example.qrscanner.databinding.ActivityLoginBinding
import com.example.qrscanner.ui.home.HomeActivity
import com.example.qrscanner.ui.user.UserActivity
import com.google.gson.Gson

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = LoginViewModel()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSignIn.setOnClickListener {
            clickBtnSignIn()
        }
    }

    private fun clickBtnSignIn() {
        val email = binding.emailEditText.text
        val password = binding.passwordEditText.text
        if (email == null || password == null) {
            createToast("Please fill the email, password boxes")
            return
        }
        loginViewModel.login(email = email.toString(), password = password.toString(), completed = { response ->
            if (response != null) {
                Log.d("succesTAg", response.token + ">>>>")
            }
            if (response == null)
                createToast("Please enter a valid email and password combination")
            else {

                val sharedPref: SharedPreferences = this.getSharedPreferences(Const.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                with(sharedPref.edit()) {
                    putString(Const.AUTH_TOKEN_NAME, response.token)
                    apply()
                }
                QrScannerApplication.getInstance().setToken(response.token).also { switchToFragment(response.token)  }

            }
        })
    }

    private fun createToast(textMessage: String?) {
        if (textMessage != null)
            Toast.makeText(this, textMessage, Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, "An error occurred please contact to the programmer", Toast.LENGTH_SHORT).show()
    }

    private fun switchToFragment(token: String) {
        loginViewModel.getMe { me ->
            when (me?.role) {
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

    override fun onBackPressed() {
        finishAffinity()
        finish()
    }
}



