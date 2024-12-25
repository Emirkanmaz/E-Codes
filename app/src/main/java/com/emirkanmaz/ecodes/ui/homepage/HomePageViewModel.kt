package com.emirkanmaz.ecodes.ui.homepage

import androidx.lifecycle.viewModelScope
import com.emirkanmaz.ecodes.base.BaseNavigationEvent
import com.emirkanmaz.ecodes.base.BaseViewModel
import com.emirkanmaz.ecodes.ui.homepage.navigationevent.HomePageNavigationEvent
import kotlinx.coroutines.launch

class HomePageViewModel : BaseViewModel<BaseNavigationEvent>() {

    fun navigateToCamera(){
        viewModelScope.launch{
            navigateTo(HomePageNavigationEvent.NavigateToCamera)
        }
    }


}
