package com.agrodrip.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FarmFertilizerResponse(
    @SerializedName("rows") val rows: ArrayList<FarmFertilizerData>,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int,
    @SerializedName("errors") val errors: String
) : Parcelable


@Parcelize
data class FarmFertilizerData(
    @SerializedName("date") val date: String,
    @SerializedName("fertiliser_image") val fertiliser_image: String,
    @SerializedName("title_en") val title_en: String,
    @SerializedName("title_gu") val title_gu: String,
    @SerializedName("description_en") val description_en: String,
    @SerializedName("description_gu") val description_gu: String,
    @SerializedName("id") val id: String
) : Parcelable