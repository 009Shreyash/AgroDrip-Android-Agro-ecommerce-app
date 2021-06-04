package com.agrodrip.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FarmProblemListResponse(
    @SerializedName("rows") val rows: ArrayList<FarmProblemListListData>,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int,
    @SerializedName("errors") val errors: String
) : Parcelable


@Parcelize
data class FarmProblemListListData(
    @SerializedName("id") val id: String,
    @SerializedName("crop_type_id") val crop_type_id: String,
    @SerializedName("crop_type_sub_id") val crop_type_sub_id: String,
    @SerializedName("title_en") val title_en: String,
    @SerializedName("title_gu") val title_gu: String,
    @SerializedName("description_en") val description_en: String,
    @SerializedName("description_gu") val description_gu: String,
    @SerializedName("problem_image") val problem_image: String,
    @SerializedName("solution_id") val solution_id: String,
    @SerializedName("created_at") val created_at: String
) : Parcelable
