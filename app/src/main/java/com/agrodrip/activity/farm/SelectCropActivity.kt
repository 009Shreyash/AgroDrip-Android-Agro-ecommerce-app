package com.agrodrip.activity.farm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.adapter.cropsAdapter.CropCategoryAdapter
import com.agrodrip.adapter.cropsAdapter.CropsAdapter
import com.agrodrip.databinding.ActivitySelectCropBinding
import com.agrodrip.model.CropCategoryResponse
import com.agrodrip.model.CropSubCategoryData
import com.agrodrip.model.CropSubTypeResponse
import com.agrodrip.utils.Constants
import com.agrodrip.utils.Function
import com.agrodrip.utils.Pref
import com.agrodrip.utils.Utils
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import com.agrodrip.webservices.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SelectCropActivity : BaseActivity(), View.OnClickListener {

    private lateinit var imgback: ImageView
    private lateinit var categoryAdapter: CropCategoryAdapter
    private lateinit var cropsAdapter: CropsAdapter
    private lateinit var binding: ActivitySelectCropBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_crop)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.crops)
        imgback = toolbar.findViewById(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener(this)
        setSupportActionBar(toolbar)

        initWidget()
    }

    private fun initWidget() {
        getCropCategoryList()
        getCropList("1")
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


    private fun getCropCategoryList() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<CropCategoryResponse>? = ApiHandler.getApiService(this)?.getCropTypeList()
        call?.enqueue(object : Callback<CropCategoryResponse> {
            override fun onResponse(call: Call<CropCategoryResponse>, response: Response<CropCategoryResponse>) {
                mDialog.dismiss()
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    Log.d("res", "res$resData")
                    categoryAdapter = CropCategoryAdapter(this@SelectCropActivity, resData.rows, object : CropCategoryAdapter.GetCategoryListener {
                        override fun getCategoryId(categoryId: String) {
                            getCropList(categoryId)
                        }
                    })
                    binding.rvCropTypeList.adapter = categoryAdapter
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

    private fun getCropList(categoryId: String) {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<CropSubTypeResponse>? = ApiHandler.getApiService(this)?.getCropSubTypeList(categoryId)
        call?.enqueue(object : Callback<CropSubTypeResponse> {
            override fun onResponse(call: Call<CropSubTypeResponse>, response: Response<CropSubTypeResponse>) {
                mDialog.dismiss()
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    Log.d("res", "res$resData")
                    if (resData.code == 200) {
                        if (resData.rows.size > 0) {
                            cropsAdapter = CropsAdapter(this@SelectCropActivity, resData.rows, object : CropsAdapter.GetSubCategoryListener {
                                override fun getSubCategoryId(cropSubCategoryData: CropSubCategoryData) {
                                    val intent = Intent()
                                    intent.putExtra("cropSubCategoryData", cropSubCategoryData)
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }
                            })
                            binding.rvCropSubTypeList.adapter = cropsAdapter
                            binding.tvNoData.visibility = View.GONE
                            binding.rvCropSubTypeList.visibility = View.VISIBLE
                        } else {
                            binding.tvNoData.visibility = View.VISIBLE
                            binding.rvCropSubTypeList.visibility = View.GONE
                        }
                    } else {
                        binding.rvCropSubTypeList.visibility = View.GONE
                        binding.tvNoData.visibility = View.VISIBLE
                    }
                } else {
                    binding.rvCropSubTypeList.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<CropSubTypeResponse>, t: Throwable) {
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }
}