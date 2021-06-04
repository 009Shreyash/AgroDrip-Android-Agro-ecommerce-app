package com.agrodrip.model

import com.google.gson.annotations.SerializedName

data class UpdateOtpResponse(
    @SerializedName("otp") val otp: String,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("errors") val errors: String,
    @SerializedName("code") val code: Int
)