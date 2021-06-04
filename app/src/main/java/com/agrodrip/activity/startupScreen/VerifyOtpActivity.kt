package com.agrodrip.activity.startupScreen

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.activity.MainActivity
import com.agrodrip.activity.settings.UpdateProfileActivity
import com.agrodrip.databinding.ActivityVerifyOtpBinding
import com.agrodrip.model.OtpResponse
import com.agrodrip.model.VerifyOtpResponse
import com.agrodrip.utils.Constants
import com.agrodrip.utils.Function
import com.agrodrip.utils.Pref
import com.agrodrip.utils.Pref.getTOKENValue
import com.agrodrip.utils.Pref.getValue
import com.agrodrip.utils.Utils
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import com.agrodrip.webservices.toast
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*


class VerifyOtpActivity : BaseActivity() {

    lateinit var binding: ActivityVerifyOtpBinding
    var mobileNo: String? = null
    var otp: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify_otp)

        initView()
    }

    private fun initView() {

        binding.btnVerifyOtp.setOnClickListener {
            verifyOtp()
        }
        binding.tvResend.setOnClickListener {
            sendOtp()
        }
        mobileNo = intent.getStringExtra("mobile")
        otp = intent.getStringExtra("otp")
    }

    private fun verifyOtp() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<VerifyOtpResponse>? = ApiHandler.getApiService(this)?.verifyOtp(getParams())
        call?.enqueue(object : Callback<VerifyOtpResponse> {
            override fun onResponse(call: Call<VerifyOtpResponse>, response: Response<VerifyOtpResponse>) {
                mDialog.dismiss()
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    Log.d("res", "res$resData")
                    if (resData.code == 200) {
                        Pref.saveData(this@VerifyOtpActivity, resData.rows)
                        if (TextUtils.isEmpty(resData.rows.name)) {
                            startActivity(Intent(this@VerifyOtpActivity, UpdateProfileActivity::class.java))
                            finish()
                        } else {
                            startActivity(Intent(this@VerifyOtpActivity, MainActivity::class.java))
                            finish()
                        }

                        binding.root.snackBar(resData.message)
                    } else if (resData.code == 400) {
                        binding.root.snackBar(resData.message)
                    }

                } else {
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
                mDialog.dismiss()
                toast(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    private fun getParams(): HashMap<String, String> {
        val param = HashMap<String, String>()
        param["mobile"] = mobileNo.toString()
        param["otp"] = otp.toString()
        param["device_token"] = getTOKENValue(this, Constants.PREF_FIREBASE_TOKEN, "").toString()
        param["device"] = "android"
        param["language"] = getValue(this, Constants.PREF_LANGUAGE_TYPE, "").toString()
        return param
    }


    private fun sendOtp() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<OtpResponse>? = ApiHandler.getApiService(this)?.sendOtp(mobileNo.toString())
        call?.enqueue(object : Callback<OtpResponse> {
            override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
                mDialog.dismiss()
                val resData: OtpResponse? = response.body()
                if (resData != null) {
                    Log.d("res", "res$resData")
                    toast(resData.rows.otp)
                    otp = resData.rows.otp
                } else {
                    try {
                        val mError: OtpResponse = GsonBuilder().create().fromJson(response.errorBody()!!.string(), OtpResponse::class.java)
                        toast(mError.message)
                    } catch (e: IOException) {
                        // handle failure to read error
                    }
                }
            }

            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                mDialog.dismiss()
                toast(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

}
