package com.emirkanmaz.ecodes.utils.singleclicklistener

import android.os.SystemClock
import android.view.View

class SingleClickListener(
    private var defaultInterval: Long = 1000,
    private val onSingleCLick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSingleCLick(v)
    }
}

fun View.setOnSingleClickListener(onSingleClick: (View) -> Unit) {
    val singleClickListener = SingleClickListener {
        onSingleClick(it)
    }
    setOnClickListener(singleClickListener)
}