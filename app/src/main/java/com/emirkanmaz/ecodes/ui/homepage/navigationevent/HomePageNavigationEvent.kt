package com.emirkanmaz.ecodes.ui.homepage.navigationevent

import android.net.Uri
import com.emirkanmaz.ecodes.base.BaseNavigationEvent

sealed class HomePageNavigationEvent : BaseNavigationEvent() {
    object NavigateToCamera : HomePageNavigationEvent()
    data class NavigateToCrop(val photoUri: Uri) : HomePageNavigationEvent()
//    data class NavigateToDetail(val eCode: String) : HomePageNavigationEvent()
    object NavigateToDetail : HomePageNavigationEvent()
}