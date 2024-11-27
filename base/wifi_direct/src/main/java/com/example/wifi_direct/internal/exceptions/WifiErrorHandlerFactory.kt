package com.example.wifi_direct.internal.exceptions

import android.net.wifi.p2p.WifiP2pManager.ActionListener
import android.net.wifi.p2p.WifiP2pManager.BUSY
import android.net.wifi.p2p.WifiP2pManager.NO_SERVICE_REQUESTS
import android.net.wifi.p2p.WifiP2pManager.P2P_UNSUPPORTED
import android.util.Log
import com.example.entities.Logs
import javax.inject.Inject

private const val POSSIBLE_ERROR_SOLUTIONS = """
Check following ( но это не точно ): 
- location services must be enabled for wifi direct to work 
- check enabled permissions in settings
- check enabled wifi
"""

class WifiErrorHandlerFactory
@Inject constructor(
    private val logs: Logs
) {

    fun actionListener(
        onFail: (Int, String) -> Unit = { _, _ -> },
        onSuccess: () -> Unit = {}
    ) = object : ActionListener {
        override fun onSuccess() {
            onSuccess()
        }

        override fun onFailure(reason: Int) {
            val message = when (reason) {
                P2P_UNSUPPORTED -> "P2P_UNSUPPORTED"
                BUSY -> "BUSY"
                NO_SERVICE_REQUESTS -> "NO_SERVICE_REQUESTS"
                else -> "ERROR! $POSSIBLE_ERROR_SOLUTIONS"
            }
            Log.e("RASPBERRY", "Error : $reason $message")
            logs.logData("Error : $reason $message")
            onFail(reason, message)
        }
    }
}