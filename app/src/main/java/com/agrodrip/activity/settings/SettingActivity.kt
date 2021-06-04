package com.agrodrip.activity.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.BuildConfig
import com.agrodrip.R
import com.agrodrip.activity.MainActivity
import com.agrodrip.activity.startupScreen.SelectLanguageActivity
import com.agrodrip.databinding.ActivitySettingBinding
import com.agrodrip.utils.Function
import com.agrodrip.utils.LocaleManager
import com.agrodrip.utils.Pref
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.*


class SettingActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySettingBinding
    lateinit var imgback: ImageView
    private val PLACE_PICKER_REQUEST = 3
    private var selectedPlace: Place? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.setting)
        imgback = toolbar.findViewById(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener(this)
        setSupportActionBar(toolbar)


        initWidget()
    }

    @SuppressLint("SetTextI18n")
    private fun initWidget() {

        binding.llUpdateMobile.setOnClickListener(this)
        binding.llUpdateProfile.setOnClickListener(this)
        binding.llUpdateLocation.setOnClickListener(this)
        binding.llUpdateLanguage.setOnClickListener(this)
        binding.llRateUs.setOnClickListener(this)
        binding.llShareUs.setOnClickListener(this)
        binding.llTermsCondition.setOnClickListener(this)
        binding.llAboutUs.setOnClickListener(this)
        binding.llVersion.setOnClickListener(this)
        binding.llLogout.setOnClickListener(this)

        if (LocaleManager.getLanguagePref(this) == LocaleManager.ENGLISH) {
            binding.txtLanguage.text = resources.getText(R.string.english)
        } else {
            binding.txtLanguage.text = resources.getText(R.string.gujrati)
        }

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key), Locale.US)
        }
        binding.txtVersion.text = "Version" + " " + BuildConfig.VERSION_NAME
        if (Pref.getValue(this, com.agrodrip.utils.Constants.PREF_ADDRESS, "").toString().isNotEmpty())
            binding.txtLocation.text = Pref.getValue(this, com.agrodrip.utils.Constants.PREF_ADDRESS, "")
    }

    override fun onResume() {
        super.onResume()
        if (LocaleManager.getLanguagePref(this) == LocaleManager.ENGLISH) {
            binding.txtLanguage.text = resources.getText(R.string.english)
        } else {
            binding.txtLanguage.text = resources.getText(R.string.gujrati)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            imgback -> {
                onBackPressed()
            }
            binding.llUpdateMobile -> {
                startActivity(Intent(this, UpdatePhoneActivity::class.java))
            }
            binding.llUpdateProfile -> {
                startActivity(Intent(this, UpdateProfileActivity::class.java))
            }
            binding.llUpdateLocation -> {
                updateLocation()
            }
            binding.llUpdateLanguage -> {
                startActivity(Intent(this, UpdateLanguageActivity::class.java))
            }
            binding.llRateUs -> {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "market://details?id=" + resources.getString(
                                R.string.app_name
                            )
                        )
                    )
                )
            }
            binding.llTermsCondition -> {

            }
            binding.llAboutUs -> {

            }
            binding.llShareUs -> {
                shareApp()
            }
            binding.llLogout -> {
                logoutDialog()
            }
            binding.llVersion -> {
                Function.showMessage(
                    "AgroDrip App Version Code" + BuildConfig.VERSION_NAME,
                    binding.root
                )
            }
        }
    }

    private fun shareApp() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            resources.getString(R.string.share_app_desc) + BuildConfig.APPLICATION_ID
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    private fun updateLocation() {
        val fields = mutableListOf(
            Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
            Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.PLUS_CODE
        )
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(
            this
        )
        startActivityForResult(intent, PLACE_PICKER_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                selectedPlace = Autocomplete.getPlaceFromIntent(data!!)
                val latlong = selectedPlace?.latLng.toString()
                val str = latlong.replace("(", "").replace(")", "").replace("lat/lng:", "")
                Pref.setValue(this, com.agrodrip.utils.Constants.PREF_UPDATED_LATLONG, str)
                val listAddress: List<AddressComponent> = selectedPlace!!.addressComponents!!.asList()
                for (i in listAddress.indices) {
                    if (listAddress[i].types[0] == "locality") {
                        binding.txtLocation.text = listAddress[i].name
                        Pref.setValue(this, com.agrodrip.utils.Constants.PREF_ADDRESS, listAddress[i].name)
                    }
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status: Status = Autocomplete.getStatusFromIntent(data!!)
                Log.d("TAG", status.statusMessage!!)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("TAG", "canceled")
            }
        }
    }

    private fun logoutDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_layout)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )

        val textYes = dialog.findViewById(R.id.txtYes) as TextView
        val textNo = dialog.findViewById(R.id.txtNo) as TextView

        textYes.setOnClickListener {
            Pref.clearAll(this)
            startActivity(Intent(this, SelectLanguageActivity::class.java))
            finish()
            dialog.dismiss()
        }

        textNo.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent()
        setResult(RESULT_OK, intent)
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}