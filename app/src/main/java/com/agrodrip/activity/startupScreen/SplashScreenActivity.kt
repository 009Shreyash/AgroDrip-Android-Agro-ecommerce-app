package com.agrodrip.activity.startupScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.activity.MainActivity
import com.agrodrip.utils.Constants
import com.agrodrip.utils.Function
import com.agrodrip.utils.Pref
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class SplashScreenActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = resources.getColor(R.color.colorPrimary, this.theme)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed({ setIntent() }, 2000)
    }

    private fun setIntent() {
        getHasKey()
        val data = Pref.getUserData(this)

        if (TextUtils.isEmpty(data?.app_key)) {
            startActivity(Intent(this@SplashScreenActivity, SelectLanguageActivity::class.java))
            finish()
        } else {
            if (Pref.getValue(this, Constants.PREF_LANGUAGE_TYPE, "").toString().isNotEmpty()) {
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@SplashScreenActivity, SelectLanguageActivity::class.java))
                finish()
            }
        }
    }
    private fun getHasKey() {
        val info: PackageInfo
        try {
            if (Build.VERSION.SDK_INT >= 28) {
                @SuppressLint("WrongConstant") val packageInfo =
                    packageManager.getPackageInfo(
                        packageName,
                        PackageManager.GET_SIGNING_CERTIFICATES
                    )
                val signatures: Array<Signature> =
                    packageInfo.signingInfo.apkContentsSigners
                val md = MessageDigest.getInstance("SHA")
                for (signature in signatures) {
                    md.update(signature.toByteArray())
                    val signatureBase64 =
                        String(Base64.encode(md.digest(), Base64.DEFAULT))
                    Log.d("TAG", "Signature Base64 HasKey : $signatureBase64")
                }
            } else {
                info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                for (signature in info.signatures) {
                    val md: MessageDigest = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    val something = String(Base64.encode(md.digest(), Base64.DEFAULT))
                    //String something = new String(Base64.encodeBytes(md.digest()));
                    Log.d("TAG", "Signature Base64 HasKey : $something")
                }
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("no such an algorithm", e.toString())
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }
    }

}
