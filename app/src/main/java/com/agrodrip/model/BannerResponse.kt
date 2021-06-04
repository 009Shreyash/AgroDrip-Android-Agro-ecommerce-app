package com.agrodrip.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BannerResponse(
    @SerializedName("rows") val rows: ArrayList<BannerData>,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int,
    @SerializedName("errors") val errors: String
) : Parcelable


@Parcelize
data class BannerData(
    @SerializedName("date") val date: String,
    @SerializedName("banner_image") val banner_image: String,
    @SerializedName("banner_title_en") val banner_title_en: String,
    @SerializedName("banner_title_gu") val banner_title_gu: String,
    @SerializedName("description_en") val description_en: String,
    @SerializedName("description_gu") val description_gu: String
) : Parcelable