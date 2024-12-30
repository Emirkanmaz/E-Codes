package com.emirkanmaz.ecodes.ui.textrecognition.cropimage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.emirkanmaz.ecodes.R
import com.emirkanmaz.ecodes.utils.singleclicklistener.setOnSingleClickListener
import com.emirkanmaz.ecodes.base.BaseFragment
import com.emirkanmaz.ecodes.base.BaseNavigationEvent
import com.emirkanmaz.ecodes.databinding.FragmentCropBinding
import com.emirkanmaz.ecodes.ui.textrecognition.cropimage.navigationevent.CropNavigationEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CropFragment : BaseFragment<FragmentCropBinding, CropViewModel, CropNavigationEvent>(
    CropViewModel::class.java
) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCropBinding =
        FragmentCropBinding::inflate

    private val args: CropFragmentArgs by navArgs()

    override fun init() {
        super.init()
        loadImageToCropper()
        setupListeners()
    }

    private fun loadImageToCropper() {
        val imageUri = args.photoUri
        binding.cropImageView.setImageUriAsync(imageUri)
    }

    override fun handleNavigationEvent(event: BaseNavigationEvent) {
        super.handleNavigationEvent(event)
        when (event) {
            is CropNavigationEvent.NavigateToHomePage -> {
                findNavController().navigateUp()
            }
            is CropNavigationEvent.NavigateToResultFragment -> {
                val action = CropFragmentDirections.actionCropFragmentToResultFragment(event.croppedImage)
                findNavController().safeNavigate(action)
            }
        }
    }

    override fun setupListeners() {
        binding.btnRotate.setOnClickListener {
            binding.cropImageView.rotateImage(90)
        }
        binding.btnCrop.setOnSingleClickListener {
            try {
                binding.cropImageView.getCroppedImage()?.let { croppedImage ->
                    viewModel.navigateToResultFragment(croppedImage)
                }
            } catch (e: Exception) {
                viewModel.setError(true, getString(R.string.an_error_occurred_during_crop))
            }
        }
        binding.btnCancel.setOnSingleClickListener {
            viewModel.navigateToHomePage()
        }
    }

}