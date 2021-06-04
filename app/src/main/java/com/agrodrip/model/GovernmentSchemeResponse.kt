package com.agrodrip.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GovernmentSchemeResponse(
    @SerializedName("rows") val rows: ArrayList<GovernmentSchemeData>,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int,
    @SerializedName("errors") val errors: String
) : Parcelable


@Parcelize
data class GovernmentSchemeData(
    @SerializedName("date") val date: String,
    @SerializedName("scheme_image") val scheme_image: String,
    @SerializedName("scheme_title_en") val scheme_title_en: String,
    @SerializedName("scheme_title_gu") val scheme_title_gu: String,
    @SerializedName("description_en") val description_en: String,
    @SerializedName("description_gu") val description_gu: String,
    @SerializedName("scheme_video_url") val video_url: String,
    @SerializedName("id") val id: String
) : Parcelable