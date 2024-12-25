package com.emirkanmaz.ecodes.ui.homepage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.emirkanmaz.diyet.utils.singleclicklistener.setOnSingleClickListener
import com.emirkanmaz.ecodes.R
import com.emirkanmaz.ecodes.base.BaseFragment
import com.emirkanmaz.ecodes.base.BaseNavigationEvent
import com.emirkanmaz.ecodes.databinding.FragmentHomePageBinding
import com.emirkanmaz.ecodes.ui.homepage.navigationevent.HomePageNavigationEvent
import com.emirkanmaz.ecodes.ui.textrecognition.TextRecognitionFragmentDirections
import java.io.File

class HomePageFragment : BaseFragment<FragmentHomePageBinding, HomePageViewModel, BaseNavigationEvent>(
    HomePageViewModel::class.java
) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomePageBinding =
        FragmentHomePageBinding::inflate

    private lateinit var photoUri: Uri

    private val capturePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photoUri = photoUri
            if (photoUri != null) {
                val action = HomePageFragmentDirections.actionHomePageFragmentToCropFragment(photoUri)
                findNavController().safeNavigate(action)
            }
        } else {
            viewModel.setError(true, "Fotoğraf çekme işlemi iptal edildi.")
        }
    }



    override fun init() {
        super.init()

        setFragmentResultListener("cropResult") { _, bundle ->
            val croppedBitmap = bundle.getParcelable<Bitmap>("croppedBitmap")
            croppedBitmap?.let {
                val action = TextRecognitionFragmentDirections.actionTextRecognitionFragmentToResultFragment(it)
//                findNavController().safeNavigate(action)
                findNavController().navigate(R.id.resultFragment, bundle)
            }
        }

    }


    override fun handleNavigationEvent(event: BaseNavigationEvent) {
        super.handleNavigationEvent(event)

    }

    override fun setupListeners() {
        super.setupListeners()
        binding.CameraFloatingActionButton.setOnSingleClickListener {
//            viewModel.navigateToCamera()
            launchCamera()
        }
    }

    private fun launchCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            val photoFile = File.createTempFile(
                "photo_${System.currentTimeMillis()}", ".jpg", requireContext().cacheDir
            ).apply {
                deleteOnExit()
            }
            photoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                photoFile
            )
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            capturePhotoLauncher.launch(cameraIntent)
        } catch (e: Exception) {
            viewModel.setError(true, "Kamera açılırken hata oluştu")
        }
    }

}