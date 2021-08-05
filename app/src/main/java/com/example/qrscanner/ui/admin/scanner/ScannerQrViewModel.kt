package com.example.qrscanner.ui.admin.scanner

import android.os.StrictMode
import android.util.JsonReader
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.qrscanner.data.api.ProfileService
import com.example.qrscanner.data.api.models.profile.ProfileModel
import com.example.qrscanner.data.api.models.profile.UserResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Reader
import java.math.BigInteger

class ScannerQrViewModel : ViewModel() {
    private var retrofit: ProfileService
    private val BASE_URL = "http://192.168.1.88:8000"
    val qrResult = MutableLiveData("")

    init {
        val gson = GsonBuilder().setLenient().create()
        retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL)
            .build()
            .create(ProfileService::class.java)
        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    fun clearLiveData() {
        qrResult.postValue("")
    }

    fun getProfile(id_number: BigInteger, completed: (ProfileModel?) -> Unit) {
        val response = retrofit.getProfile(id_number = id_number)

        response.enqueue(object : Callback<ProfileModel> {
            override fun onResponse(call: Call<ProfileModel>, response: Response<ProfileModel>) {
                val profileModel = response.body()
                completed(profileModel)
                Log.d("CallbackTAG", Gson().toJson(response.body()))
            }

            override fun onFailure(call: Call<ProfileModel>, t: Throwable) {
                completed(null)
            }

        })
    }


}