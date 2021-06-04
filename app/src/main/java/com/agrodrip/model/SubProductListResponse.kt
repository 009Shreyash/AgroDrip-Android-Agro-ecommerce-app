package com.agrodrip.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubProductListResponse(
    @SerializedName("rows") val rows: ArrayList<SubProductListData>,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int,
    @SerializedName("errors") val errors: String
) : Parcelable


@Parcelize
data class SubProductListData(
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
    @SerializedName("price_main") val price_main: String,
    @SerializedName("pro_price_details_en") val pro_price_details_en: String,
    @SerializedName("pro_price_details_gu") val pro_price_details_gu: String,
    @SerializedName("pro_image") val pro_image: ArrayList<String>?=null,
    @SerializedName("status") val status: String,
    @SerializedName("is_top") val is_top: String,
    @SerializedName("unit_row") val unit_row: ArrayList<UnitRow>
) : Parcelable



