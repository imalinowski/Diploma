package com.example.wifi_direct.internal.wifi

import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import com.example.entities.Logs
import javax.inject.Inject

class WifiService
@Inject constructor(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val logs: Logs
) {

    fun startRegistration() {
        val record: Map<String, String> = mapOf(
            "name" to "RASPBERRY"
        )

        val serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(
            "_test", "_presence._tcp", record
        )

        manager.addLocalService(channel, serviceInfo, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                logs.logData("Success register service")
            }

            override fun onFailure(reason: Int) {
                logs.logData("Fail register service reason $reason")
            }
        })

        discoverService()
    }

    private fun discoverService() {
        val txtListener = DnsSdTxtRecordListener { fullDomain, record, device ->
            logs.logData("DnsSdTxtRecord available $fullDomain $record $device")
        }
        val servListener = DnsSdServiceResponseListener { instanceName, registrationType, resourceType ->
            logs.logData("DnsSdServiceResponse available $instanceName $registrationType $resourceType")
        }
        manager.setDnsSdResponseListeners(channel, servListener, txtListener)

        val serviceRequest = WifiP2pDnsSdServiceRequest.newInstance()
        manager.addServiceRequest(
            channel,
            serviceRequest,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    logs.logData("Success addServiceRequest")
                }

                override fun onFailure(reason: Int) {
                    logs.logData("Fail addServiceRequest reason $reason")
                }
            }
        )

        manager.discoverServices(
            channel,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    logs.logData("Success discoverServices success")
                }

                override fun onFailure(reason: Int) {
                    logs.logData("Fail addServiceRequest reason $reason")
                }
            }
        )
    }
}