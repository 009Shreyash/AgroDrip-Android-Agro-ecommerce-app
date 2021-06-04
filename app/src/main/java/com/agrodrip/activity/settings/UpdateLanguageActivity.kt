package com.agrodrip.activity.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.databinding.ActivityUpdateLanguageBinding
import com.agrodrip.utils.Constants
import com.agrodrip.utils.LocaleManager

class UpdateLanguageActivity : BaseActivity(), View.OnClickListener {

    lateinit var imgback: ImageView
    private lateinit var binding: ActivityUpdateLanguageBinding
    private var sharedPreferences: SharedPreferences? = null
    var toolbarText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_language)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)

        imgback = toolbar.findViewById<ImageView>(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener(this)
        setSupportActionBar(toolbar)

        sharedPreferences = getSharedPreferences(Constants.MyPREFERENCES, MODE_PRIVATE)

        initWidget()

    }

    private fun initWidget() {
        binding.llEnglish.setOnClickListener(this)
        binding.llGujarati.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)

        if (LocaleManager.getLanguagePref(this) == LocaleManager.ENGLISH) {
            binding.ivEnglish.visibility = View.VISIBLE
            binding.ivGujrati.visibility = View.INVISIBLE
            binding.txtTitle.text = "Select Language"
            binding.btnNext.text = "Save"
            toolbarText!!.text = "Language"
        } else {
            binding.ivEnglish.visibility = View.INVISIBLE
            binding.ivGujrati.visibility = View.VISIBLE
            binding.txtTitle.text = "ભાષા પસંદ કરો"
            binding.btnNext.text = "સાચવો"
            toolbarText!!.text = "ભાષા"
        }

    }

    override fun onClick(v: View?) {
        when (v) {
            imgback -> {
                onBackPressed()
            }
            binding.llEnglish -> {
                binding.ivEnglish.visibility = View.VISIBLE
                binding.ivGujrati.visibility = View.INVISIBLE
                binding.txtTitle.text = "Select Language"
                binding.btnNext.text = "Save"
                toolbarText!!.text = "Language"
                setNewLocale(this, LocaleManager.ENGLISH)

            }
            binding.llGujarati -> {
                binding.ivEnglish.visibility = View.INVISIBLE
                binding.ivGujrati.visibility = View.VISIBLE
                binding.txtTitle.text = "ભાષા પસંદ કરો"
                binding.btnNext.text = "સાચવો"
                toolbarText!!.text = "ભાષા"
                setNewLocale(this, LocaleManager.GUJARATI)
            }
            binding.btnNext -> {
                startActivity(Intent(this, SettingActivity::class.java))
                finish()
            }
        }
    }

    private fun setNewLocale(
        mContext: AppCompatActivity,
        @LocaleManager.LocaleDef language: String
    ) {
        LocaleManager.setNewLocale(this, language)
        LocaleManager.setLanguagePref(mContext, language)
//        val intent = mContext.intent
//        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, SettingActivity::class.java))
        finish()
    }

}