package com.agrodrip.activity.settings

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil.setContentView
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.activity.MainActivity
import com.agrodrip.databinding.ActivityUpdateProfileBinding
import com.agrodrip.model.OtpResponse
import com.agrodrip.model.UserData
import com.agrodrip.model.UserResponse
import com.agrodrip.utils.Pref
import com.agrodrip.utils.Utils
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import com.agrodrip.webservices.toast
import com.bumptech.glide.Glide
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UpdateProfileActivity : BaseActivity() {

    private lateinit var data: UserData
    lateinit var imgback: ImageView
    lateinit var binding: ActivityUpdateProfileBinding

    private var profileFile: File? = null
    private var bitmap: Bitmap? = null
    private var imagePath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentView(this, R.layout.activity_update_profile)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.update_profile)
        imgback = toolbar.findViewById<ImageView>(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        setSupportActionBar(toolbar)

        initWidget()
    }


    private fun initWidget() {
        data = Pref.getUserData(this)!!
        callProfile()

        binding.btnSave.setOnClickListener {
            if (binding.etFirstName.text.toString().isEmpty()) {
                binding.root.snackBar(getString(R.string.pls_enter_name))
            } else {
                callUpdateProfileAPI()
            }
        }
        imgback.setOnClickListener {
            finish()
        }

        binding.ivEdit.setOnClickListener {
            CropImage.activity(null).start(this)
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@UpdateProfileActivity, MainActivity::class.java))
        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                profileFile = Compressor(this).compressToFile(File(result.uri.path!!))
                Glide.with(this@UpdateProfileActivity)
                    .load(profileFile)
                    .fitCenter()
                    .into(binding.ivProfile)
                Log.d("getPath", "path: $profileFile")

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.d("Cropping failed" , result.error.toString())
            }
        }
    }

    private fun callUpdateProfileAPI() {
        val mDialog = Utils.fcreateDialog(this@UpdateProfileActivity)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("name", binding.etFirstName.text.toString())
        if (profileFile != null) {
            builder.addFormDataPart("profile", profileFile!!.name, profileFile!!.asRequestBody("image/jpeg".toMediaTypeOrNull()))
        }
        val requestBody = builder.build()
        val call = ApiHandler.getApiService(this)?.updateProfileApi(requestBody, "Bearer " + data.app_key)
        call?.enqueue(object : Callback<OtpResponse> {
            override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    if (resData.code == 200) {
                        callProfile()
                        binding.root.snackBar(getString(R.string.profile_update_txt))
                        Handler().postDelayed({ finish() }, 1000)
                    } else if (resData.code == 400) {
                        binding.root.snackBar(resData.message)
                    }

                } else {
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                mDialog.dismiss()
                t.printStackTrace()
                t.cause
                toast(R.string.msg_internet)
            }
        })
    }


    override fun onResume() {
        super.onResume()
        if (imagePath != null) {
            binding.ivProfile.setImageBitmap(bitmap)
        }
    }

    private fun callProfile() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<UserResponse>? = ApiHandler.getApiService(this)?.getUser("Bearer " + data.app_key)
        call?.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    if (resData.code == 200) {
                        Pref.saveData(this@UpdateProfileActivity, resData.rows)
                        val userData = resData.rows

                        if (!TextUtils.isEmpty(userData.name)) {
                            binding.etFirstName.setText(userData.name)
                            binding.etFirstName.setSelection(userData.name.length)
                        }

                        if (!userData.profile.isBlank() || !TextUtils.isEmpty(userData.profile)) {
                            Glide.with(this@UpdateProfileActivity)
                                .load(userData.profile)
                                .fitCenter()
                                .into(binding.ivProfile)
                        }
                    } else if (resData.code == 400) {
                        binding.root.snackBar(resData.message)
                    }
                } else {
                    mDialog.dismiss()
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                mDialog.dismiss()
                toast(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }
}