package com.example.edge_ui.internal.factory

import android.content.Context
import androidx.appcompat.app.AlertDialog
import javax.inject.Inject

class AlertFactory
@Inject constructor() {

    fun createAlertDialog(
        context: Context,
        title: String,
        text: String,
        action: () -> Unit = {},
    ): AlertDialog.Builder {
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(text)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                action()
            }
    }
}