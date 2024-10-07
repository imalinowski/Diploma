package com.malinowski.chat.internal.view

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
import com.malinowski.chat.internal.presentation.ChatEffects.SaveLogs
import com.malinowski.chat.internal.presentation.ChatEffects.ShowAlertDialog
import com.malinowski.chat.internal.presentation.ChatEffects.ShowToast
import com.malinowski.chat.internal.viewmodel.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
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
            addToBackStack(null)
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
            // todo почему не используется activity lifecycle ?
            is SaveLogs -> CoroutineScope(Dispatchers.IO).launch {
                saveFile(this@ChatActivity, action.filename, action.text, "txt")
                withContext(Dispatchers.Main) {
                    toast(resources.getString(R.string.saved_success))
                }
            }

            else -> {}
        }
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    // todo move to better place
    @Throws(IOException::class)
    private fun saveFile(context: Context, fileName: String, text: String, extension: String) {
        val outputStream: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            val extVolumeUri: Uri = MediaStore.Files.getContentUri("external")
            val fileUri: Uri? = context.contentResolver.insert(extVolumeUri, values)
            context.contentResolver.openOutputStream(fileUri!!)
        } else {
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString()
            val file = File(path, "$fileName.$extension")
            FileOutputStream(file)
        }

        val bytes = text.toByteArray()
        outputStream?.write(bytes)
        outputStream?.close()
    }

}