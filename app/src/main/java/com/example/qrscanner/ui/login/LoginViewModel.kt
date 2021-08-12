package com.example.qrscanner.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.qrscanner.data.api.ApiClient
import com.example.qrscanner.data.api.models.login.LoginResponseModel
import com.example.qrscanner.data.api.models.profile.ProfileModel
import java.util.HashMap

class LoginViewModel : ViewModel() {

    fun login(email: String, password: String, completed: (LoginResponseModel?) -> Unit) {
        val hashMap = HashMap<String, String>()
        hashMap["email"] = email
        hashMap["password"] = password
        ApiClient.instance().fetch(ApiClient.instance().service.login(hashMap),
            success = { response, _, _ ->
                completed(response)
            },
            failure = {
                completed(null)
            },
            loading = {

            }
        )
    }

    fun getMe(completed: (ProfileModel?) -> Unit) {
        ApiClient.instance().fetch(
            ApiClient.instance().service.getMyInfo(),
            success = { response, code, message ->
                Log.d("successTAG", "succes")
                completed(response)
            }, failure = {
                Log.d("successTAG", "failed")
                completed(null)
            }, loading = {

            }
        )

    }

}