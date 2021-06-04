package com.agrodrip.activity.farm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.activity.SubDetailCategoryActivity
import com.agrodrip.adapter.TopProductAdapter
import com.agrodrip.adapter.cropsAdapter.CropCategoryAdapter
import com.agrodrip.databinding.ActivitySpecialProductBinding
import com.agrodrip.model.CropCategoryResponse
import com.agrodrip.model.TopProductData
import com.agrodrip.model.TopProductResponse
import com.agrodrip.utils.*
import com.agrodrip.utils.Function
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import com.agrodrip.webservices.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SpecialProductActivity : BaseActivity(), View.OnClickListener {

    private lateinit var imgback: ImageView
    private lateinit var binding: ActivitySpecialProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_special_product)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.market_place)
        imgback = toolbar.findViewById<ImageView>(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener(this)
        setSupportActionBar(toolbar)

        initWidget()

    }

    private fun initWidget() {

        setTopProductAdapter()
        getCropCategoryList()
    }


    private fun setTopProductAdapter() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<TopProductResponse>? = ApiHandler.getApiService(this)?.topProductList()
        call?.enqueue(object : Callback<TopProductResponse> {
            override fun onResponse(call: Call<TopProductResponse>, response: Response<TopProductResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    val topProductAdapter = TopProductAdapter(this@SpecialProductActivity, resData.rows, "Market", object : TopProductAdapter.OnItemClick {
                        override fun onItemClick(topProductData: TopProductData) {
                            val intent = Intent(this@SpecialProductActivity, SolutionProductActivity::class.java)
                            intent.putExtra("topProductData", topProductData)
                            intent.putExtra("isFrom", "MarketPlaceFragment")
                            startActivity(intent)
                        }

                    })
                    val itemDecoration = ItemOffsetDecoration(this@SpecialProductActivity, R.dimen._2sdp)
                    binding.rvTopProduct.addItemDecoration(itemDecoration)
                    binding.rvTopProduct.adapter = topProductAdapter
                } else {
                    if (response.code() == 400) {
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

    private fun getCropCategoryList() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<CropCategoryResponse>? = ApiHandler.getApiService(this)?.getCropCategoryList()
        call?.enqueue(object : Callback<CropCategoryResponse> {
            override fun onResponse(call: Call<CropCategoryResponse>, response: Response<CropCategoryResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    val categoryAdapter = CropCategoryAdapter(this@SpecialProductActivity, resData.rows, object : CropCategoryAdapter.GetCategoryListener {
                        override fun getCategoryId(categoryId: String) {
                            val intent = Intent(this@SpecialProductActivity, SubDetailCategoryActivity::class.java)
                            intent.putExtra("categoryId", categoryId)
                            startActivity(intent)
                        }
                    })
                    binding.productCategoryRecycler.adapter = categoryAdapter
                } else {
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<CropCategoryResponse>, t: Throwable) {
                mDialog.dismiss()
                toast(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val actionSettings = menu!!.findItem(R.id.action_settings)
        actionSettings.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                setTopProductAdapter()
                getCropCategoryList()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            imgback -> {
                finish()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}