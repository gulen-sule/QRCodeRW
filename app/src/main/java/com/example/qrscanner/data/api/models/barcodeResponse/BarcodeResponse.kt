package com.example.qrscanner.data.api.models.barcodeResponse

import com.google.gson.annotations.SerializedName

data class BarcodeResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("status") val status: Int,
    @SerializedName("query_token") var token: String?
)
