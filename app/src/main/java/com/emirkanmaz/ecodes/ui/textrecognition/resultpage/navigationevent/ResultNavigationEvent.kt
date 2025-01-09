package com.emirkanmaz.ecodes.ui.textrecognition.resultpage.navigationevent

import com.emirkanmaz.ecodes.base.BaseNavigationEvent

sealed class ResultNavigationEvent : BaseNavigationEvent(){
    object NavigateToCropPage : ResultNavigationEvent()
    data class NavigateToDetail(val eCode: String) : ResultNavigationEvent()
}