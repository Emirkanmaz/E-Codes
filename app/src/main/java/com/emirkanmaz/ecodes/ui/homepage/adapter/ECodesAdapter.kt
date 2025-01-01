package com.emirkanmaz.ecodes.ui.homepage.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emirkanmaz.ecodes.R
import com.emirkanmaz.ecodes.databinding.ItemEcodeBinding
import com.emirkanmaz.ecodes.domain.models.ecode.ECode
import java.util.Locale

class ECodesAdapter(
    private val onECodeClick: () -> Unit
) : RecyclerView.Adapter<ECodesAdapter.ECodesViewHolder>() {

    private var originalList: List<ECode> = emptyList()
    private var filteredList: List<ECode> = emptyList()

    inner class ECodesViewHolder(
        private val binding: ItemEcodeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(eCode: ECode) {
            val language = Locale.getDefault().language
            val name = if (language == "tr") eCode.names.tr else eCode.names.en
            binding.apply {
                eCodeTextView.text = eCode.ecode.toString()
                eCodeNameTextView.text = name
                circleShape.background = getCircleBackground(binding.root.context, eCode.risk)
                root.setOnClickListener {
                    onECodeClick()
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

    fun setData(newList: List<ECode>) {
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
                        it.ecode.contains(query, ignoreCase = true)
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
