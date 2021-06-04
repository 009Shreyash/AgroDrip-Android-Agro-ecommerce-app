package com.agrodrip.activity.farm

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.BuildConfig
import com.agrodrip.R
import com.agrodrip.activity.CartListActivity
import com.agrodrip.adapter.ProductImageAdapter
import com.agrodrip.adapter.UnitSelectAdapter
import com.agrodrip.databinding.ActivitySolutionProductBinding
import com.agrodrip.model.*
import com.agrodrip.utils.*
import com.agrodrip.utils.Function
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import com.agrodrip.webservices.toast
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SolutionProductActivity : BaseActivity(), View.OnClickListener {

    private lateinit var toolbarText: TextView
    private lateinit var solutionList: SolutionList
    private lateinit var topProductData: TopProductData
    private lateinit var subProductListData: SubProductListData
    private lateinit var orderListData: OrderListData
    private lateinit var imgback: ImageView
    private lateinit var ivCart: ImageView
    private lateinit var productId: String
    private var unitId: String = ""
    private lateinit var binding: ActivitySolutionProductBinding
    private lateinit var data: UserData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_solution_product)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbarText = toolbar.findViewById(R.id.toolbarText)
        ivCart = toolbar.findViewById(R.id.iv_cart)
        imgback = toolbar.findViewById(R.id.imgBack)
        imgback.visibility = View.VISIBLE
//        ivCart.visibility = View.VISIBLE
        imgback.setOnClickListener { onBackPressed() }
        ivCart.setOnClickListener {
            val intent = Intent(this@SolutionProductActivity, CartListActivity::class.java)
            startActivity(intent)
        }
        setSupportActionBar(toolbar)

        initWidget()
        invalidateOptionsMenu()
    }


    private fun initWidget() {
        data = Pref.getUserData(this)!!
        binding.llAddCart.setOnClickListener(this)
        when {
            intent.getStringExtra("isFrom") == "MarketPlaceFragment" -> {
                topProductData = intent.getParcelableExtra("topProductData") as TopProductData
                toolbarText.text = resources.getString(R.string.product_detail)

                val description_en = Html.fromHtml(topProductData.pro_description_en)
                val description_gu = Html.fromHtml(topProductData.pro_description_gu)

                if (LocaleManager.getLanguagePref(this) == LocaleManager.ENGLISH) {
                    binding.txtProductTitle.text = topProductData.pro_name_en
                    binding.txtCompanyName.text = topProductData.company_name
                    binding.txtProductDesc.text = description_en
                } else {
                    binding.txtProductTitle.text = topProductData.pro_name_gu
                    binding.txtCompanyName.text = topProductData.company_name
                    binding.txtProductDesc.text = description_gu
                }
                binding.txtProductPrice.text = topProductData.price_gu + " " + getString(R.string.Rs)
                binding.txtProductPriceDesc.text = topProductData.price_main
                binding.txtProductPriceDesc.paintFlags = binding.txtProductPriceDesc.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                productId = topProductData.id
                val dealBannerAdapter = ProductImageAdapter(this, topProductData.pro_image)
                binding.sliderView.setSliderAdapter(dealBannerAdapter)
                binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.DROP)
                binding.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                binding.sliderView.autoCycleDirection =
                    SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH

                binding.sliderView.setOnIndicatorClickListener {
                    Log.i("GGG", "onIndicatorClicked: " + binding.sliderView.currentPagePosition)
                }

                binding.shareProduct.setOnClickListener {
                    shareProductData(topProductData)
                }

                val adapter = UnitSelectAdapter(
                    this,
                    topProductData.unit_row,
                    object : UnitSelectAdapter.OnItemClick {
                        override fun onItemClick(unitRow: UnitRow) {
                            unitId = unitRow.id
                        }
                    })
                binding.packingListRecycler.adapter = adapter


            }
            intent.getStringExtra("isFrom") == "SubDetailCategoryActivity" -> {
                subProductListData =
                    intent.getParcelableExtra("topProductData") as SubProductListData
                toolbarText.text = resources.getString(R.string.product_detail)

                val description_en = Html.fromHtml(subProductListData.pro_description_en)
                val description_gu = Html.fromHtml(subProductListData.pro_description_gu)

                binding.txtProductPrice.text = subProductListData.price_gu + " " + getString(R.string.Rs)
                binding.txtProductPriceDesc.text = topProductData.price_main
                binding.txtProductPriceDesc.paintFlags = binding.txtProductPriceDesc.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                if (LocaleManager.getLanguagePref(this) == LocaleManager.ENGLISH) {
                    binding.txtProductTitle.text = subProductListData.pro_name_en
                    binding.txtCompanyName.text = subProductListData.company_name
                    binding.txtProductDesc.text = description_en

                } else {
                    binding.txtProductTitle.text = subProductListData.pro_name_gu
                    binding.txtCompanyName.text = subProductListData.company_name
                    binding.txtProductDesc.text = description_gu
                }


                val dealBannerAdapter = ProductImageAdapter(this, subProductListData.pro_image!!)
                binding.sliderView.setSliderAdapter(dealBannerAdapter)
                binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.DROP)
                binding.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                binding.sliderView.autoCycleDirection =
                    SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH

                productId = subProductListData.id
                binding.shareProduct.setOnClickListener {
                    shareProductData(subProductListData)
                }

                val adapter = UnitSelectAdapter(
                    this,
                    subProductListData.unit_row,
                    object : UnitSelectAdapter.OnItemClick {
                        override fun onItemClick(unitRow: UnitRow) {
                            unitId = unitRow.id
                        }
                    })
                binding.packingListRecycler.adapter = adapter
            }
            intent.getStringExtra("isFrom") == "OrderDetailsActivity" -> {
                orderListData = intent.getParcelableExtra("orderListData") as OrderListData
                getDetails()
            }
            else -> {
                solutionList = intent.getParcelableExtra("solutionData") as SolutionList
                toolbarText.text = resources.getString(R.string.product_detail)

                val description_en = Html.fromHtml(solutionList.pro_description_en)
                val description_gu = Html.fromHtml(solutionList.pro_description_gu)

                binding.txtProductPrice.text = solutionList.price_gu + " " + getString(R.string.Rs)
                binding.txtProductPriceDesc.text = solutionList.price_main
                binding.txtProductPriceDesc.paintFlags = binding.txtProductPriceDesc.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                if (LocaleManager.getLanguagePref(this) == LocaleManager.ENGLISH) {
                    binding.txtProductTitle.text = solutionList.pro_name_en
                    binding.txtCompanyName.text = solutionList.company_name
                    binding.txtProductDesc.text = description_en
                } else {
                    binding.txtProductTitle.text = solutionList.pro_name_gu
                    binding.txtCompanyName.text = solutionList.company_name
                    binding.txtProductDesc.text = description_gu
                }

                val dealBannerAdapter = ProductImageAdapter(this, solutionList.pro_image)
                binding.sliderView.setSliderAdapter(dealBannerAdapter)
                binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.DROP)
                binding.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                binding.sliderView.autoCycleDirection =
                    SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
                binding.shareProduct.setOnClickListener {
                    shareProductData(solutionList)
                }
                productId = solutionList.id

                val adapter = UnitSelectAdapter(
                    this,
                    solutionList.unit_row,
                    object : UnitSelectAdapter.OnItemClick {
                        override fun onItemClick(unitRow: UnitRow) {
                            unitId = unitRow.id
                        }
                    })
                binding.packingListRecycler.adapter = adapter
            }
        }
        binding.llAskExpert.setOnClickListener {
            AskExpertDialog()
        }
    }

    private fun AskExpertDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_layout)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )

        val tvTitle = dialog.findViewById(R.id.tv_title) as TextView
        val tvDesc = dialog.findViewById(R.id.tv_desc) as TextView
        val textYes = dialog.findViewById(R.id.txtYes) as TextView
        val textNo = dialog.findViewById(R.id.txtNo) as TextView

        tvTitle.text = getString(R.string.title_customer_service)
        tvDesc.text = getString(R.string.desc_customer_service)

        textYes.setOnClickListener {
            askExpert()
            dialog.dismiss()
        }

        textNo.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun getDetails() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<TopProductResponse>? =
            ApiHandler.getApiService(this)?.orderDetails(getParams())
        call?.enqueue(object : Callback<TopProductResponse> {
            override fun onResponse(
                call: Call<TopProductResponse>,
                response: Response<TopProductResponse>
            ) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    val list = resData.rows

                    val description_en = Html.fromHtml(list[0].pro_description_en)
                    val description_gu = Html.fromHtml(list[0].pro_description_gu)

                    if (LocaleManager.getLanguagePref(this@SolutionProductActivity) == LocaleManager.ENGLISH) {
                        binding.txtProductTitle.text = list[0].pro_name_en
                        binding.txtCompanyName.text = list[0].company_name
                        binding.txtProductDesc.text = description_en
                        binding.txtProductPrice.text =
                            list[0].price_gu + " " + getString(R.string.Rs)
                    } else {
                        binding.txtProductTitle.text = list[0].pro_name_gu
                        binding.txtCompanyName.text = list[0].company_name
                        binding.txtProductDesc.text = description_gu
                        binding.txtProductPrice.text =
                            list[0].price_gu + " " + getString(R.string.Rs)
                    }


                    val dealBannerAdapter =
                        ProductImageAdapter(this@SolutionProductActivity, list[0].pro_image)
                    binding.sliderView.setSliderAdapter(dealBannerAdapter)
                    binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.DROP)
                    binding.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                    binding.sliderView.autoCycleDirection =
                        SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
                    binding.shareProduct.setOnClickListener {
                        shareProductData(list[0])
                    }
                    productId = list[0].id
                } else {
                    if (response.code() == 400) {
                        mDialog.dismiss()
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<TopProductResponse>, t: Throwable) {
                mDialog.dismiss()
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    private fun getParams(): HashMap<String, String> {
        val param = HashMap<String, String>()
        param["category_id"] = orderListData.order_row[0].product.category_id
        param["sub_category_id"] = orderListData.order_row[0].product.sub_category_id
        param["page_no"] = "0"
        param["pro_id"] = orderListData.order_row[0].product.id
        return param
    }

    private fun askExpert() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<CommonResponse>? =
            ApiHandler.getApiService(this)?.askExpert("Bearer " + data.app_key)
        call?.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    binding.root.snackBar(getString(R.string.inqirey_added_ask_expert))
                } else {
                    if (response.code() == 400) {
                        mDialog.dismiss()
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                mDialog.dismiss()
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    private fun shareProductData(solutionList: SolutionList?) {
        Picasso.get().load(solutionList!!.pro_image[0]).into(object : com.squareup.picasso.Target {
            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {}
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.type = "image/*"
                intent.putExtra(
                    Intent.EXTRA_STREAM,
                    Function.getLocalBitmapUri(bitmap, this@SolutionProductActivity)
                )

                val description_en = Html.fromHtml(solutionList.pro_description_en)
                val description_gu = Html.fromHtml(solutionList.pro_description_gu)
                if (LocaleManager.getLanguagePref(this@SolutionProductActivity) == LocaleManager.ENGLISH) {
                    intent.putExtra(
                        Intent.EXTRA_TEXT,
                        solutionList.pro_name_en + " \n\n " + description_en + " \n\n " + "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                    )
                } else {
                    intent.putExtra(
                        Intent.EXTRA_TEXT,
                        solutionList.pro_name_gu + " \n\n " + description_gu + " \n\n " + "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                    )
                }

                startActivity(Intent.createChooser(intent, "Share Product by..."))
            }
        })
    }

    private fun shareProductData(topProductData: TopProductData?) {
        Picasso.get().load(topProductData!!.pro_image[0])
            .into(object : com.squareup.picasso.Target {
                override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {}
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.type = "image/*"
                    intent.putExtra(
                        Intent.EXTRA_STREAM,
                        Function.getLocalBitmapUri(bitmap, this@SolutionProductActivity)
                    )

                    val description_en = Html.fromHtml(topProductData.pro_description_en)
                    val description_gu = Html.fromHtml(topProductData.pro_description_gu)
                    if (LocaleManager.getLanguagePref(this@SolutionProductActivity) == LocaleManager.ENGLISH) {
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            topProductData.pro_name_en + " \n\n " + description_en + " \n\n " +
                                    "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                        )
                    } else {
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            topProductData.pro_name_gu + " \n\n " + description_gu + " \n\n " +
                                    "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                        )
                    }

                    startActivity(Intent.createChooser(intent, "Share Product by..."))
                }
            })
    }

    private fun shareProductData(topProductData: SubProductListData?) {
        Picasso.get().load(topProductData!!.pro_image!![0])
            .into(object : com.squareup.picasso.Target {
                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "image/*"
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.putExtra(
                        Intent.EXTRA_STREAM,
                        Function.getLocalBitmapUri(bitmap, this@SolutionProductActivity)
                    )

                    val description_en = Html.fromHtml(topProductData.pro_description_en)
                    val description_gu = Html.fromHtml(topProductData.pro_description_gu)
                    if (LocaleManager.getLanguagePref(this@SolutionProductActivity) == LocaleManager.ENGLISH) {
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            topProductData.pro_name_en + " \n\n " + description_en + " \n\n " + "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                        )
                    } else {
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            topProductData.pro_name_gu + " \n\n " + description_gu + " \n\n " + "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                        )
                    }

                    startActivity(Intent.createChooser(intent, "Share Product by..."))
                }
            })
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.ll_add_cart) {
            if (unitId.isEmpty()) {
                binding.root.snackBar("Select packaging")
            } else
                addProductAPI()
        }
    }

    private fun addProductAPI() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<CartCommonResponse>? = ApiHandler.getApiService(this)
            ?.addtoCartList("Bearer " + data.app_key, productId, unitId)
        call?.enqueue(object : Callback<CartCommonResponse> {
            override fun onResponse(
                call: Call<CartCommonResponse>,
                response: Response<CartCommonResponse>
            ) {
                mDialog.dismiss()
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    Log.d("res", "res$resData")
                    binding.root.snackBar("Product added successfully.")
                    Pref.getValue(this@SolutionProductActivity, Constants.PREF_BADGE, resData.count)
                    val intent = Intent(this@SolutionProductActivity, CartListActivity::class.java)
                    startActivity(intent)
                } else {
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<CartCommonResponse>, t: Throwable) {
                mDialog.dismiss()
                toast(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

}