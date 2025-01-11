package com.emirkanmaz.ecodes.data

import android.content.SharedPreferences
import com.emirkanmaz.ecodes.security.KeystoreManager
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val keystoreManager: KeystoreManager
) {
    companion object{
        private const val FIRST_TIME = "first_time"
    }

    fun isFirstTime(): Boolean {
        val encryptedFirstTime = sharedPreferences.getString(FIRST_TIME, null)?: return true
        val decryptedFirstTime = keystoreManager.decrypt(encryptedFirstTime)
        return decryptedFirstTime.toBoolean()
    }

    fun setFirstTime(value: Boolean) {
        val encryptedValue = keystoreManager.encrypt(value.toString())
        sharedPreferences.edit().putString(FIRST_TIME, encryptedValue).apply()
    }




}