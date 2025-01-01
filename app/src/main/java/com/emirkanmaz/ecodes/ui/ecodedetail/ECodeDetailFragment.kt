package com.emirkanmaz.ecodes.ui.ecodedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import com.emirkanmaz.ecodes.base.BaseFragment
import com.emirkanmaz.ecodes.base.BaseNavigationEvent
import com.emirkanmaz.ecodes.databinding.FragmentEcodeDetailBinding


class ECodeDetailFragment: BaseFragment<FragmentEcodeDetailBinding, ECodeDetailViewModel, BaseNavigationEvent>(
    ECodeDetailViewModel::class.java
) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentEcodeDetailBinding =
        FragmentEcodeDetailBinding::inflate






}