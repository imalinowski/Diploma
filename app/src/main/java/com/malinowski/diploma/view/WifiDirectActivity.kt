package com.malinowski.diploma.view

import android.Manifest
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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.malinowski.diploma.R
import com.malinowski.diploma.viewmodel.WifiDirectViewModel
import kotlinx.coroutines.launch

class WifiDirectActivity : AppCompatActivity() {

    private val viewModel: WifiDirectViewModel by viewModels()

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
                if (!value) Toast.makeText(
                    this,
                    "$name нужно для работы приложения",
                    Toast.LENGTH_LONG
                ).show()
                return@registerForActivityResult
            }
            processCLick()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_direct)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    textView.text = it.logText
                }
            }
        }
        searchDevicesBtn.setOnClickListener {
            processCLick()
        }
    }

    private fun processCLick() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(
                mutableListOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.INTERNET,
                ).apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        add(Manifest.permission.NEARBY_WIFI_DEVICES)
                    }
                }.toTypedArray()
            )
            Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_LONG).show()
            return
        }
        viewModel.searchForDevices()
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