package com.emirkanmaz.ecodes.ui.homepage

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.emirkanmaz.ecodes.R
import com.emirkanmaz.ecodes.utils.singleclicklistener.setOnSingleClickListener
import com.emirkanmaz.ecodes.base.BaseFragment
import com.emirkanmaz.ecodes.base.BaseNavigationEvent
import com.emirkanmaz.ecodes.databinding.FragmentHomePageBinding
import com.emirkanmaz.ecodes.ui.homepage.adapter.ECodesAdapter
import com.emirkanmaz.ecodes.ui.homepage.camerahandler.CameraHandler
import com.emirkanmaz.ecodes.ui.homepage.navigationevent.HomePageNavigationEvent
import com.emirkanmaz.ecodes.utils.extensions.isValid
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomePageFragment : BaseFragment<FragmentHomePageBinding, HomePageViewModel, HomePageNavigationEvent>(
    HomePageViewModel::class.java
) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomePageBinding =
        FragmentHomePageBinding::inflate

    private var photoUri: Uri? = null
    private lateinit var eCodesAdapter: ECodesAdapter

    private val capturePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (!photoUri.isValid()) {
                photoUri = CameraHandler().getLastCapturedPhotoUri(requireContext())
            }
            photoUri?.let { uri ->
                viewModel.navigateToCrop(photoUri!!)
            } ?: run {
                viewModel.setError(true, getString(R.string.no_photo_found))
            }
        }
    }

    override fun init() {
        super.init()
        setupRecyclerView()
    }

    override fun handleNavigationEvent(event: BaseNavigationEvent) {
        super.handleNavigationEvent(event)
        when (event) {
            is HomePageNavigationEvent.NavigateToCrop -> {
                val action = HomePageFragmentDirections.actionHomePageFragmentToCropFragment(event.photoUri)
                findNavController().safeNavigate(action)
            }
            is HomePageNavigationEvent.NavigateToCamera -> {
                launchCamera()
            }
        }
    }

    override fun setupListeners() {
        super.setupListeners()
        binding.CameraFloatingActionButton.setOnSingleClickListener {
            viewModel.navigateToCamera()
        }
    }

    private fun launchCamera() {
        try {
            photoUri = CameraHandler().createPhotoUri(requireContext())
            val cameraIntent = CameraHandler().createCameraIntent(photoUri)
            capturePhotoLauncher.launch(cameraIntent)
        } catch (e: Exception) {
            photoUri = null
            viewModel.setError(true, getString(R.string.camera_could_not_be_opened))
        }
    }

    private fun setupRecyclerView() {
        binding?.let {

            it.eCodesRecyclerView
        }
        eCodesAdapter = ECodesAdapter()
        binding.recyclerView.adapter = eCodesAdapter
    }

}