package com.agrodrip.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("rows") val rows: UserData,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int,
    @SerializedName("errors") val errors: String
)

data class UserData(
    @SerializedName("id") val id: String,
    @SerializedName("app_key") val app_key: String,
    @SerializedName("device_token") val device_token: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("profile") val profile: String,
    @SerializedName("language") val language: String,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val created_at: String,
    @SerializedName("updated_at") val updated_at: String
)