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
import com.agrodrip.adapter.farmsAdapter.TipsMainAdapter
import com.agrodrip.databinding.ActivityTipsListBinding
import com.agrodrip.model.TipsListData
import com.agrodrip.model.TipsListResponse
import com.agrodrip.utils.Constants
import com.agrodrip.utils.Function
import com.agrodrip.utils.Pref
import com.agrodrip.utils.Utils
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TipsListActivity : BaseActivity() {

    private lateinit var imgback: ImageView
    private lateinit var tipsMainAdapter: TipsMainAdapter
    private lateinit var binding: ActivityTipsListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tips_list)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.crop_tips)
        imgback = toolbar.findViewById<ImageView>(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener { onBackPressed() }
        setSupportActionBar(toolbar)

        setTipsAdapter()
    }

    private fun setTipsAdapter() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<TipsListResponse>? = ApiHandler.getApiService(this)?.getTipList("1")
        call?.enqueue(object : Callback<TipsListResponse> {
            override fun onResponse(call: Call<TipsListResponse>, response: Response<TipsListResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    tipsMainAdapter = TipsMainAdapter(this@TipsListActivity, resData.rows.tips, object : TipsMainAdapter.OnItemClick {
                        override fun onItemClick(tipsListData: TipsListData) {
                            val intent = Intent(this@TipsListActivity, TipsViewActivity::class.java)
                            intent.putExtra("tipsListData", tipsListData)
                            startActivity(intent)
                        }

                    })
                    binding.tipsListRecycler.adapter = tipsMainAdapter
                } else {
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<TipsListResponse>, t: Throwable) {
                mDialog.dismiss()
                binding.root.snackBar(getString(R.string.msg_internet))
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
                setTipsAdapter()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}