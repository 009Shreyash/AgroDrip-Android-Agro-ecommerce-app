package com.agrodrip.utils

import android.content.Context
import android.content.SharedPreferences
import com.agrodrip.model.UserData
import com.google.gson.Gson


object Pref {
    lateinit var sharedpreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    /**
     * set value in preference
     */
    fun setValue(context: Context, key: String?, value: String?) {
        sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE)
        editor = sharedpreferences.edit()
        editor.putString(key, value)
        editor.apply()
        //   return key;
    }
    fun setTokenValue(context: Context, key: String?, value: String?) {
        sharedpreferences = context.getSharedPreferences("TOKEN", Context.MODE_PRIVATE)
        editor = sharedpreferences.edit()
        editor.putString(key, value)
        editor.apply()
        //   return key;
    }


    fun setValue(context: Context, key: String?, value: Int) {
        sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE)
        editor = sharedpreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun setValue(context: Context, key: String?, value: Long) {
        sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE)
        editor = sharedpreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun setValue(context: Context, key: String?, value: Boolean) {
        sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE)
        editor = sharedpreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun removeValue(context: Context, key: String?) {
        sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE)
        editor = sharedpreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    /**
     * get value in preference
     */
    fun getValue(context: Context, key: String?, defaultValue: String?): String? {
        sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE)
        return sharedpreferences.getString(key, defaultValue)
    }
    fun getTOKENValue(context: Context, key: String?, defaultValue: String?): String? {
        sharedpreferences = context.getSharedPreferences("TOKEN", Context.MODE_PRIVATE)
        return sharedpreferences.getString(key, defaultValue)
    }

    fun getValue(context: Context, key: String?, defaultValue: Int): Int {
        sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE)
        return sharedpreferences.getInt(key, defaultValue)
    }

    fun getValue(context: Context, key: String?, defaultValue: Long): Long {
        sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE)
        return sharedpreferences.getLong(key, defaultValue)
    }

    fun getValue(context: Context, key: String?, defaultValue: Boolean): Boolean {
        sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE)
        return sharedpreferences.getBoolean(key, defaultValue)
    }


    fun clearAll(context: Context) {
        sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE)
        editor = sharedpreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun saveData(context: Context, userData: UserData?) {
        sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE)
        editor = sharedpreferences.edit()
        val json = Gson().toJson(userData)
        editor.putString(Constants.PREF_USER_DATA, json)
        editor.apply()
    }

    fun getUserData(context: Context): UserData? {
        sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE)
        val json: String = sharedpreferences.getString(Constants.PREF_USER_DATA, "{}")!!
        val obj: UserData = Gson().fromJson(json, UserData::class.java)
        return obj
    }
}