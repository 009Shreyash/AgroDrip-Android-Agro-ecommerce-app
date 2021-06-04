package com.agrodrip.model

import com.google.gson.annotations.SerializedName

data class VideoListResponse
    (
    @SerializedName("rows") val rows: ArrayList<VideoListData>,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("errors") val errors: String,
    @SerializedName("code") val code: Int
)

data class VideoListData(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String
)