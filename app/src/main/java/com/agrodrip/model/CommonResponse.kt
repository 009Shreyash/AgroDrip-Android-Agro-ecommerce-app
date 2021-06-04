package com.agrodrip.model

import com.google.gson.annotations.SerializedName

data class CommonResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int
)

