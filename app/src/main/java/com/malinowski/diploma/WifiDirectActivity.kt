package com.malinowski.diploma

import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.malinowski.diploma.wifi.WifiBroadcastReceiver

private const val TAG = "WifiDirectActivity"

class WifiDirectActivity : AppCompatActivity() {


    private val intentFilter = IntentFilter()
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var manager: WifiP2pManager

    private lateinit var reciever: WifiBroadcastReceiver

    var isWifiP2pEnabled = false

    private val textView: TextView by lazy {
        findViewById(R.id.text)
    }

    private val searchDevicesBtn: Button by lazy {
        findViewById(R.id.searchDevicesBtn)
    }

    private val peers = mutableListOf<WifiP2pDevice>()

    private val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)
        }

        if (peers.isEmpty()) {
            appendText("No devices found")
            return@PeerListListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_direct)

        initWP2P()
        searchDevicesBtn.setOnClickListener {
            try {
                manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        appendText("discoverPeers success")
                    }

                    override fun onFailure(p0: Int) {
                        appendText("discoverPeers failed")
                    }
                })
            } catch (e: SecurityException) {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
            appendText("searching for devices ...")
        }
    }

    private fun initWP2P() {
        // Indicates a change in the Wi-Fi Direct status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)

        // Indicates the state of Wi-Fi Direct connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        manager = getSystemService(WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)
    }

    fun appendText(text: String) {
        textView.append("\n ${System.currentTimeMillis()}: $text")
    }

    public override fun onResume() {
        super.onResume()
        reciever = WifiBroadcastReceiver(peerListListener, manager, channel, this)
        registerReceiver(reciever, intentFilter)
    }

    public override fun onPause() {
        super.onPause()
        unregisterReceiver(reciever)
    }
}