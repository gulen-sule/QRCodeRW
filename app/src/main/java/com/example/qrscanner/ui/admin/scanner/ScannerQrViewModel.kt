package com.example.qrscanner.ui.admin.scanner

import android.os.StrictMode
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.qrscanner.data.api.ProfileService
import com.example.qrscanner.data.api.models.profile.ProfileModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ScannerQrViewModel : ViewModel() {
    private var retrofit: ProfileService
    private val BASE_URL = "https://60ebedc5e9647b0017cddf34.mockapi.io"
    val qrResult = MutableLiveData("")

    init {
        retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ProfileService::class.java)
        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    fun clearLiveData() {
        qrResult.postValue("")
    }

    fun getProfile(): List<ProfileModel>? {
        val response = retrofit.getProfile()
        return response.execute().body()
    }


}