package com.agrodrip.activity.drip

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
import com.agrodrip.activity.agroNews.AgroNewsActivity
import com.agrodrip.adapter.drip.GovernmentAdapter
import com.agrodrip.databinding.ActivityGovernmentSchemeListBinding
import com.agrodrip.model.GovernmentSchemeData
import com.agrodrip.model.GovernmentSchemeResponse
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

class GovernmentSchemeListActivity : BaseActivity() {

    private lateinit var binding: ActivityGovernmentSchemeListBinding
    private lateinit var imgback: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_government_scheme_list)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.gov_scheme)
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
        val call: Call<GovernmentSchemeResponse>? =
            ApiHandler.getApiService(this)?.getGovSchemeList()
        call?.enqueue(object : Callback<GovernmentSchemeResponse> {
            override fun onResponse(
                call: Call<GovernmentSchemeResponse>,
                response: Response<GovernmentSchemeResponse>
            ) {
                mDialog.dismiss()
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    Log.d("res", "res$resData")
                    val governmentAdapter =
                        GovernmentAdapter(this@GovernmentSchemeListActivity, resData.rows, object :
                            GovernmentAdapter.OnItemClick {
                            override fun onItemClick(governmentSchemeData: GovernmentSchemeData) {
                                val intent = Intent(this@GovernmentSchemeListActivity,
                                    GovernmentSchemeActivity::class.java
                                )
                                intent.putExtra("govShemeData", governmentSchemeData)
                                startActivity(intent)
                            }

                        })
                    binding.govSchemeListRecycler.adapter = governmentAdapter
                } else {
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<GovernmentSchemeResponse>, t: Throwable) {
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