package com.example.wifi_direct.internal.exceptions

import android.net.wifi.p2p.WifiP2pManager.ActionListener
import android.net.wifi.p2p.WifiP2pManager.ERROR
import android.net.wifi.p2p.WifiP2pManager.BUSY
import android.net.wifi.p2p.WifiP2pManager.NO_SERVICE_REQUESTS
import android.net.wifi.p2p.WifiP2pManager.P2P_UNSUPPORTED
import android.util.Log
import com.example.entities.Logs
import javax.inject.Inject

const val POSSIBLE_ERROR_SOLUTIONS = """
Check following may be helpful ( но это не точно ) : 
- location services must be enabled for wifi direct to work 
"""

class WifiDirectErrorHandlerFactory
@Inject constructor(
    private val logs: Logs
) {

    fun actionListener(
        onFail: (reason: Int, message: String) -> Unit = { _, _ -> },
        onSuccess: () -> Unit = {}
    ) = object : ActionListener {

        override fun onSuccess() {
            onSuccess()
        }

        override fun onFailure(reason: Int) {
            val message = when (reason) {
                ERROR -> "internal error"
                P2P_UNSUPPORTED -> "P2P_UNSUPPORTED"
                BUSY -> "BUSY"
                NO_SERVICE_REQUESTS -> "NO_SERVICE_REQUESTS"
                else -> "UNKNOWN ERROR\n$POSSIBLE_ERROR_SOLUTIONS"
            }
            Log.e("RASPBERRY", "Error : $reason $message")
            logs.logData("Error : $reason $message")
            onFail(reason, message)
        }
    }
}