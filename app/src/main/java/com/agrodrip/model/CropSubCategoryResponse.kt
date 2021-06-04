package com.agrodrip.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CropSubCategoryResponse(
    @SerializedName("rows") val rows: ArrayList<CropSubCategoryData>,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int,
    @SerializedName("errors") val errors: String
) : Parcelable


@Parcelize
data class CropSubCategoryData(
    @SerializedName("id") val id: String,
    @SerializedName("crop_type_id") val category_id: String,
    @SerializedName("sub_name_en") val sub_name_en: String,
    @SerializedName("sub_name_gu") val sub_name_gu: String,
    @SerializedName("type_sub_image") val sub_image: String,
    @SerializedName("status") val status: String
) : Parcelable