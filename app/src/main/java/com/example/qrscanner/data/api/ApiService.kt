package com.example.qrscanner.data.api

import com.example.qrscanner.data.ResponseModel
import com.example.qrscanner.data.api.models.login.LoginResponseModel
import com.example.qrscanner.data.api.models.profile.ProfileModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.math.BigInteger

interface ApiService {
    @GET("/api/id/{id_number}")
    fun getProfile(
        @Path("id_number") id_number: BigInteger,
    ): Observable<Response<ProfileModel?>>

    @POST("/api/login")
    fun login(
        @Body loginBody: HashMap<String, String>
    ): Observable<Response<LoginResponseModel?>>

    @GET("/api/role")
    fun getRole(): Observable<Response<Int?>>

    @GET("/api/me")
    fun getMyInfo(): Observable<Response<ProfileModel?>>

    @GET("/api/getBarcode")
    fun getBarcodeToken(): Observable<Response<ResponseModel?>>

}