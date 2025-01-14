package com.emirkanmaz.ecodes.ui.textrecognition.cropimage

import androidx.lifecycle.viewModelScope
import com.emirkanmaz.ecodes.base.BaseViewModel
import com.emirkanmaz.ecodes.ui.textrecognition.cropimage.navigationevent.CropNavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CropViewModel @Inject constructor(

) : BaseViewModel<CropNavigationEvent>() {

    fun navigateToHomePage(){
        viewModelScope.launch{
            navigateTo(CropNavigationEvent.NavigateToHomePage)
        }
    }

    fun navigateToResultFragment() {
        viewModelScope.launch {
            navigateTo(CropNavigationEvent.NavigateToResultFragment)
        }
    }

}