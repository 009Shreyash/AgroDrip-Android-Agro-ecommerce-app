package com.agrodrip.activity.dealOfTheDay

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.BuildConfig
import com.agrodrip.R
import com.agrodrip.databinding.ActivityDealOfBannerBinding
import com.agrodrip.model.BannerData
import com.agrodrip.utils.Constants
import com.agrodrip.utils.Function.getLocalBitmapUri
import com.agrodrip.utils.LocaleManager
import com.agrodrip.utils.Pref
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso


class DealOfBannerActivity : BaseActivity() {
    private lateinit var binding: ActivityDealOfBannerBinding
    private lateinit var bannerData: BannerData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_deal_of_banner)

        initWidget()
    }

    private fun initWidget() {

        bannerData = intent.getParcelableExtra("bannerData") as BannerData
        binding.imgBack.setOnClickListener { finish() }
        binding.shareDealOfDay.setOnClickListener { shareDealOfBanner(bannerData) }
        binding.toolbarTxtMore.setOnClickListener {
            startActivity(Intent(this, DealBannerListActivity::class.java))
            finish()
        }

        val description_en = Html.fromHtml(bannerData.description_en)
        val description_gu = Html.fromHtml(bannerData.description_gu)

        if (LocaleManager.getLanguagePref(this) == LocaleManager.ENGLISH) {
            binding.txtBannerTitle.text = bannerData.banner_title_en
            binding.tvBannerDesc.text = description_en
        } else {
            binding.txtBannerTitle.text = bannerData.banner_title_gu
            binding.tvBannerDesc.text = description_gu
        }

        binding.txtBannerDate.text = bannerData.date + " " + getString(R.string.txt_by_agrodrip)

        Glide.with(this)
            .load(bannerData.banner_image)
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .into(binding.bannerImg)
    }


    private fun shareDealOfBanner(bannerData: BannerData) {
        Picasso.get().load(bannerData.banner_image).into(object: com.squareup.picasso.Target {
            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {

            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap,this@DealOfBannerActivity))

                val description_en = Html.fromHtml(bannerData.description_en)
                val description_gu = Html.fromHtml(bannerData.description_gu)

                if (LocaleManager.getLanguagePref(this@DealOfBannerActivity) == LocaleManager.ENGLISH){
                    intent.putExtra(Intent.EXTRA_TEXT,bannerData.banner_title_en + " \n\n "+ description_en
                            +" \n\n "+ "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                }else{
                    intent.putExtra(Intent.EXTRA_TEXT,bannerData.banner_title_gu + " \n\n "+ description_gu
                            +" \n\n "+ "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                }

                startActivity(Intent.createChooser(intent, getString(R.string.share_banner_by_txt)))
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}