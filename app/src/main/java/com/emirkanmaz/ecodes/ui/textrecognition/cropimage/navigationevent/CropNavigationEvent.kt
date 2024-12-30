package com.emirkanmaz.ecodes.ui.textrecognition.cropimage.navigationevent

import android.graphics.Bitmap
import com.emirkanmaz.ecodes.base.BaseNavigationEvent

sealed class CropNavigationEvent : BaseNavigationEvent(){
    object NavigateToHomePage : CropNavigationEvent()
    data class NavigateToResultFragment(val croppedImage: Bitmap) : CropNavigationEvent()
}