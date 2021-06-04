package com.agrodrip.activity

import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.agrodrip.BaseActivity
import com.agrodrip.R
import kotlinx.android.synthetic.main.activity_watch_demo.*


class WatchDemoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_demo)

        val videoUrl = intent.getStringExtra("videoUrl") as String
        Log.d("sdssdsdsds", videoUrl)


        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {
                return false
            }
        }
        val webSettings: WebSettings = webview.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true

        webview.loadUrl("http://www.youtube.com/embed/$videoUrl?autoplay=1&vq=small")

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}