package com.emirkanmaz.ecodes.ui.textrecognition.resultpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.emirkanmaz.ecodes.base.BaseFragment
import com.emirkanmaz.ecodes.base.BaseNavigationEvent
import com.emirkanmaz.ecodes.databinding.FragmentResultBinding
import com.emirkanmaz.ecodes.ui.homepage.adapter.ECodesAdapter
import com.emirkanmaz.ecodes.ui.textrecognition.adapter.ResultECodesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResultFragment : BaseFragment<FragmentResultBinding, ResultViewModel, BaseNavigationEvent>(
    ResultViewModel::class.java
    ) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentResultBinding =
        FragmentResultBinding::inflate

    private val args: ResultFragmentArgs by navArgs()
    private lateinit var resultECodesAdapter: ResultECodesAdapter
    override fun init() {
        setupRecyclerView()
        super.init()
        processImage()
    }

    private fun processImage() {
        viewModel.processImage(args.photoBitmap)
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.matchedECodes.collect{
                it?.let {
                    resultECodesAdapter.submitList(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.processedBitmap.collect{
                binding.capturedImageView.setImageBitmap(it)
            }
        }
    }

    private fun setupRecyclerView() {
        binding?.let {
            resultECodesAdapter = ResultECodesAdapter(onCancelClick = {
                viewModel.removeItem(it)
            })
            it.eCodesRecyclerView.adapter = resultECodesAdapter
        }
    }

}