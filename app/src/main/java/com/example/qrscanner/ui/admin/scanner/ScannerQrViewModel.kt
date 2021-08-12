package com.example.qrscanner.ui.admin.scanner

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.qrscanner.data.api.ApiClient
import com.example.qrscanner.data.api.models.profile.ProfileModel
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigInteger

class ScannerQrViewModel : ViewModel() {
    val qrResult = MutableLiveData("")
    fun clearLiveData() {
        qrResult.postValue("")
    }

    fun getProfile(id_number: BigInteger, completed: (ProfileModel?) -> Unit) {

        ApiClient.instance().fetch(
            ApiClient.instance().service.getProfile(id_number = id_number),

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
    fun sendBarcodeToke(){

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