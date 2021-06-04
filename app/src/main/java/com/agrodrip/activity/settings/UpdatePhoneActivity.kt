package com.agrodrip.activity.settings

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.databinding.ActivityPhoneUpdateBinding
import com.agrodrip.model.UpdateOtpResponse
import com.agrodrip.model.UserData
import com.agrodrip.utils.Constants
import com.agrodrip.utils.Function
import com.agrodrip.utils.Pref
import com.agrodrip.utils.Utils
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import com.agrodrip.webservices.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdatePhoneActivity : BaseActivity(), View.OnClickListener {

    lateinit var imgback: ImageView
    private lateinit var binding: ActivityPhoneUpdateBinding
    private lateinit var data: UserData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone_update)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.updatePhone)
        imgback = toolbar.findViewById<ImageView>(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener(this)
        setSupportActionBar(toolbar)

        initWidget()

    }

    private fun initWidget() {
        data = Pref.getUserData(this)!!
        binding.btnUpdateMobile.setOnClickListener(this)
        binding.btnVerifyOtp.setOnClickListener(this)
        binding.tvResend.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v) {
            imgback -> {
                onBackPressed()
            }
            binding.btnUpdateMobile -> {
                when {
                    TextUtils.isEmpty(binding.edtMobileNumber.text.toString()) -> {
                        binding.root.snackBar("Enter mobile number")
                    }
                    data.mobile == binding.edtMobileNumber.text.toString() -> {
                        binding.root.snackBar("New mobile number is same as current mobile number")
                    }
                    else -> changePhone()
                }

            }
            binding.btnVerifyOtp -> {
                if (TextUtils.isEmpty(binding.etOtp.text.toString())) {
                    binding.root.snackBar("Enter otp")
                } else
                    updatePhone()
            }
            binding.tvResend -> {
                changePhone()
            }
        }
    }

    private fun updatePhone() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<UpdateOtpResponse>? = ApiHandler.getApiService(this)?.updateMobile("Bearer " + data.app_key, binding.edtMobileNumber.text.toString(), binding.etOtp.text.toString())
        call?.enqueue(object : Callback<UpdateOtpResponse> {
            override fun onResponse(call: Call<UpdateOtpResponse>, response: Response<UpdateOtpResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    Handler().postDelayed({
                        binding.mobileRelative.visibility = View.VISIBLE
                        binding.otpRelative.visibility = View.GONE
                    }, 1500)

                    binding.root.snackBar("Mobile number updated")
                } else {
                    if (response.code() == 400) {
                        mDialog.dismiss()
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<UpdateOtpResponse>, t: Throwable) {
                mDialog.dismiss()
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    private fun changePhone() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<UpdateOtpResponse>? = ApiHandler.getApiService(this)?.changeMobile("Bearer " + data.app_key, binding.edtMobileNumber.text.toString())
        call?.enqueue(object : Callback<UpdateOtpResponse> {
            override fun onResponse(call: Call<UpdateOtpResponse>, response: Response<UpdateOtpResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    binding.mobileRelative.visibility = View.GONE
                    binding.otpRelative.visibility = View.VISIBLE
                    toast(resData.otp)
                } else {
                    if (response.code() == 400) {
                        mDialog.dismiss()
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<UpdateOtpResponse>, t: Throwable) {
                mDialog.dismiss()
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}