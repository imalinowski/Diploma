package com.malinowski.diploma.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.malinowski.diploma.R
import com.malinowski.diploma.databinding.ActivityMainBinding
import com.malinowski.diploma.model.WifiDirectActions
import com.malinowski.diploma.model.getComponent
import com.malinowski.diploma.viewmodel.WifiDirectViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel: WifiDirectViewModel by viewModels { factory }
    private lateinit var binding: ActivityMainBinding

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
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getComponent().inject(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collect(::actions)
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.page_1 -> {
                    supportFragmentManager.commit(allowStateLoss = true) {
                        replace(R.id.fragment_container_view, PeerListFragment.newInstance())
                        addToBackStack(null)
                    }
                }
                R.id.page_2 -> {
                    supportFragmentManager.commit(allowStateLoss = true) {
                        replace(R.id.fragment_container_view, LogFragment.newInstance())
                        addToBackStack(null)
                    }
                }
            }
            true
        }
        binding.bottomNavigation.selectedItemId = R.id.page_1

    }

    private fun actions(action: WifiDirectActions?) {
        when (action) {
            is WifiDirectActions.RequestPermissions -> {
                requestPermissionLauncher.launch(action.permissions)
            }
            is WifiDirectActions.ShowToast -> {
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