package com.emirkanmaz.ecodes.ui.textrecognition.resultpage.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emirkanmaz.ecodes.R
import com.emirkanmaz.ecodes.databinding.ItemResultEcodeBinding
import com.emirkanmaz.ecodes.domain.models.ecode.ECodeItemUI
import java.util.Locale

class ResultECodesAdapter(
    private val onCancelClick: (ECodeItemUI) -> Unit
) : RecyclerView.Adapter<ResultECodesAdapter.ECodesViewHolder>() {

    private var eCodeList: List<ECodeItemUI> = emptyList()
    val language = Locale.getDefault().language

    inner class ECodesViewHolder(
        private val binding: ItemResultEcodeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(eCode: ECodeItemUI) {
            val name = if (language == "tr") eCode.names.tr else eCode.names.en
            val halal = if (language == "tr") eCode.halal.desc.tr else eCode.halal.desc.en
            val halalCertf = eCode.halal.halal == 1 || eCode.halal.halal == 2 || eCode.halal.halal == 4
            val veganCertf = eCode.halal.halal == 1 || eCode.halal.halal == 2 || eCode.halal.halal == 6
            binding.apply {
                eCodeTextView.text = eCode.eCode
                eCodeNameTextView.text = name
                eCodeHalalTextView.text = halal
                veganImageView.visibility = if (veganCertf) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                halalImageView.visibility = if (halalCertf) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                circleShape.background = getCircleBackground(binding.root.context, eCode.risk)
                btnRemove.setOnClickListener {
                    onCancelClick(eCode)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ECodesViewHolder {
        val binding = ItemResultEcodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ECodesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ECodesViewHolder, position: Int) {
        holder.bind(eCodeList[position])
    }

    override fun getItemCount(): Int = eCodeList.size

    fun submitList(newList: List<ECodeItemUI>) {
        eCodeList = newList
        notifyDataSetChanged()
    }

    private fun getCircleBackground(context: Context, risk: Int): Drawable? {
        return when (risk) {
            1 -> ContextCompat.getDrawable(context, R.drawable.shape_hollow_circle_low_risk)
            2 -> ContextCompat.getDrawable(context, R.drawable.shape_hollow_circle_med_risk)
            else -> ContextCompat.getDrawable(context, R.drawable.shape_hollow_circle_high_risk)
        }
    }
}