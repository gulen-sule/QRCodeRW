package com.example.qrscanner.data.api

import com.example.qrscanner.data.api.models.profile.ProfileModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import java.math.BigInteger

interface ProfileService {
    @GET("/id/{id_number}")
    fun getProfile(
        @Path("id_number") id_number: BigInteger,
    ): Call<ProfileModel>


}