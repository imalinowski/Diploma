package com.example.wifi_direct.internal.exceptions

import android.annotation.SuppressLint
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pWfdInfo

/*
Set WifiP2p Device Name
set device name programatically
*/
fun WifiP2pManager.markDeviceName(
    channel: WifiP2pManager.Channel,
    deviceName: String,
    actionListener: WifiP2pManager.ActionListener
) {
    try {
        val method = this.javaClass.getMethod(
            "setDeviceName", WifiP2pManager.Channel::class.java, String::class.java, WifiP2pManager.ActionListener::class.java
        )

        method.invoke(this, channel, deviceName, actionListener)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@SuppressLint("NewApi", "MissingPermission")
fun WifiP2pManager.markThisDevice(
    channel: WifiP2pManager.Channel,
    deviceName: String,
    actionListener: WifiP2pManager.ActionListener
) {
    this.setWfdInfo(channel, WifiP2pWfdInfo(), actionListener)
}

fun WifiP2pManager.reflectDiscoverPeers(
    channel: WifiP2pManager.Channel,
    deviceName: String,
    actionListener: WifiP2pManager.ActionListener
) {
    try {
        val paramTypes = arrayOf(
            WifiP2pManager.Channel::class.java,
            WifiP2pManager.ActionListener::class.java
        )
        val setDeviceName = this.javaClass.getMethod(
            "discoverPeers", *paramTypes
        )
        setDeviceName.isAccessible = true

        val argsList = arrayOf(
            channel,
            actionListener
        )
        setDeviceName.invoke(this, *argsList)
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}