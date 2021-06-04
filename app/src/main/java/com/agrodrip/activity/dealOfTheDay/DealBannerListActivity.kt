package com.agrodrip.activity.dealOfTheDay

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
import com.agrodrip.activity.MainActivity
import com.agrodrip.adapter.bannerAdapter.DealBannerMainAdapter
import com.agrodrip.databinding.ActivityDealBannerListBinding
import com.agrodrip.model.BannerData
import com.agrodrip.model.BannerResponse
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


class DealBannerListActivity : BaseActivity(){

    private lateinit var dealBannerAdapter: DealBannerMainAdapter
    private lateinit var imgback: ImageView
    private lateinit var binding: ActivityDealBannerListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_deal_banner_list)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.deal_of_banner)
        imgback = toolbar.findViewById<ImageView>(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener{
            finish()
        }
        setSupportActionBar(toolbar)

        getBannerList()

    }


    private fun getBannerList() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<BannerResponse>? = ApiHandler.getApiService(this)?.getBannerList()
        call?.enqueue(object : Callback<BannerResponse> {
            override fun onResponse(call: Call<BannerResponse>, response: Response<BannerResponse>) {
                mDialog.dismiss()
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    Log.d("res", "res$resData")
                    dealBannerAdapter = DealBannerMainAdapter(this@DealBannerListActivity, resData.rows, object : DealBannerMainAdapter.OnItemClick {
                        override fun onItemClick(bannerData: BannerData) {
                            val intent = Intent(this@DealBannerListActivity, DealOfBannerActivity::class.java)
                            intent.putExtra("bannerData", bannerData)
                            startActivity(intent)
                        }
                    })
                    binding.bannerListRecycler.adapter = dealBannerAdapter
                } else {
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<BannerResponse>, t: Throwable) {
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
                getBannerList()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}