package com.example.kartsho

import com.example.kartsho.data.repository.KmpSettings
import platform.Foundation.NSUserDefaults

class IosSettings : KmpSettings {
    override fun getString(key: String, defaultValue: String?): String? {
        return NSUserDefaults.standardUserDefaults.stringForKey(key) ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        NSUserDefaults.standardUserDefaults.setObject(value, forKey = key)
    }

    override fun remove(key: String) {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(key)
    }
}
