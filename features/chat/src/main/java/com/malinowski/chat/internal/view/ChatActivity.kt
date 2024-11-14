package com.malinowski.chat.internal.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.malinowski.chat.R
import com.malinowski.chat.databinding.ActivityMainBinding
import com.malinowski.chat.internal.ext.getComponent
import com.malinowski.chat.internal.presentation.ChatEffects
import com.malinowski.chat.internal.presentation.ChatEffects.OpenChat
import com.malinowski.chat.internal.presentation.ChatEffects.RequestPermissions
import com.malinowski.chat.internal.presentation.ChatEffects.ShowAlertDialog
import com.malinowski.chat.internal.presentation.ChatEffects.ShowToast
import com.malinowski.chat.internal.viewmodel.ChatViewModel
import javax.inject.Inject

class ChatActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel: ChatViewModel by viewModels { factory }
    private lateinit var binding: ActivityMainBinding

    companion object {

        fun createIntent(context: Context): Intent {
            return Intent(context, ChatActivity::class.java)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionResult ->
            permissionResult.forEach { (name, value) ->
                if (!value) {
                    handleEffects(
                        ShowAlertDialog(
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

        viewModel.collect(lifecycleScope, {}, ::handleEffects)

        supportFragmentManager.commit(allowStateLoss = true) {
            replace(R.id.app_fragment_container, MainFragment.newInstance())
        }
    }

    private fun handleEffects(action: ChatEffects?) {
        when (action) {
            is RequestPermissions -> requestPermissionLauncher.launch(
                action.permissions.toTypedArray()
            )
            is ShowToast -> toast(action.text)
            is ShowAlertDialog -> {
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

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}