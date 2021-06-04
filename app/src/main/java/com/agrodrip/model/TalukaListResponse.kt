package com.agrodrip.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TalukaListResponse(
    @SerializedName("rows") val rows: ArrayList<TalukaListData>,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int,
    @SerializedName("errors") val errors: String
) : Parcelable


@Parcelize
data class TalukaListData(
    @SerializedName("date") val date: String,
    @SerializedName("name_en") val name_en: String,
    @SerializedName("name_gu") val name_gu: String,
) : Parcelable