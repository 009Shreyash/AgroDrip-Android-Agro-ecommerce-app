package com.agrodrip.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CropCategoryResponse(
    @SerializedName("rows") val rows: ArrayList<CropCategoryData>,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int,
    @SerializedName("errors") val errors: String
) : Parcelable


@Parcelize
data class CropCategoryData(
    @SerializedName("id") val id: String,
    @SerializedName("crop_image") val banner_image: String,
    @SerializedName("crop_name_en") val description_en: String,
    @SerializedName("crop_name_gu") val description_gu: String
) : Parcelable