package com.example.qrscanner.data.api

import com.example.qrscanner.data.ResponseModel
import com.example.qrscanner.data.api.models.barcodeResponse.BarcodeResponse
import com.example.qrscanner.data.api.models.login.LoginResponseModel
import com.example.qrscanner.data.api.models.profile.ProfileModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("/api/isPermitted")
    fun isPermitted(
        @Body tokenMap :HashMap<String, String?>
    ): Observable<Response<BarcodeResponse?>>

    @POST("/api/login")
    fun login(
        @Body loginBody: HashMap<String, String>
    ): Observable<Response<LoginResponseModel?>>

    @POST("/api/isPermitted")
    fun getProfile(
        @Body tokenMap :HashMap<String, String?>
    ): Observable<Response<ProfileModel?>>

    @GET("/api/role")
    fun getRole(): Observable<Response<Int?>>

    @GET("/api/me")
    fun getMyInfo(): Observable<Response<ProfileModel?>>

    @GET("/api/getBarcode")
    fun getBarcodeToken(): Observable<Response<ResponseModel?>>

}