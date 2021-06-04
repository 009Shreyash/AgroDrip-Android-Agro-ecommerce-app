package com.agrodrip.model

import com.google.gson.annotations.SerializedName

data class OtpResponse(
    @SerializedName("rows") val rows: Rows,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int
)


data class Rows(
    @SerializedName("otp") val otp: String
)