package com.agrodrip.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TopProductListResponse(
    @SerializedName("rows") val rows: ArrayList<TopProductListData>,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int,
    @SerializedName("errors") val errors: String
) : Parcelable


@Parcelize
data class TopProductListData(
    @SerializedName("unit_name_en") val unit_name_en: String,
    @SerializedName("unit_name_gu") val unit_name_gu: String,
    @SerializedName("id") val id: Int,
    @SerializedName("category_id") val category_id: Int,
    @SerializedName("sub_category_id") val sub_category_id: Int,
    @SerializedName("unit_id") val unit_id: Int,
    @SerializedName("company_name") val company_name: String,
    @SerializedName("pro_name_en") val pro_name_en: String,
    @SerializedName("pro_name_gu") val pro_name_gu: String,
    @SerializedName("pro_description_en") val pro_description_en: String,
    @SerializedName("pro_description_gu") val pro_description_gu: String,
    @SerializedName("price_gu") val price_gu: Int,
    @SerializedName("pro_price_details_en") val pro_price_details_en: String,
    @SerializedName("pro_price_details_gu") val pro_price_details_gu: String,
    @SerializedName("pro_image") val pro_image: ArrayList<String>,
) : Parcelable