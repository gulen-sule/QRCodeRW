package com.example.qrscanner.data.api

import com.example.qrscanner.data.api.models.profile.ProfileModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProfileService {
    @GET("/service/profile/profiles")
    fun getProfile(
    ): Call<List<ProfileModel>>

}