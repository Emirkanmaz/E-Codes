package com.emirkanmaz.ecodes.data

import android.content.SharedPreferences
import com.emirkanmaz.ecodes.security.KeystoreManager
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val keystoreManager: KeystoreManager
) {


}