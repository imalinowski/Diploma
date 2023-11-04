package com.malinowski.diploma.view

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel: WifiDirectViewModel by viewModels { factory }
    private lateinit var binding: ActivityMainBinding

    companion object {

        fun createIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

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

            is WifiDirectActions.SaveLogs -> CoroutineScope(Dispatchers.IO).launch {
                saveFile(this@MainActivity, action.filename, action.text, "txt")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "SAVED SUCCESS!", Toast.LENGTH_LONG).show()
                }
            }

            else -> {}
        }
    }

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