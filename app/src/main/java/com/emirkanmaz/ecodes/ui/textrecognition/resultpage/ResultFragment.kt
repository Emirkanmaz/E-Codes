package com.emirkanmaz.ecodes.ui.textrecognition.resultpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.emirkanmaz.ecodes.base.BaseFragment
import com.emirkanmaz.ecodes.base.BaseNavigationEvent
import com.emirkanmaz.ecodes.databinding.FragmentResultBinding
import com.emirkanmaz.ecodes.domain.models.ecode.ECodeItemUI
import com.emirkanmaz.ecodes.ui.textrecognition.resultpage.adapter.ResultECodesAdapter
import com.emirkanmaz.ecodes.ui.textrecognition.resultpage.navigationevent.ResultNavigationEvent
import com.emirkanmaz.ecodes.ui.textrecognition.shared.SharedImageViewModel
import com.emirkanmaz.ecodes.utils.singleclicklistener.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResultFragment : BaseFragment<FragmentResultBinding, ResultViewModel, ResultNavigationEvent>(
    ResultViewModel::class.java
    ) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentResultBinding =
        FragmentResultBinding::inflate

    private val sharedViewModel: SharedImageViewModel by activityViewModels()
//    private val args: ResultFragmentArgs by navArgs()
    private lateinit var resultECodesAdapter: ResultECodesAdapter
    override fun init() {
        setupRecyclerView()
        super.init()
//        processImage()
    }

//    private fun processImage() {
//        viewModel.processImage(args.photoBitmap)
//    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewLifecycleOwner.lifecycleScope.launch{
            sharedViewModel.bitmap.collect{ bitmap ->
                bitmap?.let {
                    viewModel.processImage(bitmap)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.matchedECodes.collect{
                it?.let {
                    updateUI(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.processedBitmap.collect{
                binding.capturedImageView.setImageBitmap(it)
            }
        }
    }

    private fun updateUI(matchedECodes: List<ECodeItemUI>) {
        if (matchedECodes.isEmpty()) {
            binding.emptyViewHolder.isVisible = true
            binding.listViewHolder.isVisible = false
        } else {
            binding.emptyViewHolder.isVisible = false
            binding.listViewHolder.isVisible = true
            resultECodesAdapter.submitList(matchedECodes)
        }
    }

    override fun handleNavigationEvent(event: BaseNavigationEvent) {
        super.handleNavigationEvent(event)
        when (event) {
            is ResultNavigationEvent.NavigateToCropPage -> {
                findNavController().navigateUp()
            }
        }
    }

    override fun setupListeners() {
        super.setupListeners()
        binding.btnBack.setOnSingleClickListener {
            viewModel.navigateCrop()
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