package com.example.qrscanner.ui.myAccount

import androidx.lifecycle.ViewModel
import com.example.qrscanner.data.api.ApiClient
import com.example.qrscanner.data.api.models.profile.ProfileModel

class MyAccountViewModel : ViewModel() {
    fun getMe(completed: (ProfileModel?) -> Unit) {
        ApiClient.instance().fetch(ApiClient.instance().service.getMyInfo(),
            success = { response, code, message ->
                completed(response)
            }, failure = {
                completed(null)

            }, loading = {

            }

        )
    }
}