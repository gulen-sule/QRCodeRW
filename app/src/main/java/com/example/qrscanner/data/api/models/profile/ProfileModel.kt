package com.example.qrscanner.data.api.models.profile

import com.example.qrscanner.data.api.models.authority.Authority
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProfileModel(
    @SerializedName("createdAt") val createdAt : String,
    @SerializedName("name") val name : String,
    @SerializedName("lastName") val lastName : String,
    @SerializedName("idNumber") var idNumber : String,
    @SerializedName("avatar") val avatar : String?,
    @SerializedName("authorities") val authorities : List<Authority>?,
    @SerializedName("id") val id : Int
) : Serializable
