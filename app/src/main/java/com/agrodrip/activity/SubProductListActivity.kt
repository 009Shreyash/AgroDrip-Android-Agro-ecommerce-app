package com.agrodrip.activity

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
import com.agrodrip.adapter.SubProductAdapter
import com.agrodrip.databinding.ActivitySubProductListBinding
import com.agrodrip.model.CropSubCategoryData
import com.agrodrip.model.SubProductListData
import com.agrodrip.model.SubProductListResponse
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

class SubProductListActivity : BaseActivity() {
    private lateinit var imgback: ImageView
    private lateinit var binding: ActivitySubProductListBinding
    private lateinit var subProductListData: CropSubCategoryData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sub_product_list)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.video_gallary)
        imgback = toolbar.findViewById(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener { finish() }
        setSupportActionBar(toolbar)

        geSubProduct()
    }



    private fun geSubProduct() {
        subProductListData = intent.getParcelableExtra("SubProductListData") as CropSubCategoryData
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<SubProductListResponse>? = ApiHandler.getApiService(this)?.subProduct(getParams(subProductListData.category_id, subProductListData.id))
        call?.enqueue(object : Callback<SubProductListResponse> {
            override fun onResponse(call: Call<SubProductListResponse>, response: Response<SubProductListResponse>) {
                mDialog.dismiss()
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    Log.d("res", "res$resData")

                    val adapter = SubProductAdapter(this@SubProductListActivity, resData.rows,object : SubProductAdapter.OnItemClick{
                        override fun onItemClick(topProductData: SubProductListData) {

                        }
                    })
                    binding.rvSubProduct.adapter = adapter
                } else {
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
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
        param["sub_id"] = id
        return param
    }
}