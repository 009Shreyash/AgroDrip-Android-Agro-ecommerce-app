package com.agrodrip.model

import com.google.gson.annotations.SerializedName

data class VerifyOtpResponse(
    @SerializedName("rows") val rows: UserData,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("errors") val errors: String,
    @SerializedName("code") val code: Int
)