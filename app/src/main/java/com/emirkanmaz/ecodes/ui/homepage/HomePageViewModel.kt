package com.emirkanmaz.ecodes.ui.homepage

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.emirkanmaz.ecodes.base.BaseViewModel
import com.emirkanmaz.ecodes.ui.homepage.navigationevent.HomePageNavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(

) : BaseViewModel<HomePageNavigationEvent>() {

    fun navigateToCrop(photoUri: Uri) {
        viewModelScope.launch {
            navigateTo(HomePageNavigationEvent.NavigateToCrop(photoUri))
        }
    }

    fun navigateToCamera() {
        viewModelScope.launch {
            navigateTo(HomePageNavigationEvent.NavigateToCamera)
        }
    }

}
