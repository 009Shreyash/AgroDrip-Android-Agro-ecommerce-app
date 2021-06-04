package com.agrodrip.webservices

import com.agrodrip.model.*
import com.agrodrip.utils.Constants
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*


interface APIInterface {

    @FormUrlEncoded
    @POST(Constants.SEND_OTP)
    fun sendOtp(@Field("mobile") mobile: String?): Call<OtpResponse>?

    @FormUrlEncoded
    @POST(Constants.VERIFY_SIGNUP)
    fun verifyOtp(@FieldMap requestBody: HashMap<String, String>): Call<VerifyOtpResponse>?

    @POST(Constants.UPDATE_PROFILE)
    fun updateProfileApi(@Body file: RequestBody?, @Header("Authorization") Authorization: String): Call<OtpResponse>?

    @GET(Constants.VIDEO_LIST)
    fun getVideoList(): Call<VideoListResponse>?

    @GET(Constants.BANNER_LIST)
    fun getBannerList(): Call<BannerResponse>?

    @GET(Constants.NEWS_LIST)
    fun getNewsList(): Call<NewsResponse>?

    @GET(Constants.ISSUE_DRIP)
    fun getIssueDripList(): Call<IssueDripResponse>?

    @GET(Constants.GOV_SCHEME)
    fun getGovSchemeList(): Call<GovernmentSchemeResponse>?

    @GET(Constants.FERTILIZER_LIST)
    fun getFertilizerList(): Call<FarmFertilizerResponse>?

    @GET(Constants.CROPS_CATEGORY)
    fun getCropCategoryList(): Call<CropCategoryResponse>?

    @GET(Constants.CROPS_TYPE_LIST)
    fun getCropTypeList(): Call<CropCategoryResponse>?

    @GET(Constants.CROPS_SUB_TYPE_LIST)
    fun getCropSubTypeList(@Query("crop_type_id") category_id: String): Call<CropSubTypeResponse>?

    @GET(Constants.CROPS_SUB_CATEGORY)
    fun getCropsSubCategoryList(@Query("category_id") category_id: String): Call<CropSubCategoryResponse>?

    @GET(Constants.GET_USER)
    fun getUser(@Header("Authorization") Authorization: String): Call<UserResponse>?

    @FormUrlEncoded
    @POST(Constants.FEEDBACK)
    fun giveFeedback(@Header("Authorization") Authorization: String, @Field("feedback") feedback: String): Call<VerifyOtpResponse>?

    @FormUrlEncoded
    @POST(Constants.ADD_FARM)
    fun addFarm(@FieldMap requestBody: HashMap<String, String>, @Header("Authorization") Authorization: String): Call<CommonResponse>?

    @FormUrlEncoded
    @POST(Constants.EDIT_FARM)
    fun editFarm(@FieldMap requestBody: HashMap<String, String>, @Header("Authorization") Authorization: String): Call<CommonResponse>?

    @GET(Constants.GET_FARM)
    fun getFarm(@Header("Authorization") Authorization: String): Call<MyFarmListResponse>?

    @FormUrlEncoded
    @POST(Constants.CROP_PROBLEM)
    fun getFarmProblemList(@FieldMap requestBody: HashMap<String, String>): Call<FarmProblemListResponse>?

    @FormUrlEncoded
    @POST(Constants.CROP_SOLUTION)
    fun getFarmSolutionsList(@Field("problem_id") problem_id: String): Call<FarmSolutionListResponse>?

    @GET(Constants.TOP_PRODUCT_LIST)
    fun topProductList(): Call<TopProductResponse>?

    @GET(Constants.CART_LIST)
    fun getCartList(@Header("Authorization") Authorization: String): Call<CartListResponse>?

    @GET(Constants.ADDRESS_LIST)
    fun getAddressList(@Header("Authorization") Authorization: String): Call<AddressListResponse>?

    @FormUrlEncoded
    @POST(Constants.TIPS_LIST)
    fun getTipList(@Field("crop_type_id") crop_type_id: String): Call<TipsListResponse>?

    @FormUrlEncoded
    @POST(Constants.EDIT_ADDRESS_LIST)
    fun editAddressList(@Header("Authorization") Authorization: String, @FieldMap requestBody: HashMap<String, String>): Call<CommonResponse>?

    @FormUrlEncoded
    @POST(Constants.ADD_ADDRESS_LIST)
    fun addAddress(@Header("Authorization") Authorization: String, @FieldMap requestBody: HashMap<String, String>): Call<CommonResponse>?

    @FormUrlEncoded
    @POST(Constants.ADD_PRODUCT)
    fun addProduct(@Header("Authorization") Authorization: String, @Field("pro_id") pro_id: String, @Field("unit_id") unit_id: String): Call<CommonResponse>?

    @FormUrlEncoded
    @POST(Constants.REMOVE_PRODUCT)
    fun removeProduct(@Header("Authorization") Authorization: String, @Field("pro_id") pro_id: String): Call<CommonResponse>?

    @FormUrlEncoded
    @POST(Constants.DELETE_PRODUCT)
    fun deleteProduct(@Header("Authorization") Authorization: String, @Field("pro_id") pro_id: String): Call<CommonResponse>?

    @GET(Constants.SUB_PRODUCT_LIST)
    fun subProduct(@QueryMap requestBody: HashMap<String, String>): Call<SubProductListResponse>?

    @GET(Constants.SUB_PRODUCT_LIST)
    fun orderDetails(@QueryMap requestBody: HashMap<String, String>): Call<TopProductResponse>?

    @FormUrlEncoded
    @POST(Constants.ALL_PRODUCT_LIST)
    fun allProduct(@FieldMap requestBody: HashMap<String, String>): Call<SubProductListResponse>?


    @FormUrlEncoded
    @POST(Constants.DELETE_FARM_LIST)
    fun deleteFarmlist(@Header("Authorization") authorization: String, @Field("farm_id") id: String): Call<CommonResponse>?

    @FormUrlEncoded
    @POST(Constants.DELETE_ADDRESS_LIST)
    fun deleteAddresslist(@Header("Authorization") authorization: String, @Field("address_id") id: String): Call<CommonResponse>?

    @FormUrlEncoded
    @POST(Constants.ADD_PRODUCT)
    fun addtoCartList(@Header("Authorization") authorization: String, @Field("pro_id") id: String,@Field("unit_id") unit_id: String): Call<CartCommonResponse>?

    @FormUrlEncoded
    @POST(Constants.PLACE_ORDER)
    fun placeOrder(@Header("Authorization") authorization: String, @FieldMap requestBody: HashMap<String, String>): Call<CommonResponse>?

    @GET(Constants.ORDER_LIST)
    fun orderList(@Header("Authorization") Authorization: String): Call<OrderListResponse>?

    @GET(Constants.ASK_EXPERT)
    fun askExpert(@Header("Authorization") Authorization: String): Call<CommonResponse>?

    @GET(Constants.TALUKA_LIST)
    fun talukaList(): Call<TalukaListResponse>?

    @FormUrlEncoded
    @POST(Constants.ADD_DRIP_SECTION)
    fun addDrip(@Header("Authorization") Authorization: String, @FieldMap requestBody: HashMap<String, String>): Call<CommonResponse>?

    @FormUrlEncoded
    @POST(Constants.CHANGE_MOBILE)
    fun changeMobile(@Header("Authorization") Authorization: String, @Field("mobile") id: String): Call<UpdateOtpResponse>?

    @FormUrlEncoded
    @POST(Constants.UPDATE_MOBILE)
    fun updateMobile(@Header("Authorization") Authorization: String, @Field("mobile") mobile: String, @Field("otp") otp: String): Call<UpdateOtpResponse>?

    @POST(Constants.ADD_PROBLEM)
    fun addProblemApi(@Body file: RequestBody?, @Header("Authorization") Authorization: String): Call<CommonResponse>?


}