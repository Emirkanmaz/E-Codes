package com.emirkanmaz.ecodes.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.viewbinding.ViewBinding
import com.emirkanmaz.ecodes.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

abstract class BaseFragment<VBinding : ViewBinding, VModel : BaseViewModel<T>, T : BaseNavigationEvent>
    (private val viewModelClass: Class<VModel>) : Fragment() {

    private var _binding: VBinding? = null
    val binding get() = _binding ?: throw IllegalStateException("Binding is null")

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VBinding

    val viewModel: VModel by lazy {
        ViewModelProvider(this)[viewModelClass]
    }

    //    singleton loading dialog
    companion object {
        private var loadingDialog: Dialog? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    protected open fun init(){
        observeNavigationEvent()
        observeLoadingState()
        observeErrorState()
        observeViewModel()
        setupListeners()
    }

    protected open fun observeNavigationEvent(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigationEvent.collect { event ->
                handleNavigationEvent(event)
            }
        }
    }

    protected open fun observeViewModel() = Unit
    protected open fun setupListeners() = Unit

    //    overriding in fragment classes
    protected open fun handleNavigationEvent(event: BaseNavigationEvent){}

    //    safeNavigate extension to NavController
    fun NavController.safeNavigate(direction: NavDirections) {
        try {
            currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
        } catch (e: Exception) {
            handleError("Navigation error. Please try again.")
        }
    }

    protected open fun observeErrorState(){
        lifecycleScope.launch {
            viewModel.errorState.collect { (error, message) ->
                if (error){
                    viewModel.setError(false)
                    handleError(message)
                }
            }
        }
    }

    protected open fun observeLoadingState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadingState.collect { isLoading ->
                    if (isLoading) showLoading() else hideLoading()
                }
            }
        }
    }

    protected open fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = Dialog(requireContext()).apply {
                setCancelable(false)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setContentView(R.layout.dialog_loading)
            }
        }
        if (!loadingDialog!!.isShowing) {
            loadingDialog!!.show()
        }
    }

    protected open fun hideLoading() {
        if (loadingDialog?.isShowing == true) {
            loadingDialog?.dismiss()
        }
    }

    protected open fun handleError(errorMessage: String? = null) {
//        set error makes the loading state false. No need for hide loading
//        hideLoading()

//        a direct error message was given its better for user not to see this message
        showSnackBar(errorMessage, snackbarType = SnackbarType.Error)
    }

    sealed interface SnackbarType {
        object Success : SnackbarType
        object Error : SnackbarType
        object Info : SnackbarType
    }

    protected open fun showSnackBar(
        message: String? = null,
        snackbarType: SnackbarType = SnackbarType.Success
    ) {

        val safeMessage = message ?: when (snackbarType) {
            is SnackbarType.Success -> getString(R.string.success_snackbar_message)
            is SnackbarType.Error -> getString(R.string.error_snackbar_message)
            is SnackbarType.Info -> getString(R.string.info_snackbar_message)
        }

        val backgroundColor = when (snackbarType) {
            is SnackbarType.Success -> "#4CAF50"
            is SnackbarType.Error -> "#F44336"
            is SnackbarType.Info -> "#2196F3"
        }

        val snackbar = Snackbar.make(requireView(), safeMessage, Snackbar.LENGTH_SHORT).apply {
            setBackgroundTint(Color.parseColor(backgroundColor))
            setTextColor(Color.WHITE)
        }

        val snackbarView = snackbar.view
        val params = snackbarView.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.topMargin = 0
        params.leftMargin = 0
        params.rightMargin = 0

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            snackbarView.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).apply {
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        snackbarView.layoutParams = params
        snackbarView.animation = AnimationUtils.loadAnimation(context, R.anim.slide_from_top)

        snackbar.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
//        viewModel.setIdle()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (loadingDialog?.isShowing == true) {
            loadingDialog?.dismiss()
        }
        loadingDialog = null
    }

}