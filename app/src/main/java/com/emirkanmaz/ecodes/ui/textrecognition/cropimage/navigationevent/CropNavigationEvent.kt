package com.emirkanmaz.ecodes.ui.textrecognition.cropimage.navigationevent

import com.emirkanmaz.ecodes.base.BaseNavigationEvent

sealed class CropNavigationEvent : BaseNavigationEvent(){
    object NavigateToHomePage : CropNavigationEvent()
    object NavigateToResultFragment : CropNavigationEvent()
}