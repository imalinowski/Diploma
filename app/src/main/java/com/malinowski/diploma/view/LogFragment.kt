package com.malinowski.diploma.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.malinowski.diploma.databinding.FragmentLogBinding
import com.malinowski.diploma.model.WifiDirectActions
import com.malinowski.diploma.model.getComponent
import com.malinowski.diploma.model.wifi.WifiDirectCore
import com.malinowski.diploma.viewmodel.WifiDirectState
import com.malinowski.diploma.viewmodel.WifiDirectViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class LogFragment : Fragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel: WifiDirectViewModel by activityViewModels { factory }

    private lateinit var binding: FragmentLogBinding

    private val logView: TextView by lazy {
        binding.logsText
    }
    private val searchDevicesBtn: Button by lazy {
        binding.searchDevicesBtn
    }
    private val clearLogs: Button by lazy {
        binding.clearLogs
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionResult ->
            permissionResult.forEach { (name, value) ->
                if (!value) {
                    actions(WifiDirectActions.ShowToast("$name нужно для работы приложения"))
                    return@registerForActivityResult
                }
            }
            searchForDevices()
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                with(viewModel) {
                    launch { uiState.collect(::update) }
                    launch { actions.collect(::actions) }
                }
            }
        }

        searchDevicesBtn.setOnClickListener {
            searchForDevices()
        }
        clearLogs.setOnClickListener {
            viewModel.clearText()
        }

    }

    private fun searchForDevices() {
        if (checkPermissions()) {
            viewModel.searchForDevices()
        }
    }

    private fun update(state: WifiDirectState) {
        logView.text = state.logText
    }

    private fun actions(action: WifiDirectActions?) {
        when (action) {
            is WifiDirectActions.RequestPermissions -> {
                requestPermissionLauncher.launch(action.permissions)
            }
            is WifiDirectActions.ShowToast -> {
                Toast.makeText(context, action.text, Toast.LENGTH_LONG).show()
            }
            null -> {}
        }
    }

    // TODO deal with it
    private fun checkPermissions(): Boolean {
        fun checkPermission(permission: String): Boolean {
            return ActivityCompat.checkSelfPermission(
                requireContext(), permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermissionLauncher.launch(WifiDirectCore.WIFI_CORE_PERMISSIONS)
            viewModel.appendText("Permission Denied")
            return false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !checkPermission(Manifest.permission.NEARBY_WIFI_DEVICES)
        ) {
            requestPermissionLauncher.launch(WifiDirectCore.WIFI_CORE_PERMISSIONS_13)
            viewModel.appendText("Permission Denied")
            return false
        }
        return true
    }

    companion object {
        fun newInstance() = LogFragment()
    }
}