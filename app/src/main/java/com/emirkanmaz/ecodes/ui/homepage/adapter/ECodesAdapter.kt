package com.emirkanmaz.ecodes.ui.homepage.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emirkanmaz.ecodes.R
import com.emirkanmaz.ecodes.databinding.ItemEcodeBinding
import com.emirkanmaz.ecodes.databinding.ItemNativeAdBinding
import com.emirkanmaz.ecodes.domain.models.ecode.ECodeItemUI
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import java.util.Locale

class ECodesAdapter(
    private val onECodeClick: (eCode: ECodeItemUI) -> Unit,
    private val onEmptyAddClick: () -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var originalList: List<ECodeItemUI> = emptyList()
    private var filteredList: List<ECodeItemUI> = emptyList()
    private val nativeAdList = mutableListOf<NativeAd>()
    val language = Locale.getDefault().language

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_AD = 1
        private const val AD_REPEAT_COUNT = 10
    }

    inner class ECodesViewHolder(
        private val binding: ItemEcodeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(eCode: ECodeItemUI) {
            val halalCertf = eCode.halal.halal == 1 || eCode.halal.halal == 2 || eCode.halal.halal == 4
            val veganCertf = eCode.halal.halal == 1 || eCode.halal.halal == 2 || eCode.halal.halal == 6
            binding.apply {
                eCodeTextView.text = eCode.eCode
                eCodeNameTextView.text = eCode.names.getLocaLizedText(language)
                eCodeHalalTextView.text = eCode.halal.desc.getLocaLizedText(language)
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

    inner class AdViewHolder(private val binding: ItemNativeAdBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(nativeAd: NativeAd) {
            populateNativeAdView(nativeAd, binding.nativeAdView)
        }

        fun bindEmptyAd() {
            binding.nativeAdView.setOnClickListener {
                onEmptyAddClick()
            }
            binding.cta.setOnClickListener{
                onEmptyAddClick()
            }
        }

        private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
            adView.visibility= android.view.View.VISIBLE
            adView.headlineView = binding.primary
            binding.primary.text = nativeAd.headline

            nativeAd.icon?.let { icon ->
                adView.iconView = binding.icon
                binding.icon.setImageDrawable(icon.drawable)
                binding.icon.visibility = android.view.View.VISIBLE
            } ?: run {
                binding.icon.visibility = android.view.View.GONE
            }

            nativeAd.starRating?.let {
                if (it > 0){
                    adView.starRatingView = binding.ratingBar
                    binding.ratingBar.rating = nativeAd.starRating!!.toFloat()
                    binding.ratingBar.visibility = android.view.View.VISIBLE
                }
            } ?: run {
                binding.ratingBar.visibility = android.view.View.GONE
            }

            adView.bodyView = binding.secondary
            binding.secondary.text = nativeAd.body ?: ""
            binding.secondary.visibility = if (!nativeAd.body.isNullOrEmpty()) android.view.View.VISIBLE else android.view.View.GONE

            adView.callToActionView = binding.cta
            binding.cta.text = nativeAd.callToAction ?: ""
            binding.cta.visibility = if (!nativeAd.callToAction.isNullOrEmpty()) android.view.View.VISIBLE else android.view.View.GONE

            adView.setNativeAd(nativeAd)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if ((position + 1) % AD_REPEAT_COUNT == 0) VIEW_TYPE_AD else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_AD) {
            val binding = ItemNativeAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            AdViewHolder(binding)
        } else {
            val binding = ItemEcodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ECodesViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ECodesViewHolder) {
            val adjustedPosition = position - (position / AD_REPEAT_COUNT)
            if (adjustedPosition < filteredList.size) {
                holder.bind(filteredList[adjustedPosition])
            }
        } else if (holder is AdViewHolder) {
            val adIndex = position / AD_REPEAT_COUNT - 1
            if (adIndex in nativeAdList.indices) {
                holder.bind(nativeAdList[adIndex])
            } else {
                holder.bindEmptyAd()
            }
        }
    }

    override fun getItemCount(): Int {
        val itemCount = filteredList.size
        return itemCount + (itemCount / AD_REPEAT_COUNT)
    }

    fun submitList(newList: List<ECodeItemUI>) {
        originalList = newList
        filteredList = newList
        notifyDataSetChanged()
    }

    fun submitAdList(newAdList: List<NativeAd>) {
        nativeAdList.clear()
        nativeAdList.addAll(newAdList)
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter {
                it.names.tr.contains(query, ignoreCase = true) ||
                        it.names.en.contains(query, ignoreCase = true) ||
                        it.names.ru.contains(query, ignoreCase = true) ||
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
