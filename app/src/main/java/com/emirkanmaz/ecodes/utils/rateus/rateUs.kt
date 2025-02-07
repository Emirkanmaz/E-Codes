package com.emirkanmaz.ecodes.utils.rateus

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.rateUs() {
    val packageName = "com.emirkanmaz.ecodes"
    val playStoreLink = "market://details?id=$packageName"
    val webLink = "https://play.google.com/store/apps/details?id=$packageName"
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(playStoreLink))
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webLink))
        startActivity(webIntent)
    }
}