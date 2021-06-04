package com.agrodrip.activity.agroNews

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.StrictMode
import android.text.Html
import android.view.View
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.BuildConfig
import com.agrodrip.R
import com.agrodrip.activity.WatchDemoActivity
import com.agrodrip.databinding.ActivityAgroNewsBinding
import com.agrodrip.model.NewsData
import com.agrodrip.utils.Function
import com.agrodrip.utils.LocaleManager
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso


class AgroNewsActivity : BaseActivity() {

    private lateinit var binding: ActivityAgroNewsBinding
    private lateinit var newsData: NewsData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agro_news)

        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        initWidget()

    }


    @SuppressLint("SetTextI18n")
    private fun initWidget() {

        newsData = intent.getParcelableExtra("newsData") as NewsData
        binding.imgBack.setOnClickListener { finish() }
        binding.shareNews.setOnClickListener { shareNewsData(newsData) }
        binding.toolbarTxtMore.setOnClickListener {
            startActivity(Intent(this, AgroNewsListActivity::class.java))
            finish()
        }

        if (newsData.video_url != "") {
            binding.txtViewVideo.visibility = View.VISIBLE

            binding.txtViewVideo.setOnClickListener {
                val intent = Intent(this, WatchDemoActivity::class.java)
                intent.putExtra("videoUrl", newsData.video_url)
                startActivity(intent)
            }
        } else {
            binding.txtViewVideo.visibility = View.GONE
        }
        val description_en = Html.fromHtml(newsData.description_en)
        val description_gu = Html.fromHtml(newsData.description_gu)

        if (LocaleManager.getLanguagePref(this) == LocaleManager.ENGLISH) {
            binding.txtNewsTitle.text = newsData.news_title_en
            binding.tvNewsDesc.text = description_en
        } else {
            binding.txtNewsTitle.text = newsData.news_title_gu
            binding.tvNewsDesc.text = description_gu
        }

        binding.txtNewsDate.text = newsData.date + " " + getString(R.string.txt_by_agrodrip)
        Glide.with(this)
            .load(newsData.news_image)
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .into(binding.imgAgroNews)

    }

    private fun shareNewsData(newsData: NewsData) {

        Picasso.get().load(newsData.news_image).into(object : com.squareup.picasso.Target {
            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {

            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_STREAM, Function.getLocalBitmapUri(bitmap, this@AgroNewsActivity))

                val description_en = Html.fromHtml(newsData.description_en)
                val description_gu = Html.fromHtml(newsData.description_en)

                if (LocaleManager.getLanguagePref(this@AgroNewsActivity) == LocaleManager.ENGLISH){
                    intent.putExtra(Intent.EXTRA_TEXT, newsData.news_title_en + " \n\n " + description_en + " \n\n "
                            + "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                }else{
                    intent.putExtra(Intent.EXTRA_TEXT, newsData.news_title_en + " \n\n " + description_gu +
                            " \n\n " + "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                }

                startActivity(Intent.createChooser(intent, getString(R.string.share_neews_by_txt)))
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}