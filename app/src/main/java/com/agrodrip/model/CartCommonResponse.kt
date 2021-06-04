package com.agrodrip.model

import com.google.gson.annotations.SerializedName

data class CartCommonResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("count") val count: String,
    @SerializedName("code") val code: Int
)

