package com.agrodrip.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyFarmListResponse(
	@SerializedName("rows") val rows: ArrayList<MyFarmListData>,
	@SerializedName("status") val status: String,
	@SerializedName("message") val message: String,
	@SerializedName("code") val code: Int,
	@SerializedName("errors") val errors: String
) : Parcelable


@Parcelize
data class MyFarmListData(
	@SerializedName("id") val id: String,
	@SerializedName("farm_name") val farm_name: String,
	@SerializedName("area") val area: String,
	@SerializedName("unit") val unit: String,
	@SerializedName("sowing_date") val sowing_date: String,
	@SerializedName("cropType") val cropType: CropType,
	@SerializedName("cropTypeSub") val cropSubType: CropSubType,
) : Parcelable

@Parcelize
data class CropType(
	@SerializedName("id") val id: String,
	@SerializedName("crop_name_en") val crop_name_en: String,
	@SerializedName("crop_name_gu") val crop_name_gu: String,
	@SerializedName("crop_image") val crop_image: String,
) : Parcelable


@Parcelize
data class CropSubType(
	@SerializedName("id") val id: String,
	@SerializedName("sub_name_en") val sub_name_en: String,
	@SerializedName("sub_name_gu") val sub_name_gu: String,
	@SerializedName("type_sub_image") val type_sub_image: String,
	@SerializedName("stage") val stage: ArrayList<Stage>,
) : Parcelable


@Parcelize
data class Stage(
	@SerializedName("from_day") val from_day: String,
	@SerializedName("to_day") val to_day: String,
	@SerializedName("day_status_en") val day_status_en: String,
	@SerializedName("day_status_gu") val day_status_gu: String,
	@SerializedName("datediff") val datediff: String,
	@SerializedName("farm_status") val farm_status: String,
) : Parcelable