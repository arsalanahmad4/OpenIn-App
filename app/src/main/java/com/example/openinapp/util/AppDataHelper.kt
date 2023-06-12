package com.example.openinapp.util

import android.content.Context
import android.content.SharedPreferences

class AppDataHelper(context: Context) {
    private val PREFS_NAME = "OpenInApp"
    private var sp: SharedPreferences? =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
    private var prefEditor: SharedPreferences.Editor? = sp?.edit()

    fun save(key: String, value: String?): Boolean {
        prefEditor?.putString(key, "")
        if (value != null)
            prefEditor?.putString(key, value)
        return prefEditor?.commit() ?: false
    }

    fun save(key: String, value: Boolean): Boolean {
        prefEditor?.putBoolean(key, value)
        return prefEditor?.commit() ?: false
    }

    fun save(key: String, value: Long): Boolean {
        prefEditor?.putLong(key, value)
        return prefEditor?.commit() ?: false
    }

    fun save(key: String, value: Int): Boolean {
        prefEditor?.putInt(key, value)
        return prefEditor?.commit() ?: false
    }

    fun getBoolean(key: String): Boolean {
        return sp?.getBoolean(key, false) ?: false
    }

    fun getLong(key: String): Long {
        return sp?.getLong(key, 0) ?: 0
    }

    fun getString(key: String): String {
        return sp?.getString(key, "") ?: ""
    }

    fun getInt(key: String): Int {
        return sp?.getInt(key, 0) ?: 0
    }

    fun saveTokenToLocalStorage(token: String) :Boolean{
        prefEditor?.putString(Constants.TOKEN_KEY, "")
        if (token != null)
            prefEditor?.putString(Constants.TOKEN_KEY, token)
        return prefEditor?.commit() ?: false
    }

    fun getTokenFromLocalStorage(): String {
        return sp?.getString(Constants.TOKEN_KEY, "") ?: ""
    }

    fun clearData() {
        prefEditor?.clear()
        prefEditor?.apply()
    }
}

