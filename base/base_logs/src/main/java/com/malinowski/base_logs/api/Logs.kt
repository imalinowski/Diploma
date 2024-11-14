package com.malinowski.base_logs.api

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

// Это класс со всеми логами приложения показывается в LogFragment
// !!! пока нет потребности делать Thread Save но это не точно

private const val LINE_SEPARATOR = "\n"

@Singleton
class Logs
@Inject constructor() {

    private var logs: String = ""

    fun getLogs() = logs

    fun logData(log: String) {
        logs += LINE_SEPARATOR + log
    }

    fun clearLogs() {
        logs = ""
    }

    fun saveLogs(context: Context, fileName: String) {
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
            val file = File(path, "$fileName.txt")
            FileOutputStream(file)
        }

        val bytes = logs.toByteArray()
        outputStream?.write(bytes)
        outputStream?.close()
    }
}