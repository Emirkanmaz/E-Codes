package com.emirkanmaz.ecodes.ui.ecodedetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emirkanmaz.ecodes.R
import com.emirkanmaz.ecodes.base.BaseNavigationEvent
import com.emirkanmaz.ecodes.base.BaseViewModel
import com.emirkanmaz.ecodes.data.ECodeRepository
import com.emirkanmaz.ecodes.domain.models.ecode.ECodeDetail
import com.emirkanmaz.ecodes.ui.ecodedetail.navigationevent.ECodeDetailNavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ECodeDetailViewModel @Inject constructor(
    val eCodeRepository: ECodeRepository,
) : BaseViewModel<BaseNavigationEvent>() {

    private val _eCodeDetails = MutableLiveData<ECodeDetail>()
    val eCodeDetails: MutableLiveData<ECodeDetail> = _eCodeDetails



    fun getHalalBackground(halal: Int): Int {
        return when (halal) {
            1,2,4 -> R.color.primary_green
//            3,5,6 -> R.color.primary_orange
            else -> R.color.primary_orange
        }
    }

    fun getRiskBackground(risk: Int): Int {
        return when (risk) {
            1 -> R.color.primary_green
            2 -> R.color.primary_orange
            else -> R.color.primary_red
        }
    }

    fun getECodeDetail(eCode: String) {
        viewModelScope.launch {
            _eCodeDetails.value = eCodeRepository.getECodeDetail(eCode)
        }
    }

    fun navigateToHome() {
        viewModelScope.launch {
            navigateTo(ECodeDetailNavigationEvent.NavigateToHome)
        }
    }

}