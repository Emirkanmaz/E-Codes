package com.emirkanmaz.ecodes.ui.homepage

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emirkanmaz.ecodes.base.BaseViewModel
import com.emirkanmaz.ecodes.data.ECodeRepository
import com.emirkanmaz.ecodes.data.SharedPreferencesManager
import com.emirkanmaz.ecodes.domain.helpers.AnalyticsHelper
import com.emirkanmaz.ecodes.domain.helpers.AnalyticsHelper.Companion.EVENT_NAME_CAMERA_OPEN
import com.emirkanmaz.ecodes.domain.helpers.AnalyticsHelper.Companion.EVENT_NAME_DETAIL_CLICK
import com.emirkanmaz.ecodes.domain.models.ecode.ECodeItemUI
import com.emirkanmaz.ecodes.ui.homepage.navigationevent.HomePageNavigationEvent
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
    val eCodeRepository: ECodeRepository,
    val sharedPreferencesManager: SharedPreferencesManager,
    val analyticsHelper: AnalyticsHelper,
    @ApplicationContext private val context: Context
) : BaseViewModel<HomePageNavigationEvent>() {

    private val _eCodeDetails = MutableLiveData<List<ECodeItemUI>>()
    val eCodeDetails: MutableLiveData<List<ECodeItemUI>> = _eCodeDetails

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: MutableLiveData<String> = _searchQuery

    private val _nativeAdList = MutableLiveData<List<NativeAd>>()
    val nativeAdList: MutableLiveData<List<NativeAd>> = _nativeAdList

    init {
        getECodeDetails()
        if (_nativeAdList.value?.isEmpty() ?: true){
            preloadAds("ca-app-pub-6542807046143275/9196715238", 5)
        }
    }

    fun onDetailClicked(eCodeName: String) {
        analyticsHelper.logEvent(
            EVENT_NAME_DETAIL_CLICK,
            mapOf(
                "e_code" to eCodeName,
            )
        )
    }

    fun onCameraClicked() {
        analyticsHelper.logEvent(EVENT_NAME_CAMERA_OPEN)
    }

    fun preloadAds(adUnitId: String, count: Int) {
        viewModelScope.launch(Dispatchers.IO){
            delay(2500)
            val adLoader = AdLoader.Builder(context, adUnitId)
                .forNativeAd { nativeAd ->
                    val currentList = _nativeAdList.value.orEmpty()
                    val updatedList = currentList + nativeAd
                    _nativeAdList.postValue(updatedList)
                }
                .build()
            repeat(count) {
                adLoader.loadAd(AdRequest.Builder().build())
            }
        }
    }

    fun getECodeDetails() {
        viewModelScope.launch{
            setLoading(true)
            _eCodeDetails.value = eCodeRepository.getECodeItemUIList()
            setLoading(false)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun isFirstTime(): Boolean {
        return sharedPreferencesManager.isFirstTime()
    }

    fun setFirstTime(){
        sharedPreferencesManager.setFirstTime(false)
    }

    fun navigateToDetail(eCode: String){
        viewModelScope.launch{
         navigateTo(HomePageNavigationEvent.NavigateToDetail(eCode))
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

    fun navigateToPrivacyPolicy() {
        viewModelScope.launch {
            navigateTo(HomePageNavigationEvent.NavigateToPrivacyPolicy)
        }
    }

}
