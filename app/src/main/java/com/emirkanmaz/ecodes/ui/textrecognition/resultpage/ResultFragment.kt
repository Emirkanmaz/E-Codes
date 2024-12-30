package com.emirkanmaz.ecodes.ui.textrecognition.resultpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.emirkanmaz.ecodes.base.BaseFragment
import com.emirkanmaz.ecodes.base.BaseNavigationEvent
import com.emirkanmaz.ecodes.databinding.FragmentResultBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResultFragment : BaseFragment<FragmentResultBinding, ResultViewModel, BaseNavigationEvent>(
    ResultViewModel::class.java
    ) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentResultBinding =
        FragmentResultBinding::inflate

    private val args: ResultFragmentArgs by navArgs()

    override fun init() {
        super.init()
        processImage()
    }

    private fun processImage() {
        val targetWords = listOf("kakaolu", "poligliserol")
        viewModel.processImage(args.photoBitmap, targetWords)
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.recognizedText.collect{
                binding.recognizedTextView.text = it
            }
        }
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.processedBitmap.collect{
                binding.capturedImageView.setImageBitmap(it)
            }
        }
    }
}