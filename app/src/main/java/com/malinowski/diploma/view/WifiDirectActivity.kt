package com.malinowski.diploma.view

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.malinowski.diploma.R
import com.malinowski.diploma.model.WifiDirectActions
import com.malinowski.diploma.model.WifiDirectActions.ShowToast
import com.malinowski.diploma.model.getComponent
import com.malinowski.diploma.viewmodel.WifiDirectState
import com.malinowski.diploma.viewmodel.WifiDirectViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class WifiDirectActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel: WifiDirectViewModel by viewModels { factory }

    private val textView: TextView by lazy {
        findViewById(R.id.text)
    }

    private val searchDevicesBtn: Button by lazy {
        findViewById(R.id.searchDevicesBtn)
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
            viewModel.searchForDevices()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_direct)
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
            viewModel.searchForDevices()
        }
    }

    private fun update(state: WifiDirectState) {
        textView.text = state.logText
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
}