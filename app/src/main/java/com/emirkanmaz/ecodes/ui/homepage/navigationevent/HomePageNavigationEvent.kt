package com.emirkanmaz.ecodes.ui.homepage.navigationevent

import com.emirkanmaz.ecodes.base.BaseNavigationEvent

sealed class HomePageNavigationEvent : BaseNavigationEvent() {
    object NavigateToCamera : HomePageNavigationEvent()
}