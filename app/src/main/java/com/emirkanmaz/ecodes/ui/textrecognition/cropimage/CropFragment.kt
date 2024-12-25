package com.emirkanmaz.ecodes.ui.textrecognition.cropimage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.emirkanmaz.diyet.utils.singleclicklistener.setOnSingleClickListener
import com.emirkanmaz.ecodes.databinding.FragmentCropBinding

class CropFragment : Fragment() {
    private var _binding: FragmentCropBinding? = null
    private val binding get() = _binding!!

    private val args: CropFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCropBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageUri = args.photoUri
        binding.cropImageView.setImageUriAsync(imageUri)

        binding.btnRotate.setOnClickListener {
            binding.cropImageView.rotateImage(90)
        }

        binding.btnCrop.setOnSingleClickListener {
            try {
                val croppedImageBitmap = binding.cropImageView.getCroppedImage()

                setFragmentResult(
                    "cropResult",
                    bundleOf("croppedBitmap" to croppedImageBitmap)
                )
                findNavController().navigateUp()
            } catch (e: Exception) {
                Log.e("CropFragment", "Error cropping: ${e.message}")
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}