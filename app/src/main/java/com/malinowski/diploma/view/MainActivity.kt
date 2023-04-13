package com.malinowski.diploma.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.malinowski.diploma.R
import com.malinowski.diploma.databinding.ActivityMainBinding
import com.malinowski.diploma.model.WifiDirectActions
import com.malinowski.diploma.model.WifiDirectActions.OpenChat
import com.malinowski.diploma.model.getComponent
import com.malinowski.diploma.viewmodel.WifiDirectViewModel
import kotlinx.coroutines.flow.collectLatest
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
                    actions(
                        WifiDirectActions.ShowAlertDialog(
                            text = "$name нужно для работы приложения"
                        )
                    )
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
                viewModel.actions.collectLatest { actions(it) }
            }
        }

        supportFragmentManager.commit(allowStateLoss = true) {
            replace(R.id.app_fragment_container, MainFragment.newInstance())
            addToBackStack(null)
        }
    }

    private fun actions(action: WifiDirectActions?) {
        when (action) {
            is WifiDirectActions.RequestPermissions -> {
                requestPermissionLauncher.launch(action.permissions)
            }
            is WifiDirectActions.ShowToast -> {
                Toast.makeText(this, action.text, Toast.LENGTH_LONG).show()
            }
            is WifiDirectActions.ShowAlertDialog -> {
                AlertDialog.Builder(this)
                    .setTitle(action.title)
                    .setMessage(action.text)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        action.dialogAction()
                    }
                    .show()
            }
            is OpenChat -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.app_fragment_container,
                        ChatFragment.newInstance(action.peer)
                    )
                    addToBackStack(null)
                }
            }
            else -> {}
        }
    }

}