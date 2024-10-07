package com.malinowski.chat.internal.presentation.command_handlers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.example.common_arch.CommandHandler
import com.example.wifi_direct.api.WIFI_CORE_PERMISSIONS
import com.example.wifi_direct.api.WIFI_CORE_PERMISSIONS_13
import com.malinowski.chat.internal.presentation.ChatCommands
import com.malinowski.chat.internal.presentation.ChatCommands.CheckPermissions
import com.malinowski.chat.internal.presentation.ChatEvents
import com.malinowski.chat.internal.presentation.ChatEvents.WifiDirectEvents.PermissionMissed
import com.malinowski.chat.internal.presentation.ChatEvents.WifiDirectEvents.PermissionsOkay
import javax.inject.Inject

class PermissionsCommandHandler
@Inject constructor(
    val context: Context
) : CommandHandler<ChatCommands, ChatEvents> {

    override suspend fun handle(command: ChatCommands): ChatEvents? {
        if (command !is CheckPermissions) {
            return null
        }

        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            return PermissionMissed(
                permissions = WIFI_CORE_PERMISSIONS,
                log = "Denied > ${Manifest.permission.ACCESS_FINE_LOCATION}"
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !checkPermission(Manifest.permission.NEARBY_WIFI_DEVICES)
        ) {
            return PermissionMissed(
                permissions = WIFI_CORE_PERMISSIONS_13,
                log = "Denied > ${Manifest.permission.NEARBY_WIFI_DEVICES}"
            )
        }

        return PermissionsOkay
    }

    private fun checkPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            context, permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}