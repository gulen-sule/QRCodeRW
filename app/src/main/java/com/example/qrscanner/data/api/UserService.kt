package com.example.qrscanner.data.api

import com.example.qrscanner.data.ProfileModel
import retrofit2.Response
import retrofit2.http.GET

interface UserService {
    @GET("#")
    fun getToken(): Response<ProfileModel>
}