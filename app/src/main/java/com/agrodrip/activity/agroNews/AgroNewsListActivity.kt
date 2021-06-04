package com.agrodrip.activity.agroNews

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
import com.agrodrip.adapter.agroNewsAdapter.AgroNewsMainAdapter
import com.agrodrip.databinding.ActivityAgroNewsListBinding
import com.agrodrip.model.NewsData
import com.agrodrip.model.NewsResponse
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


class AgroNewsListActivity : BaseActivity(), View.OnClickListener {

    private lateinit var agroNewsMainAdapter: AgroNewsMainAdapter
    private lateinit var imgback: ImageView
    private lateinit var binding: ActivityAgroNewsListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agro_news_list)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.agro_news)
        imgback = toolbar.findViewById<ImageView>(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener(this)
        setSupportActionBar(toolbar)

        initWidget()
    }

    private fun initWidget() {

        setNewsAdapter()
    }

    //News List
    private fun setNewsAdapter() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<NewsResponse>? = ApiHandler.getApiService(this)?.getNewsList()
        call?.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                mDialog.dismiss()
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    Log.d("res", "res$resData")
                    agroNewsMainAdapter = AgroNewsMainAdapter(this@AgroNewsListActivity, resData.rows, object :
                        AgroNewsMainAdapter.OnItemClick {
                        override fun onItemClick(newsData: NewsData) {
                            val intent = Intent(this@AgroNewsListActivity, AgroNewsActivity::class.java)
                            intent.putExtra("newsData", newsData)
                            startActivity(intent)
                        }

                    })
                    binding.agroNewsListRecycler.adapter = agroNewsMainAdapter
                } else {
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                mDialog.dismiss()
                toast(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            imgback -> {
                onBackPressed()
            }
        }
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
                setNewsAdapter()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@AgroNewsListActivity, MainActivity::class.java))
        finish()
    }
}