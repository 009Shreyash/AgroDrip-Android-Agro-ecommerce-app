package com.agrodrip.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IssueDripResponse(
    @SerializedName("rows") val rows: ArrayList<IssueDripData>,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int,
    @SerializedName("errors") val errors: String
) : Parcelable


@Parcelize
data class IssueDripData(
    @SerializedName("date") val date: String,
    @SerializedName("issue_image") val issue_image: String,
    @SerializedName("issue_title_en") val issue_title_en: String,
    @SerializedName("issue_title_gu") val issue_title_gu: String,
    @SerializedName("description_en") val description_en: String,
    @SerializedName("description_gu") val description_gu: String,
    @SerializedName("issue_video_url") val issue_video_url: String,
    @SerializedName("id") val id: String
) : Parcelable