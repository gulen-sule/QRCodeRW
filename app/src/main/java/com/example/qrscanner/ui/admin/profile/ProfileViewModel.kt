package com.example.qrscanner.ui.admin.profile

import androidx.lifecycle.ViewModel
import com.example.qrscanner.data.api.ApiClient
import com.example.qrscanner.data.api.models.profile.ProfileModel

class ProfileViewModel : ViewModel() {
    fun getProfile(token: String?, completed: (ProfileModel?) -> Unit) {
        val hashMap = HashMap<String, String?>()
        hashMap["query_token"] = token
        ApiClient.instance().fetch(ApiClient.instance().service.getProfile(hashMap),
            success = { response, code, message ->
                completed(response)
            }, failure = {
                completed(null)
            },
            loading = {

            }

        )
    }

}