package com.agrodrip.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.activity.agroNews.AgroNewsActivity
import com.agrodrip.adapter.VideoGalleryAdapter
import com.agrodrip.databinding.ActivityVideoGalleryBinding
import com.agrodrip.model.NewsData
import com.agrodrip.model.VideoListResponse
import com.agrodrip.utils.Utils
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import com.agrodrip.webservices.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoGalleryActivity : BaseActivity() {

    private lateinit var videoGallaryAdapter: VideoGalleryAdapter
    private lateinit var imgback: ImageView
    private lateinit var binding: ActivityVideoGalleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_gallery)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.video_gallary)
        imgback = toolbar.findViewById<ImageView>(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener {
            finish()
        }
        setSupportActionBar(toolbar)
        getVideoList()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    private fun getVideoList() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<VideoListResponse>? = ApiHandler.getApiService(this)?.getVideoList()
        call?.enqueue(object : Callback<VideoListResponse> {
            override fun onResponse(
                call: Call<VideoListResponse>,
                response: Response<VideoListResponse>
            ) {
                mDialog.dismiss()
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    Log.d("res", "res$resData")

                    val alist = ArrayList<String>()
                    val titleList = ArrayList<String>()
                    resData.rows.map {
                        alist.add(it.url)
                        titleList.add(it.title)
                    }
                    videoGallaryAdapter =
                        VideoGalleryAdapter(this@VideoGalleryActivity, alist, titleList, object :
                            VideoGalleryAdapter.OnItemClick {
                            override fun onItemClick(youtubeId: String) {
                                val intent =
                                    Intent(this@VideoGalleryActivity, WatchDemoActivity::class.java)
                                intent.putExtra("videoUrl", youtubeId)
                                startActivity(intent)
                            }
                        })

                    binding.videosListRecycler.adapter = videoGallaryAdapter
                } else {
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<VideoListResponse>, t: Throwable) {
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
                getVideoList()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}