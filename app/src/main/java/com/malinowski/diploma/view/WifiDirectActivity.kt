package com.malinowski.diploma.view

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.NEARBY_WIFI_DEVICES
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.malinowski.diploma.R
import com.malinowski.diploma.databinding.ActivityWifiDirectBinding
import com.malinowski.diploma.model.WifiDirectActions
import com.malinowski.diploma.model.WifiDirectActions.ShowToast
import com.malinowski.diploma.model.getComponent
import com.malinowski.diploma.model.wifi.WifiDirectCore
import com.malinowski.diploma.viewmodel.WifiDirectState
import com.malinowski.diploma.viewmodel.WifiDirectViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class WifiDirectActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel: WifiDirectViewModel by viewModels { factory }
    private lateinit var binding: ActivityWifiDirectBinding

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
                    actions(ShowToast("$name нужно для работы приложения"))
                    return@registerForActivityResult
                }
            }
            searchForDevices()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWifiDirectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getComponent().inject(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
            is ShowToast -> {
                Toast.makeText(this, action.text, Toast.LENGTH_LONG).show()
            }
            null -> {}
        }
    }

    public override fun onResume() {
        super.onResume()
        viewModel.registerReceiver()
    }

    public override fun onPause() {
        super.onPause()
        viewModel.unregisterReceiver()
    }

    // TODO deal with it
    private fun checkPermissions(): Boolean {
        fun checkPermission(permission: String): Boolean {
            return ActivityCompat.checkSelfPermission(
                this, permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        if (!checkPermission(ACCESS_FINE_LOCATION)) {
            requestPermissionLauncher.launch(WifiDirectCore.WIFI_CORE_PERMISSIONS)
            viewModel.appendText("Permission Denied")
            return false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !checkPermission(NEARBY_WIFI_DEVICES)
        ) {
            requestPermissionLauncher.launch(WifiDirectCore.WIFI_CORE_PERMISSIONS_13)
            viewModel.appendText("Permission Denied")
            return false
        }
        return true
    }
}