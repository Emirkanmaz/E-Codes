package com.emirkanmaz.ecodes.ui.textrecognition.resultpage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.emirkanmaz.ecodes.base.BaseFragment
import com.emirkanmaz.ecodes.base.BaseNavigationEvent
import com.emirkanmaz.ecodes.databinding.FragmentResultBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


class ResultFragment : BaseFragment<FragmentResultBinding, ResultViewModel, BaseNavigationEvent>(
    ResultViewModel::class.java
    ) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentResultBinding =
        FragmentResultBinding::inflate

    override fun init() {
        super.init()
        viewModel.setLoading(true)
        val bitmap = arguments?.getParcelable<Bitmap>("croppedBitmap")
        bitmap?.let { originalBitmap ->

            recognizeAndDrawText(originalBitmap)
        }
//        val recognizedText = arguments?.getString("recognizedText")
//        recognizedText?.let {
//            binding.recognizedTextView.text = it
//        }

//
//        binding.recognizedTextView.text = navArgs<ResultFragmentArgs>().value.recognizedText
    }

    private fun recognizeAndDrawText(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val elist = listOf<String>("kakaolu", "poligliserol")

        recognizer
            .process(image)
            .addOnSuccessListener { visionText ->

                binding.recognizedTextView.text = visionText.text

                val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = Canvas(mutableBitmap)
                val paint = Paint().apply {
                    color = Color.RED
                    style = Paint.Style.STROKE
                    strokeWidth = 3f
                }


                for (block in visionText.textBlocks) {
                    for (line in block.lines) {
                        for (element in line.elements) {
                            if (elist.any { it.equals(element.text, ignoreCase = true) }) {
                                element.boundingBox?.let { box ->
                                    canvas.drawRect(box, paint)
                                }
                            }
                        }
                    }
                }

                binding.capturedImageView.setImageBitmap(mutableBitmap)

                binding.recognizedTextView.text = visionText.text
                viewModel.setLoading(false)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(), "Text recognition failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


}