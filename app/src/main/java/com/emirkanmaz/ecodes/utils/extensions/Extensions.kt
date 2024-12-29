package com.emirkanmaz.diyet.utils.extensions

import android.content.res.Resources
import android.net.Uri
import android.util.TypedValue

fun Int.dpToPx(): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()
}

fun Uri?.isValid(): Boolean {
    return this != null && toString().isNotEmpty()
}
