package com.agrodrip.activity.startupScreen

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.databinding.ActivitySelectLanguageBinding
import com.agrodrip.utils.Constants
import com.agrodrip.utils.Constants.MyPREFERENCES
import com.agrodrip.utils.LocaleManager
import com.agrodrip.utils.Pref


open class SelectLanguageActivity : BaseActivity(), View.OnClickListener {

    private val REQUEST_PERMISSIONS: Int = 1010
    private lateinit var binding: ActivitySelectLanguageBinding
    private var type: String = "English"
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_language)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions()
        }


        if (LocaleManager.getLanguagePref(this) == LocaleManager.ENGLISH) {
            binding.ivEnglish.visibility = View.VISIBLE
            binding.ivGujrati.visibility = View.INVISIBLE
            binding.txtwelcome.text = "Welcome"
            binding.txtTitle.text = "Select Language"
            binding.btnNext.text = "Next"
        } else {
            binding.ivEnglish.visibility = View.INVISIBLE
            binding.ivGujrati.visibility = View.VISIBLE
            binding.txtwelcome.text = "સ્વાગત છે"
            binding.txtTitle.text = "ભાષા પસંદ કરો"
            binding.btnNext.text = "આગળ"
        }

        sharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE)
        initView()
    }


    private fun initView() {
        binding.llEnglish.setOnClickListener {
            binding.ivEnglish.visibility = View.VISIBLE
            binding.ivGujrati.visibility = View.INVISIBLE
            type = "English"
            setNewLocale(this, LocaleManager.ENGLISH)
            binding.txtwelcome.text = "Welcome"
            binding.txtTitle.text = "Select Language"
            binding.btnNext.text = "Next"
        }
        binding.llGujrati.setOnClickListener {
            binding.ivEnglish.visibility = View.INVISIBLE
            binding.ivGujrati.visibility = View.VISIBLE
            type = "Gujrati"
            binding.txtwelcome.text = "સ્વાગત છે"
            binding.txtTitle.text = "ભાષા પસંદ કરો"
            binding.btnNext.text = "આગળ"
            setNewLocale(this, LocaleManager.GUJARATI)
        }

        binding.btnNext.setOnClickListener(this)
    }

    private fun setNewLocale(
        mContext: AppCompatActivity,
        @LocaleManager.LocaleDef language: String
    ) {
        LocaleManager.setNewLocale(this, language)
        LocaleManager.setLanguagePref(mContext, language)
    }


    override fun onClick(v: View?) {
        when (v) {
            binding.btnNext -> {
                Pref.setValue(this, Constants.PREF_LANGUAGE_TYPE, type)
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            }
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val camerapermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val writepermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readpermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val location2permission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val location1permission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val listPermissionsNeeded = ArrayList<String>()
        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (readpermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (location2permission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (location1permission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_PERMISSIONS
            )
            return false
        }
        return true
    }

}