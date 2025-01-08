package com.emirkanmaz.ecodes.ui.homepage

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import com.emirkanmaz.ecodes.R
import com.emirkanmaz.ecodes.base.BaseFragment
import com.emirkanmaz.ecodes.base.BaseNavigationEvent
import com.emirkanmaz.ecodes.databinding.FragmentHomePageBinding
import com.emirkanmaz.ecodes.ui.homepage.adapter.ECodesAdapter
import com.emirkanmaz.ecodes.ui.homepage.camerahandler.CameraHandler
import com.emirkanmaz.ecodes.ui.homepage.navigationevent.HomePageNavigationEvent
import com.emirkanmaz.ecodes.utils.extensions.isValid
import com.emirkanmaz.ecodes.utils.pressbackagaintoexit.pressBackAgainToExit
import com.emirkanmaz.ecodes.utils.singleclicklistener.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomePageFragment : BaseFragment<FragmentHomePageBinding, HomePageViewModel, HomePageNavigationEvent>(
    HomePageViewModel::class.java
) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomePageBinding =
        FragmentHomePageBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pressBackAgainToExit(
            onExitAction = { requireActivity().finish() },
            onWarningAction = { showSnackBar(getString(R.string.press_back_again_to_exit), SnackbarType.Error) }
        )
    }

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
        clearTempFiles(requireContext())
        super.init()
        setupRecyclerView()
    }

    fun clearTempFiles(context: Context) {
        context.cacheDir.listFiles()?.forEach { file ->
            if (file.name.startsWith("photo_")) {
                file.delete()
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewModel.eCodeDetails.observe(viewLifecycleOwner) {
            eCodesAdapter.submitList(it)
        }
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
            is HomePageNavigationEvent.NavigateToDetail -> {
                val action = HomePageFragmentDirections.actionHomePageFragmentToECodeDetailFragment()
                findNavController().safeNavigate(action)
            }
        }
    }

    override fun setupListeners() {
        super.setupListeners()

        binding.apply {

            drawerButton.setOnSingleClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }

            navigationView.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_home -> {
                        Toast.makeText(context, "Home seçildi!", Toast.LENGTH_SHORT).show()
                    }
                    R.id.nav_settings -> {
                        Toast.makeText(context, "Settings seçildi!", Toast.LENGTH_SHORT).show()
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }


            CameraFloatingActionButton.setOnSingleClickListener {
                viewModel.navigateToCamera()
            }

            searchEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    eCodesAdapter.filter(s.toString())
                    clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            clearButton.setOnClickListener {
                searchEditText.text.clear()
            }

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
            eCodesAdapter = ECodesAdapter(onECodeClick = {
                viewModel.navigateToDetail()
//                val action = HomePageFragmentDirections(it)
//                findNavController().safeNavigate(action)
            })
            it.eCodesRecyclerView.adapter = eCodesAdapter
        }
    }

}