package com.emirkanmaz.ecodes.domain.helpers

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsHelper @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {

    fun logEvent(eventName: String, params: Map<String, String>? = null) {
        val bundle = Bundle()

        params?.forEach { (key, value) ->
            bundle.putString(key, value)
        }

        firebaseAnalytics.logEvent(eventName, bundle)
    }

    companion object{
        const val EVENT_NAME_DETAIL_CLICK = "ecode_detail_click"
        const val EVENT_NAME_CAMERA_OPEN = "camera_open"
    }
}