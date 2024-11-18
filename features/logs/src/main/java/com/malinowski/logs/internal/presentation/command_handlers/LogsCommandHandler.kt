package com.malinowski.logs.internal.presentation.command_handlers

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.common_arch.CommandHandler
import com.example.entities.Logs
import com.malinowski.logs.internal.presentation.LogCommands
import com.malinowski.logs.internal.presentation.LogEvents
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

class LogsCommandHandler
@Inject constructor(
    private val logs: Logs,
    private val context: Context
) : CommandHandler<LogCommands, LogEvents> {

    override suspend fun handle(command: LogCommands): LogEvents? {
        when (command) {
            is LogCommands.AddLog -> logs.logData(command.text)
            LogCommands.Clear -> logs.clearLogs()
            is LogCommands.Save -> saveLogs(context, command.fileName)
            LogCommands.Restore -> Unit // just return logs.data
        }
        return LogEvents.UpdateLog(logs.getLogs())
    }

    private fun saveLogs(context: Context, fileName: String) {
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

        val bytes = logs.getLogs().toByteArray()
        outputStream?.write(bytes)
        outputStream?.close()
    }
}