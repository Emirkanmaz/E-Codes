package com.emirkanmaz.diyet.utils.extensions.pressbackagaintoexit

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

// use in onCreate
fun Fragment.pressBackAgainToExit(
    duration: Long = 3000L,
    onWarningAction: () -> Unit = {},
    onExitAction: () -> Unit = {}
) {
    var backPressedTime: Long = 0

    requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressedTime < duration) {
                onExitAction()
            } else {
                onWarningAction()
            }
            backPressedTime = currentTime
        }
    })
}
