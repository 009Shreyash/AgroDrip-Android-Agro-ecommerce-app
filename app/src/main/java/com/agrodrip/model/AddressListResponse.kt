package com.agrodrip.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddressListResponse(
    @SerializedName("rows") val rows: ArrayList<AddressListData>,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int,
    @SerializedName("errors") val errors: String
) : Parcelable


@Parcelize
data class AddressListData(
    @SerializedName("id") val id: Int,
    @SerializedName("state") val state: String,
    @SerializedName("city") val city: String,
    @SerializedName("full_name") val full_name: String,
    @SerializedName("pincode") val pincode: Int,
    @SerializedName("address_1") val address_1: String,
    @SerializedName("address_2") val address_2: String
) : Parcelable
