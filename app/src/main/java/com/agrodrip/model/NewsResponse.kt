package com.agrodrip.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewsResponse(
    @SerializedName("rows") val rows: ArrayList<NewsData>,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int,
    @SerializedName("errors") val errors: String
) : Parcelable


@Parcelize
data class NewsData(
    @SerializedName("date") val date: String,
    @SerializedName("news_image") val news_image: String,
    @SerializedName("news_title_en") val news_title_en: String,
    @SerializedName("news_title_gu") val news_title_gu: String,
    @SerializedName("description_en") val description_en: String,
    @SerializedName("description_gu") val description_gu: String,
    @SerializedName("video_url") val video_url: String
) : Parcelable