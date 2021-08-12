package com.example.qrscanner.data.api.models.profile

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.math.BigInteger

data class ProfileModel(
    // @SerializedName("id") val id: Int, ?????sor?????
    @SerializedName("first_name") val name: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("email") val email: String?,
    @SerializedName("id_number") var idNumber: BigInteger,
    @SerializedName("user_photo_path") val avatar: String?,
    @SerializedName("role") val role: Int
    /* @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("authorities") val authorities: List<Authority>? = null,*/
) : Serializable
