package com.agrodrip.activity

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.activity.farm.SolutionProductActivity
import com.agrodrip.adapter.CropSubCategoryAdapter
import com.agrodrip.adapter.SubProductAdapter
import com.agrodrip.databinding.ActivitySubDetailcategoryBinding
import com.agrodrip.model.CropSubCategoryData
import com.agrodrip.model.CropSubCategoryResponse
import com.agrodrip.model.SubProductListData
import com.agrodrip.model.SubProductListResponse
import com.agrodrip.utils.*
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubDetailCategoryActivity : BaseActivity() {
    private var categoryId: String? = ""
    private lateinit var imgback: ImageView
    private lateinit var binding: ActivitySubDetailcategoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sub_detailcategory)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.crops)
        imgback = toolbar.findViewById(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener { finish() }
        setSupportActionBar(toolbar)

        initWidget()
    }

    private fun initWidget() {
        categoryId = intent.getStringExtra("categoryId")
        if (!TextUtils.isEmpty(categoryId)) {
            getCropCategoryList(categoryId!!)
        }

        binding.llAllProduct.setOnClickListener {
            binding.rlSubCategory.visibility = View.GONE
            binding.rlAllProduct.visibility = View.VISIBLE
        }

        if (binding.llAllProduct.isVisible) {
            getAllProduct(categoryId!!)
        }
    }

    private fun getAllProduct(categoryId: String) {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<SubProductListResponse>? = ApiHandler.getApiService(this)?.allProduct(getAllProductParams(categoryId))
        call?.enqueue(object : Callback<SubProductListResponse> {
            override fun onResponse(call: Call<SubProductListResponse>, response: Response<SubProductListResponse>) {
                mDialog.dismiss()
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    Log.d("res", "res$resData")
                    val res = resData.rows
                    val adapter = SubProductAdapter(this@SubDetailCategoryActivity, res, object : SubProductAdapter.OnItemClick {
                        override fun onItemClick(topProductData: SubProductListData) {
                            val intent = Intent(this@SubDetailCategoryActivity, SolutionProductActivity::class.java)
                            intent.putExtra("topProductData", topProductData)
                            intent.putExtra("isFrom", "SubDetailCategoryActivity")
                            startActivity(intent)
                        }
                    })
                    binding.rvAllProduct.adapter = adapter
                } else {
                    if (response.code() == 400) {
                        binding.rlAllProduct.visibility = View.GONE
                        binding.llAllProduct.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<SubProductListResponse>, t: Throwable) {
                mDialog.dismiss()
                toast(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    private fun getCropCategoryList(categoryId: String) {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<CropSubCategoryResponse>? = ApiHandler.getApiService(this)?.getCropsSubCategoryList(categoryId)
        call?.enqueue(object : Callback<CropSubCategoryResponse> {
            override fun onResponse(call: Call<CropSubCategoryResponse>, response: Response<CropSubCategoryResponse>) {
                mDialog.dismiss()
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    Log.d("res", "res$resData")
                    val categoryAdapter = CropSubCategoryAdapter(this@SubDetailCategoryActivity, resData.rows, object : CropSubCategoryAdapter.GetCategoryListener {
                        override fun getCategoryId(cropSubCategoryData: CropSubCategoryData) {
                            binding.rlSubCategory.visibility = View.VISIBLE
                            binding.rlAllProduct.visibility = View.GONE

                            if (binding.rlSubCategory.isVisible) {
                                getCropList(cropSubCategoryData.category_id, cropSubCategoryData.id)
                            }
                        }
                    })
                    val itemDecoration = ItemOffsetDecoration(this@SubDetailCategoryActivity, R.dimen._2sdp)
                    binding.productCategoryRecycler.addItemDecoration(itemDecoration)
                    binding.productCategoryRecycler.adapter = categoryAdapter
                } else {
                    if (response.code() == 400) {
                        binding.productCategoryRecycler.visibility = View.GONE
                        binding.tvNoData.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<CropSubCategoryResponse>, t: Throwable) {
                mDialog.dismiss()
                toast(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    private fun getCropList(categoryId: String, subId: String) {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<SubProductListResponse>? = ApiHandler.getApiService(this)?.subProduct(getParams(categoryId, subId))
        call?.enqueue(object : Callback<SubProductListResponse> {
            override fun onResponse(call: Call<SubProductListResponse>, response: Response<SubProductListResponse>) {
                mDialog.dismiss()
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    Log.d("res", "res$resData")

                    val adapter = SubProductAdapter(this@SubDetailCategoryActivity, resData.rows, object : SubProductAdapter.OnItemClick {
                        override fun onItemClick(topProductData: SubProductListData) {
                            val intent = Intent(this@SubDetailCategoryActivity, SolutionProductActivity::class.java)
                            intent.putExtra("topProductData", topProductData)
                            intent.putExtra("isFrom", "SubDetailCategoryActivity")
                            startActivity(intent)
                        }
                    })
                    binding.productRecycler.adapter = adapter
                } else {
                    if (response.code() == 400) {
                        binding.productRecycler.visibility = View.GONE
                        binding.tvNoData.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<SubProductListResponse>, t: Throwable) {
                mDialog.dismiss()
                toast(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    private fun getParams(categoryId: String, id: String): HashMap<String, String> {
        val param = HashMap<String, String>()
        param["category_id"] = categoryId
        param["sub_category_id"] = id
        param["page_no"] = "0"
        return param
    }

    private fun getAllProductParams(categoryId: String): HashMap<String, String> {
        val param = HashMap<String, String>()
        param["category_id"] = categoryId
        param["page_no"] = "0"
        return param
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        invalidateOptionsMenu()
        menuInflater.inflate(R.menu.toolbar_main, menu)
        val itemCart = menu!!.findItem(R.id.action_cart)
        val icon: LayerDrawable = itemCart.icon as LayerDrawable
        setBadgeCount(this, icon, Pref.getValue(this, Constants.PREF_BADGE,""))
        return super.onCreateOptionsMenu(menu)
    }

    private fun setBadgeCount(context: Context?, icon: LayerDrawable, count: String?) {
        val badge: BadgeDrawable
        val reuse: Drawable? = icon.findDrawableByLayerId(R.id.ic_badge)
        if (reuse != null && reuse is BadgeDrawable) {
            badge = reuse
        } else {
            badge = BadgeDrawable(context)
        }
        badge.setCount(count)
        icon.mutate()
        icon.setDrawableByLayerId(R.id.ic_badge, badge)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_cart) {
            val intent = Intent(this@SubDetailCategoryActivity, CartListActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}