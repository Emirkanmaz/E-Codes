package com.emirkanmaz.ecodes.ui.ecodedetail

import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.emirkanmaz.ecodes.R
import com.emirkanmaz.ecodes.base.BaseFragment
import com.emirkanmaz.ecodes.base.BaseNavigationEvent
import com.emirkanmaz.ecodes.databinding.FragmentEcodeDetailBinding
import com.emirkanmaz.ecodes.domain.models.ecode.ECodeDetail
import com.emirkanmaz.ecodes.ui.ecodedetail.navigationevent.ECodeDetailNavigationEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class ECodeDetailFragment: BaseFragment<FragmentEcodeDetailBinding, ECodeDetailViewModel, BaseNavigationEvent>(
    ECodeDetailViewModel::class.java
) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentEcodeDetailBinding =
        FragmentEcodeDetailBinding::inflate

    private val args: ECodeDetailFragmentArgs by navArgs()

    override fun init() {
        super.init()
        viewModel.getECodeDetail(args.eCode)
    }

    override fun observeViewModel() {
        super.observeViewModel()
        lifecycleScope.launch{
            viewModel.eCodeDetails.observe(viewLifecycleOwner){ eCodeDetail ->
                updateUI(eCodeDetail)
            }
        }
    }

    override fun handleNavigationEvent(event: BaseNavigationEvent) {
        super.handleNavigationEvent(event)
        when (event) {
            is ECodeDetailNavigationEvent.NavigateToHome -> {
                findNavController().navigateUp()
            }
        }
    }

    private fun updateUI(eCodeDetail: ECodeDetail) {
        val language = Locale.getDefault().language
        val halalCertf = eCodeDetail.halal!!.halal == 1 || eCodeDetail.halal!!.halal == 2 || eCodeDetail.halal!!.halal == 4
        val veganCertf = eCodeDetail.halal!!.halal == 1 || eCodeDetail.halal!!.halal == 2 || eCodeDetail.halal!!.halal == 6

        binding.apply {
            veganImageView.visibility = if (veganCertf) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
            halalImageView.visibility = if (halalCertf) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
            warningImageView.visibility = if(eCodeDetail.risk!!.risk == 1){
                android.view.View.GONE
            }else{
                android.view.View.VISIBLE
            }

            eCodeTextView.text = eCodeDetail.eCode.ecode
            eCodeTextView.backgroundTintList = ContextCompat.getColorStateList(requireContext(), viewModel.getRiskBackground(eCodeDetail.risk!!.risk))
            nameTextView.text = eCodeDetail.names.getLocaLizedText(language)
            halalTextView.text = eCodeDetail.halal!!.desc.getLocaLizedText(language)
            halalLinearLayout.backgroundTintList = ContextCompat.getColorStateList(requireContext(), viewModel.getHalalBackground(eCodeDetail.halal.halal))
            sourceTextView.text = eCodeDetail.halal!!.type.getLocaLizedText(language)
            riskTextView.text = eCodeDetail.risk!!.desc.getLocaLizedText(language)
            riskLinearLayout.backgroundTintList = ContextCompat.getColorStateList(requireContext(), viewModel.getRiskBackground(eCodeDetail.risk.risk))

            if (eCodeDetail.warningList[0].warning == 0){
                warningLinearLayout.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary_green)
            }

            eCodeDetail.warningList.forEach { warning ->
                val warningText = warning.desc.getLocaLizedText(language)
                addWarningTextView("- $warningText")
            }

        }
    }

    override fun setupListeners() {
        super.setupListeners()
        binding.apply {
            btnBack.setOnClickListener {
                viewModel.navigateToHome()
            }
        }
    }

    private fun addWarningTextView(text: String) {
        val textView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.START
            }
            this.text = text
            setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral_black))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            typeface = ResourcesCompat.getFont(requireContext(), R.font.inter)
        }
        binding.warningLinearLayout.addView(textView)
    }



}