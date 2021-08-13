package com.example.qrscanner.ui.admin.scanner

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.qrscanner.data.api.ApiClient
import com.example.qrscanner.data.api.models.barcodeResponse.BarcodeResponse
import com.google.gson.Gson
import kotlin.collections.HashMap

class ScannerQrViewModel : ViewModel() {
    val qrResult = MutableLiveData("")
    fun clearLiveData() {
        qrResult.postValue("")
    }

    fun getProfile(barcodeToken: String?, completed: (BarcodeResponse?) -> Unit) {
        val hashMap = HashMap<String, String?>()
        hashMap["token"] = barcodeToken
        ApiClient.instance().fetch(
            ApiClient.instance().service.isPermitted(tokenMap = hashMap),
            success = { response, _, _ ->
                completed(response)
                Log.d("CallbackTAG", Gson().toJson(response))
            },
            failure = {
                completed(null)
            },
            loading = {

            },

            )
    }

    fun sendBarcodeToke() {

    }

    /*   val response = ApiClient.instance().service.getProfile(id_number = id_number)
       response.enqueue(object : Callback<ProfileModel> {
           override fun onResponse(call: Call<ProfileModel>, response: Response<ProfileModel>) {
               val profileModel = response.body()
               completed(profileModel) }
           override fun onFailure(call: Call<ProfileModel>, t: Throwable) {
               completed(null) }
           artik fetch fonksiyonumla bunu yaptigim icin bu kısma ihtiyacım yok
       })*/


}