package com.emirkanmaz.ecodes.ui.homepage

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emirkanmaz.ecodes.base.BaseViewModel
import com.emirkanmaz.ecodes.data.ECodeRepository
import com.emirkanmaz.ecodes.domain.models.ecode.ECodeItemUI
import com.emirkanmaz.ecodes.ui.homepage.navigationevent.HomePageNavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
    val eCodeRepository: ECodeRepository
) : BaseViewModel<HomePageNavigationEvent>() {

    private val _eCodeDetails = MutableLiveData<List<ECodeItemUI>>()
    val eCodeDetails: MutableLiveData<List<ECodeItemUI>> = _eCodeDetails

    init {
        getECodeDetails()
    }

    fun getECodeDetails() {
        viewModelScope.launch{
            setLoading(true)
            _eCodeDetails.value = eCodeRepository.getECodeItemUIList()
            setLoading(false)
        }
    }

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
