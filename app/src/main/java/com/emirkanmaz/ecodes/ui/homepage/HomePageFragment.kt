package com.emirkanmaz.ecodes.ui.homepage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.doOnTextChanged
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
import com.emirkanmaz.ecodes.utils.rateus.rateUs
import com.emirkanmaz.ecodes.utils.singleclicklistener.setOnSingleClickListener
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
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
            onWarningAction = {
                showSnackBar(
                    getString(R.string.press_back_again_to_exit),
                    SnackbarType.Error
                )
            }
        )
    }

    private var photoUri: Uri? = null
    private lateinit var eCodesAdapter: ECodesAdapter

    private val capturePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
        isFirstTime()
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
        viewModel.nativeAdList.observe(viewLifecycleOwner) {
            eCodesAdapter.submitAdList(it)
        }
        viewModel.searchQuery.observe(viewLifecycleOwner) {
            eCodesAdapter.filter(it)
        }
    }

    override fun handleNavigationEvent(event: BaseNavigationEvent) {
        super.handleNavigationEvent(event)
        when (event) {
            is HomePageNavigationEvent.NavigateToCrop -> {
                val action =
                    HomePageFragmentDirections.actionHomePageFragmentToCropFragment(event.photoUri)
                findNavController().safeNavigate(action)
            }

            is HomePageNavigationEvent.NavigateToCamera -> {
                launchCamera()
            }

            is HomePageNavigationEvent.NavigateToDetail -> {
                val action =
                    HomePageFragmentDirections.actionHomePageFragmentToECodeDetailFragment(event.eCode)
                findNavController().safeNavigate(action)
            }

            is HomePageNavigationEvent.NavigateToPrivacyPolicy -> {
                val action = HomePageFragmentDirections.actionHomePageFragmentToPrivacyPolicyFragment()
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
                    R.id.shareApp -> {
                        shareApp()
                    }
                    R.id.rateUs -> {
                        requireContext().rateUs()
                    }
                    R.id.eCodeRecognition -> {
                        viewModel.navigateToCamera()
                    }
                    R.id.privacyPolicy -> {
                        viewModel.navigateToPrivacyPolicy()
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

            CameraFloatingActionButton.setOnSingleClickListener {
                viewModel.onCameraClicked()
                viewModel.navigateToCamera()
            }

            searchEditText.doOnTextChanged { text, _, _, _ ->
                viewModel.setSearchQuery(text.toString())
                clearButton.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
            }

            clearButton.setOnClickListener {
                searchEditText.text.clear()
                viewModel.setSearchQuery("")
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
            eCodesAdapter = ECodesAdapter(
                onECodeClick = { eCodeItemUI ->
                    viewModel.onDetailClicked(eCodeItemUI.eCode)
                    viewModel.navigateToDetail(eCodeItemUI.eCode)
                },
                onEmptyAddClick = {
                    requireContext().rateUs()
                }
            )
            it.eCodesRecyclerView.adapter = eCodesAdapter
        }
    }

    private fun isFirstTime() {
        if (viewModel.isFirstTime()) {
            showOverlay()
        }
    }

    private fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.app_share_text))
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share"))
    }

    private fun showOverlay() {
        TapTargetView.showFor(requireActivity(),
            TapTarget.forView(
                binding.searchView,
                getString(R.string.onboarding_search)
            )
                .outerCircleColor(R.color.primary_purple)
                .outerCircleAlpha(0.96f)
                .targetCircleColor(R.color.white)
                .titleTextSize(20)
                .titleTextColor(R.color.white)
                .descriptionTextSize(10)
                .descriptionTextColor(R.color.white)
                .textColor(R.color.white)
                .textTypeface(Typeface.SANS_SERIF)
                .dimColor(R.color.black)
                .drawShadow(true)
                .cancelable(false)
                .tintTarget(true)
                .transparentTarget(false)
                .icon(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.search
                    )
                )
                .targetRadius(60),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)

                    val itemViewHolder =
                        binding.eCodesRecyclerView.findViewHolderForAdapterPosition(1)
                    val itemView = itemViewHolder?.itemView

                    if (itemView != null) {
                        TapTargetView.showFor(requireActivity(),
                            TapTarget.forView(
                                itemView.findViewById<ImageView>(R.id.detailImageView),
                                getString(R.string.onboarding_card_detail)
                            )
                                .outerCircleColor(R.color.primary_purple)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(20)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(10)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .textTypeface(Typeface.SANS_SERIF)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(true)
                                .targetRadius(60),
                            object : TapTargetView.Listener() {
                                override fun onTargetClick(view: TapTargetView) {
                                    super.onTargetClick(view)

                                    TapTargetView.showFor(requireActivity(),
                                        TapTarget.forView(
                                            binding.CameraFloatingActionButton,
                                            getString(R.string.onboarding_camera),
                                            getString(R.string.onboarding_thanks)
                                        )
                                            .outerCircleColor(R.color.primary_purple)
                                            .outerCircleAlpha(0.96f)
                                            .targetCircleColor(R.color.white)
                                            .titleTextSize(20)
                                            .titleTextColor(R.color.white)
                                            .descriptionTextSize(12)
                                            .descriptionTextColor(R.color.white)
                                            .textColor(R.color.white)
                                            .textTypeface(Typeface.SANS_SERIF)
                                            .dimColor(R.color.black)
                                            .drawShadow(true)
                                            .cancelable(false)
                                            .tintTarget(true)
                                            .transparentTarget(true)
                                            .targetRadius(60),
                                        object : TapTargetView.Listener() {
                                            override fun onTargetClick(view: TapTargetView) {
                                                super.onTargetClick(view)
                                                viewModel.setFirstTime()
                                            }
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        )
    }


}