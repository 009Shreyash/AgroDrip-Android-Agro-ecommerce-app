package com.agrodrip.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.databinding.ActivityAddYourProblemsBinding
import com.agrodrip.model.CommonResponse
import com.agrodrip.model.UserData
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

class AddYourProblemsActivity : BaseActivity() {

    private lateinit var data: UserData
    lateinit var imgback: ImageView
    lateinit var binding: ActivityAddYourProblemsBinding

    private var profileFile: File? = null
    private var bitmap: Bitmap? = null
    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_your_problems)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.add_your_problems)
        imgback = toolbar.findViewById(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        setSupportActionBar(toolbar)

        initWidget()
    }

    private fun initWidget() {
        data = Pref.getUserData(this)!!

        binding.btnSave.setOnClickListener {
            if (binding.etProblems.text.toString().isEmpty()) {
                binding.root.snackBar(getString(R.string.pls_enter_problems))
            } else {
                callAddProblemAPI()
            }
        }
        imgback.setOnClickListener {
            finish()
        }
        binding.ivProfile.setOnClickListener {
            CropImage.activity(null).start(this)
        }
    }

    private fun callAddProblemAPI() {
        val mDialog = Utils.fcreateDialog(this@AddYourProblemsActivity)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("description", binding.etProblems.text.toString())
        if (profileFile != null) {
            builder.addFormDataPart("cust_problem_image", profileFile!!.name, profileFile!!.asRequestBody("image/jpeg".toMediaTypeOrNull()))
        }
        val requestBody = builder.build()
        val call = ApiHandler.getApiService(this)?.addProblemApi(requestBody, "Bearer " + data.app_key)
        call?.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    if (resData.code == 200) {
                        binding.root.snackBar(getString(R.string.problem_added_successfully))
                        Handler().postDelayed({ finish() }, 1500)
                    } else if (resData.code == 400) {
                        binding.root.snackBar(resData.message)
                    }

                } else {
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                mDialog.dismiss()
                t.printStackTrace()
                t.cause
                toast(R.string.msg_internet)
            }
        })
    }

    override fun onBackPressed() {
        startActivity(Intent(this@AddYourProblemsActivity, MainActivity::class.java))
        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                profileFile = Compressor(this).compressToFile(File(result.uri.path!!))
                Glide.with(this@AddYourProblemsActivity)
                    .load(profileFile)
                    .fitCenter()
                    .into(binding.ivProfile)
                Log.d("getPath", "path: $profileFile")

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.d("Cropping failed", result.error.toString())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (imagePath != null) {
            binding.ivProfile.setImageBitmap(bitmap)
        }
    }
}