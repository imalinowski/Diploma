package com.example.wifi_direct.internal.wifi

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import com.example.entities.Logs
import com.example.wifi_direct.internal.exceptions.WifiErrorHandlerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

private const val NAME_KEY = "NAME"
private const val NAME_VALUE = "RASPBERRY"
private const val WAIT_FOR_NEW_SERVICE = 5000L

@Singleton
class WifiService
@Inject constructor(
    private val manager: WifiP2pManager,
    private val managerChannel: WifiP2pManager.Channel,
    private val logs: Logs,
    private val handlerFactory: WifiErrorHandlerFactory
) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    private val workers: MutableSet<String> = mutableSetOf()

    fun registerService() {
        val record = mapOf(NAME_KEY to NAME_VALUE)

        val serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(
            "_test", "_presence._tcp", record
        )

        manager.addLocalService(managerChannel, serviceInfo, handlerFactory.actionListener {
            logs.logData("Success register service")
        })
    }

    private suspend fun searchForNewServices(): String {
        val channel = Channel<WifiP2pDevice>()

        registerService() // иногда ранее зарегестрированный сервис не видно перерегистрация

        val txtListener = DnsSdTxtRecordListener { _, record, device ->
            logs.logData("Service available $record ${device.deviceAddress} ${device.deviceName}")
            channel.trySend(device)
            if (record[NAME_KEY] == NAME_VALUE && device.deviceName.isNotEmpty()) { // проверка на ублюдские принтеры
                workers.add(device.deviceName)
            }
        }
        val servListener = DnsSdServiceResponseListener { _, _, _ -> }
//        val servListener = UpnpServiceResponseListener { _, _, _ -> }
        manager.setDnsSdResponseListeners(managerChannel, servListener, txtListener)
//        manager.setUpnpServiceResponseListener(managerChannel, )

        val serviceRequest = WifiP2pDnsSdServiceRequest.newInstance()
        manager.addServiceRequest(managerChannel, serviceRequest, handlerFactory.actionListener {
            logs.logData("Success addServiceRequest")
        })

        manager.discoverServices(managerChannel, handlerFactory.actionListener {
            logs.logData("Success discoverServices")
        })

        return channel.receive().deviceName
    }

    fun unregisterService() {
        manager.clearLocalServices(managerChannel, handlerFactory.actionListener {
            logs.logData("Success unregister service")
        })
        manager.clearServiceRequests(managerChannel, handlerFactory.actionListener {
            logs.logData("Success clear service request")
        })
    }

    suspend fun getWorkersName(): Set<String> = supervisorScope {
        // todo figure out how to do it with multiple devices
        withTimeoutOrNull(WAIT_FOR_NEW_SERVICE) {
            // немного поиск новых устройств
            searchForNewServices()
        }
        workers
    }
}