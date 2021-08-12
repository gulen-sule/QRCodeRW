package com.example.qrscanner.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResponseModel(
    @SerializedName("token") var token:String?,
    @SerializedName("time") var time:Int=30,
):Serializable
