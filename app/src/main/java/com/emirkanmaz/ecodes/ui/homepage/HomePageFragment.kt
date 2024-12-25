package com.emirkanmaz.ecodes.ui.homepage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.emirkanmaz.diyet.utils.singleclicklistener.setOnSingleClickListener
import com.emirkanmaz.ecodes.R
import com.emirkanmaz.ecodes.base.BaseFragment
import com.emirkanmaz.ecodes.base.BaseNavigationEvent
import com.emirkanmaz.ecodes.databinding.FragmentHomePageBinding
import com.emirkanmaz.ecodes.ui.textrecognition.TextRecognitionFragmentDirections
import java.io.File

class HomePageFragment : BaseFragment<FragmentHomePageBinding, HomePageViewModel, BaseNavigationEvent>(
    HomePageViewModel::class.java
) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomePageBinding =
        FragmentHomePageBinding::inflate

    private var photoUri: Uri? = null

    private val capturePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (photoUri == null) {
                photoUri = getLastCapturedPhotoUri()
            }
            photoUri?.let { uri ->
                val action = HomePageFragmentDirections.actionHomePageFragmentToCropFragment(uri)
                findNavController().safeNavigate(action)
            } ?: run {
                viewModel.setError(true, "Fotoğraf bulunamadı.")
            }
        } else {
            viewModel.setError(true, "Fotoğraf çekme işlemi iptal edildi.")
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

        cropListener()

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

            if (!photoFile.exists()) {
                viewModel.setError(true, "Dosya oluşturulamadı.")
            }

            capturePhotoLauncher.launch(cameraIntent)
        } catch (e: Exception) {
            photoUri = null
            viewModel.setError(true, "Kamera açılırken hata oluştu")
        }
    }

    private fun getLastCapturedPhotoUri(): Uri? {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val id = cursor.getLong(idColumn)
                return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
            }
        }
        return null
    }


    private fun cropListener(){
        setFragmentResultListener("cropResult") { _, bundle ->
            val croppedBitmap = bundle.getParcelable<Bitmap>("croppedBitmap")
            croppedBitmap?.let {
                val action = TextRecognitionFragmentDirections.actionTextRecognitionFragmentToResultFragment(it)
//                findNavController().safeNavigate(action)
                findNavController().navigate(R.id.resultFragment, bundle)
            }
        }
    }

}