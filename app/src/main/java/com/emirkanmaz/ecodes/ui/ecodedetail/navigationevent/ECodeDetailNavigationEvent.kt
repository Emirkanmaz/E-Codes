package com.emirkanmaz.ecodes.ui.ecodedetail.navigationevent

import com.emirkanmaz.ecodes.base.BaseNavigationEvent

sealed class ECodeDetailNavigationEvent : BaseNavigationEvent() {
    object NavigateToHome : ECodeDetailNavigationEvent()
}