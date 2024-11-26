package com.example.wifi_direct.internal.wifi

import android.annotation.SuppressLint
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import com.example.entities.Logs
import com.example.wifi_direct.internal.exceptions.WifiErrorHandlerFactory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import javax.inject.Inject

class WifiService
@Inject constructor(
    private val manager: WifiP2pManager,
    private val managerChannel: WifiP2pManager.Channel,
    private val logs: Logs,
    private val handlerFactory: WifiErrorHandlerFactory
) {

    @SuppressLint("MissingPermission")
    private val serviceFlow = flow {
        val channel = Channel<WifiP2pDevice>()

        val txtListener = DnsSdTxtRecordListener { _, record, device ->
            logs.logData("Service available $record ${device.deviceAddress}")
            channel.trySend(device)
        }
        val servListener = DnsSdServiceResponseListener { _, _, _ -> }
        manager.setDnsSdResponseListeners(managerChannel, servListener, txtListener)

        val serviceRequest = WifiP2pDnsSdServiceRequest.newInstance()
        manager.addServiceRequest(managerChannel, serviceRequest, handlerFactory.actionListener {
            logs.logData("Success addServiceRequest")
        })

        manager.discoverServices(managerChannel, handlerFactory.actionListener {
            logs.logData("Success discoverServices")
        })

        emit(channel.receive())
    }

    fun registerService() {
        val record: Map<String, String> = mapOf(
            "name" to "RASPBERRY"
        )

        val serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(
            "_test", "_presence._tcp", record
        )

        manager.addLocalService(managerChannel, serviceInfo, handlerFactory.actionListener {
            logs.logData("Success register service")
        })

    }

    fun unregisterService() {
        manager.clearLocalServices(managerChannel, handlerFactory.actionListener {
            logs.logData("Success unregister service")
        })
        manager.clearServiceRequests(managerChannel, handlerFactory.actionListener {
            logs.logData("Success clear service request")
        })
    }

    suspend fun discoverService(): WifiP2pDevice {
        return serviceFlow.last()
    }
}