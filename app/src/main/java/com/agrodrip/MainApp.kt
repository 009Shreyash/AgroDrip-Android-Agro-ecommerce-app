package com.agrodrip

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.agrodrip.utils.LocaleManager

class MainApp : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleManager.setLocale(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig!!)
        LocaleManager.setLocale(this)
    }

}