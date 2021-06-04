package com.agrodrip.activity.drip

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
import com.agrodrip.databinding.ActivityGovernmentSchemeBinding
import com.agrodrip.model.GovernmentSchemeData
import com.agrodrip.utils.Function
import com.agrodrip.utils.LocaleManager
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso

class GovernmentSchemeActivity : BaseActivity() {

    private lateinit var binding: ActivityGovernmentSchemeBinding
    private lateinit var governmentSchemeData: GovernmentSchemeData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_government_scheme)

        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        initWidget()
    }

    @SuppressLint("SetTextI18n")
    private fun initWidget() {

        governmentSchemeData = intent.getParcelableExtra("govShemeData") as GovernmentSchemeData
        binding.imgBack.setOnClickListener { finish() }
        binding.shareGovScheme.setOnClickListener { shareNewsData(governmentSchemeData) }
        binding.toolbarTxtMore.setOnClickListener {
            startActivity(Intent(this, GovernmentSchemeListActivity::class.java))
            finish()
        }

        if (governmentSchemeData.video_url != "") {
            binding.txtViewVideo.visibility = View.VISIBLE

            binding.txtViewVideo.setOnClickListener {
                val intent = Intent(this, WatchDemoActivity::class.java)
                intent.putExtra("videoUrl", governmentSchemeData.video_url)
                startActivity(intent)
            }
        } else {
            binding.txtViewVideo.visibility = View.GONE
        }


        val description_en = Html.fromHtml(governmentSchemeData.description_en)
        val description_gu = Html.fromHtml(governmentSchemeData.description_gu)

        if (LocaleManager.getLanguagePref(this) == LocaleManager.ENGLISH) {
            binding.txtGovSchemeTitle.text = governmentSchemeData.scheme_title_en
            binding.tvGovSchemeDesc.text = description_en
        } else {
            binding.txtGovSchemeTitle.text = governmentSchemeData.scheme_title_gu
            binding.tvGovSchemeDesc.text = description_gu
        }

        binding.txtGovSchemeDate.text = governmentSchemeData.date + " " +
                getString(R.string.txt_by_agrodrip)

        Glide.with(this)
            .load(governmentSchemeData.scheme_image)
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .into(binding.imgGovScheme)

    }

    private fun shareNewsData(governmentSchemeData: GovernmentSchemeData) {

        Picasso.get().load(governmentSchemeData.scheme_image)
            .into(object : com.squareup.picasso.Target {
                override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {

                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "image/*"
                    intent.putExtra(
                        Intent.EXTRA_STREAM,
                        Function.getLocalBitmapUri(bitmap, this@GovernmentSchemeActivity)
                    )

                    if (LocaleManager.getLanguagePref(this@GovernmentSchemeActivity) == LocaleManager.ENGLISH) {
                        intent.putExtra(
                            Intent.EXTRA_TEXT, governmentSchemeData.scheme_title_en + " \n\n " +
                                    governmentSchemeData.description_en + " \n\n " + "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                        )
                    } else {
                        intent.putExtra(
                            Intent.EXTRA_TEXT, governmentSchemeData.scheme_title_gu + " \n\n " +
                                    governmentSchemeData.description_gu + " \n\n " + "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                        )
                    }

                    startActivity(
                        Intent.createChooser(
                            intent,
                            getString(R.string.share_neews_by_txt)
                        )
                    )
                }
            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}