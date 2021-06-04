package com.agrodrip.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TipsListResponse(
    @SerializedName("rows") val rows: RowsData,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int,
    @SerializedName("errors") val errors: String
) : Parcelable


@Parcelize
data class RowsData(
    @SerializedName("id") val id: String,
    @SerializedName("crop_type_id") val crop_type_id: String,
    @SerializedName("crop_type_sub_id") val crop_type_sub_id: String,
    @SerializedName("title_en") val title_en: String,
    @SerializedName("title_gu") val title_gu: String,
    @SerializedName("description_en") val description_en: String,
    @SerializedName("description_gu") val description_gu: String,
    @SerializedName("tip_image") val tip_image: String,
    @SerializedName("solution_id") val solution_id: String,
    @SerializedName("status") val status: String,
    @SerializedName("tips") val tips: ArrayList<TipsListData>
) : Parcelable

@Parcelize
data class TipsListData(
    @SerializedName("unit_name_en") val unit_name_en: String,
    @SerializedName("unit_name_gu") val unit_name_gu: String,
    @SerializedName("id") val id: String,
    @SerializedName("category_id") val category_id: String,
    @SerializedName("sub_category_id") val sub_category_id: String,
    @SerializedName("unit_id") val unit_id: String,
    @SerializedName("company_name") val company_name: String,
    @SerializedName("pro_name_en") val pro_name_en: String,
    @SerializedName("pro_name_gu") val pro_name_gu: String,
    @SerializedName("pro_description_en") val pro_description_en: String,
    @SerializedName("pro_description_gu") val pro_description_gu: String,
    @SerializedName("price_gu") val price_gu: String,
    @SerializedName("pro_price_details_en") val pro_price_details_en: String,
    @SerializedName("pro_price_details_gu") val pro_price_details_gu: String,
    @SerializedName("pro_image") val pro_image: ArrayList<String>,
    @SerializedName("status") val status: String,
    @SerializedName("is_top") val is_top: String,
) : Parcelable