package com.agrodrip.activity.farm

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.BuildConfig
import com.agrodrip.R
import com.agrodrip.databinding.ActivityTipsViewBinding
import com.agrodrip.model.TipsListData
import com.agrodrip.utils.Constants
import com.agrodrip.utils.Function
import com.agrodrip.utils.LocaleManager
import com.agrodrip.utils.Pref
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso

class TipsViewActivity : BaseActivity() {

    private lateinit var imgback: ImageView
    private lateinit var binding: ActivityTipsViewBinding
    private lateinit var tipsListData: TipsListData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tips_view)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.txt_tips)
        imgback = toolbar.findViewById<ImageView>(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener { onBackPressed() }
        setSupportActionBar(toolbar)

        initWidget()
    }

    private fun initWidget() {
        tipsListData = intent.getParcelableExtra("tipsListData") as TipsListData

        val description_en = Html.fromHtml(tipsListData.pro_description_en)
        val description_gu = Html.fromHtml(tipsListData.pro_description_gu)

        if (LocaleManager.getLanguagePref(this) == LocaleManager.ENGLISH) {
            binding.txtTipsTitle.text = tipsListData.pro_name_en
            binding.txtTipsDesc.text = description_en
        } else {
            binding.txtTipsTitle.text = tipsListData.pro_name_gu
            binding.txtTipsDesc.text = description_gu
        }

        Glide.with(this)
            .load(tipsListData.pro_image)
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .error(R.drawable.places_autocomplete_toolbar_shadow)
            .into(binding.imgTips)

        binding.shareTips.setOnClickListener {
            shareProblemsData(tipsListData)
        }
    }


    private fun shareProblemsData(tipsListData: TipsListData) {

        Picasso.get().load(tipsListData.pro_image[0]).into(object : com.squareup.picasso.Target {
            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {

            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_STREAM, Function.getLocalBitmapUri(bitmap, this@TipsViewActivity))

                val description_en = Html.fromHtml(tipsListData.pro_description_en)
                val description_gu = Html.fromHtml(tipsListData.pro_description_gu)

                if (LocaleManager.getLanguagePref(this@TipsViewActivity) == LocaleManager.ENGLISH) {
                    intent.putExtra(Intent.EXTRA_TEXT, tipsListData.pro_name_en + " \n\n " + description_en
                            + " \n\n " + "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                } else {
                    intent.putExtra(Intent.EXTRA_TEXT, tipsListData.pro_name_gu + " \n\n " + description_gu
                            + " \n\n " + "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                }

                startActivity(Intent.createChooser(intent, getString(R.string.share_tips_by_txt)))
            }
        })
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}