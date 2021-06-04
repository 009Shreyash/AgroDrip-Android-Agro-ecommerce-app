package com.agrodrip.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderListResponse(
    @SerializedName("row") val row: ArrayList<OrderListData>,
    @SerializedName("status") val status: String,
    @SerializedName("code") val code: String,
    @SerializedName("errors") val errors: String
) : Parcelable


@Parcelize
data class OrderListData(
    @SerializedName("order_row") val order_row: ArrayList<OrderList>,
    @SerializedName("address") val address: Address,
    @SerializedName("order_no") val order_no: String,
    @SerializedName("total_amount") val total_amount: String,
    @SerializedName("order_date") val order_date: String,
    @SerializedName("payment_type") val payment_type: String,
    @SerializedName("status") val status: String,
    @SerializedName("status_name") val status_name: String,
    @SerializedName("id") val id: String
) : Parcelable

@Parcelize
data class OrderList(
    @SerializedName("total") val total: String,
    @SerializedName("qty") val qty: String,
    @SerializedName("product") val product: Product
) : Parcelable

@Parcelize
data class Address(
    @SerializedName("id") val id: String,
    @SerializedName("app_key") val app_app_key: String,
    @SerializedName("state") val state: String,
    @SerializedName("city") val city: String,
    @SerializedName("full_name") val full_name: String,
    @SerializedName("pincode") val pincode: String,
    @SerializedName("address_1") val address_1: String,
    @SerializedName("address_2") val address_2: String,
    @SerializedName("status") val status: String
) : Parcelable

@Parcelize
data class Product(
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
