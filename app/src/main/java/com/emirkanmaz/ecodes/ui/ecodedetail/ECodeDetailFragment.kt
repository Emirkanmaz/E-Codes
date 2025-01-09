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
        val name = if (language == "tr") eCodeDetail.names.tr else eCodeDetail.names.en
        val halal = if (language == "tr") eCodeDetail.halal!!.desc.tr else eCodeDetail.halal!!.desc.en
        val source = if (language == "tr") eCodeDetail.halal!!.type.tr else eCodeDetail.halal!!.type.en
        val risk = if (language == "tr") eCodeDetail.risk!!.desc.tr else eCodeDetail.risk!!.desc.en
        val halalCertf = eCodeDetail.halal.halal == 1 || eCodeDetail.halal.halal == 2 || eCodeDetail.halal.halal == 4
        val veganCertf = eCodeDetail.halal.halal == 1 || eCodeDetail.halal.halal == 2 || eCodeDetail.halal.halal == 6

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
            warningImageView.visibility = if(eCodeDetail.risk.risk == 1){
                android.view.View.GONE
            }else{
                android.view.View.VISIBLE
            }

            eCodeTextView.text = eCodeDetail.eCode.ecode
            eCodeTextView.backgroundTintList = ContextCompat.getColorStateList(requireContext(), getRiskBackground(eCodeDetail.risk.risk))
            nameTextView.text = name
            halalTextView.text = halal
            halalLinearLayout.backgroundTintList = ContextCompat.getColorStateList(requireContext(), getHalalBackground(eCodeDetail.halal.halal))
            sourceTextView.text = source
            riskTextView.text = risk
            riskLinearLayout.backgroundTintList = ContextCompat.getColorStateList(requireContext(), getRiskBackground(eCodeDetail.risk.risk))
            if (eCodeDetail.warningList[0].warning == 0){
                warningLinearLayout.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary_green)
            }

            eCodeDetail.warningList.forEach { warning ->
                val warningText = if (language == "tr") warning.desc.tr else warning.desc.en

                val textView = TextView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.START
                    }
                    text = "- $warningText"
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral_black))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                    typeface = ResourcesCompat.getFont(requireContext(), R.font.inter)
                }

                warningLinearLayout.addView(textView)
            }
//            warningTextView.text = warning


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


    private fun getHalalBackground(halal: Int): Int {
        return when (halal) {
            1,2,4 -> R.color.primary_green
//            3,5,6 -> R.color.primary_orange
            else -> R.color.primary_orange
        }
    }

    private fun getRiskBackground(risk: Int): Int {
        return when (risk) {
            1 -> R.color.primary_green
            2 -> R.color.primary_orange
            else -> R.color.primary_red
        }
    }

}