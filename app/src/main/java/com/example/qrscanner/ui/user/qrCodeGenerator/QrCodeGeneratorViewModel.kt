package com.example.qrscanner.ui.user.qrCodeGenerator

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.qrscanner.data.ResponseModel
import com.example.qrscanner.data.api.ApiClient
import com.google.android.gms.common.api.Api
import java.util.*

class QrCodeGeneratorViewModel : ViewModel() {

    fun getToken(completed: (ResponseModel?) -> Unit) {
        ApiClient.instance().fetch(ApiClient.instance().service.getBarcodeToken(),
            success = { response, code, message ->
                Log.d("tokenTag","succes")
                completed(response)
            }, failure = {
                Log.d("tokenTag","failure")
                completed(null)
            },
            loading = {

            }
        )
    }

}