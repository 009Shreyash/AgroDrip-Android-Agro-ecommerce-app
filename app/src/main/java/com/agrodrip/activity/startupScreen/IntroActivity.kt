package com.agrodrip.activity.startupScreen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.adapter.IntroViewPagerAdapter
import com.agrodrip.databinding.ActivityIntroBinding
import com.agrodrip.model.OtpResponse
import com.agrodrip.utils.IntroExpandingViewPagerTransformer
import com.agrodrip.utils.Utils
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import com.agrodrip.webservices.toast
import kotlinx.android.synthetic.main.activity_intro.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IntroActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = resources.getColor(R.color.colorPrimary, this.theme)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)

        initView()
    }

    private fun initView() {
        binding.btnLogin.setOnClickListener(this)

        val img = intArrayOf(R.drawable.img2, R.drawable.imgslide3, R.drawable.imgslide1)
        val introTitle = arrayOf(getString(R.string.stay_connected), getString(R.string.delivery_door_step), getString(R.string.letest_update_advice))
        val introDesc = arrayOf(
            getString(R.string.intro_desc_1),
            getString(R.string.intro_desc_2),
            getString(R.string.intro_desc_3)
        )
        val adapter: PagerAdapter = IntroViewPagerAdapter(this@IntroActivity, img, introTitle, introDesc)
        binding.viewPager.setPadding(80, 0, 80, 0)
        binding.viewPager.clipToPadding = false
        binding.viewPager.setPageTransformer(true, IntroExpandingViewPagerTransformer())
        binding.viewPager.adapter = adapter
        dotsIndicator.setViewPager(binding.viewPager)

    }

    override fun onClick(view: View) {
        if (view.id == R.id.btn_login) {
            if (binding.edtMobile.text.toString().isEmpty()) {
                binding.root.snackBar(getString(R.string.pls_enter_mobile_txt))
            } else if (binding.edtMobile.text.toString().length < 10) {
                binding.root.snackBar(getString(R.string.pls_enter_valid_number_txt))
            } else {
                sendOtp()
            }
        }
    }

    private fun sendOtp() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<OtpResponse>? = ApiHandler.getApiService(this)?.sendOtp(binding.edtMobile.text.toString())
        call?.enqueue(object : Callback<OtpResponse> {
            override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
                mDialog.dismiss()
                val resData: OtpResponse? = response.body()
                if (resData != null) {
                    Log.d("res", "res$resData")
                    toast(resData.rows.otp)
                    val intent = Intent(this@IntroActivity, VerifyOtpActivity::class.java)
                    intent.putExtra("mobile", binding.edtMobile.text.toString())
                    intent.putExtra("otp", resData.rows.otp)
                    startActivity(intent)
                } else {
                    Log.d("TAG", "onResponse: "+response.message())
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