package com.example.kartsho

import android.content.Context
import android.content.SharedPreferences
import com.example.kartsho.data.repository.KmpSettings

class AndroidSettings(context: Context) : KmpSettings {
    private val prefs: SharedPreferences = context.getSharedPreferences("kartsho_prefs", Context.MODE_PRIVATE)

    override fun getString(key: String, defaultValue: String?): String? = prefs.getString(key, defaultValue)

    override fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }
}
