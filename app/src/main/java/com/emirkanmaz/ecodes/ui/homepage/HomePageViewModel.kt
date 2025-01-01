package com.emirkanmaz.ecodes.ui.homepage

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emirkanmaz.ecodes.base.BaseViewModel
import com.emirkanmaz.ecodes.data.ECodeRepository
import com.emirkanmaz.ecodes.domain.models.ecode.ECode
import com.emirkanmaz.ecodes.ui.homepage.navigationevent.HomePageNavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
    val eCodeRepository: ECodeRepository
) : BaseViewModel<HomePageNavigationEvent>() {

    private val _eCodes = MutableLiveData<List<ECode>>()
    val eCodes: MutableLiveData<List<ECode>> = _eCodes

    init {
        getECodesList()
    }

    fun getECodesList() {
        viewModelScope.launch {
            setLoading(true)
            _eCodes.value = eCodeRepository.getECodeList()
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
