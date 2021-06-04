package com.agrodrip.activity.drip

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.adapter.drip.IssueDripAdapter
import com.agrodrip.databinding.ActivityIssueDripListBinding
import com.agrodrip.model.IssueDripData
import com.agrodrip.model.IssueDripResponse
import com.agrodrip.utils.Utils
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import com.agrodrip.webservices.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IssueDripListActivity : BaseActivity() {

    private lateinit var binding: ActivityIssueDripListBinding
    private lateinit var imgback: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_issue_drip_list)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.issue_drip)
        imgback = toolbar.findViewById<ImageView>(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener {
            finish()
        }
        setSupportActionBar(toolbar)

        setAdapter()
    }

    //News List
    private fun setAdapter() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<IssueDripResponse>? =
            ApiHandler.getApiService(this)?.getIssueDripList()
        call?.enqueue(object : Callback<IssueDripResponse> {
            override fun onResponse(call: Call<IssueDripResponse>, response: Response<IssueDripResponse>) {
                mDialog.dismiss()
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    Log.d("res", "res$resData")
                    val issDripAdapter = IssueDripAdapter(this@IssueDripListActivity, resData.rows, object : IssueDripAdapter.OnItemClick {
                        override fun onItemClick(issueDripData: IssueDripData) {
                            val intent = Intent(this@IssueDripListActivity, IssueDripActivity::class.java)
                            intent.putExtra("issueDripData", issueDripData)
                            startActivity(intent)
                        }

                    })
                    binding.issueDripListRecycler.adapter = issDripAdapter
                } else {
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<IssueDripResponse>, t: Throwable) {
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
}