package com.emirkanmaz.ecodes.ui.homepage.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emirkanmaz.ecodes.R
import com.emirkanmaz.ecodes.databinding.ItemEcodeBinding
import com.emirkanmaz.ecodes.domain.models.ecode.ECodeItemUI
import java.util.Locale

class ECodesAdapter(
    private val onECodeClick: (eCode: ECodeItemUI) -> Unit
) : RecyclerView.Adapter<ECodesAdapter.ECodesViewHolder>() {

    private var originalList: List<ECodeItemUI> = emptyList()
    private var filteredList: List<ECodeItemUI> = emptyList()
    val language = Locale.getDefault().language

    inner class ECodesViewHolder(
        private val binding: ItemEcodeBinding
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
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }
                halalImageView.visibility = if (halalCertf) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }
                circleShape.background = getCircleBackground(binding.root.context, eCode.risk)
                root.setOnClickListener {
                    onECodeClick(eCode)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ECodesViewHolder {
        val binding = ItemEcodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ECodesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ECodesViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int = filteredList.size

    fun submitList(newList: List<ECodeItemUI>) {
        originalList = newList
        filteredList = newList
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter {
                it.names.tr.contains(query, ignoreCase = true) ||
                        it.names.en.contains(query, ignoreCase = true) ||
                        it.eCode.contains(query, ignoreCase = true)
            }
        }
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
