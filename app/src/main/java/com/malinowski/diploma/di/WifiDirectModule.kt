package com.malinowski.diploma.di

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides

@Module
class WifiDirectModule {

    @Provides
    fun injectIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }
    }

    @Provides
    fun injectWifiP2pManager(context: Context): WifiP2pManager {
        return context.getSystemService(AppCompatActivity.WIFI_P2P_SERVICE) as WifiP2pManager
    }

    @Provides
    fun injectChannel(
        context: Context,
        manager: WifiP2pManager
    ): WifiP2pManager.Channel {
        return manager.initialize(context, context.mainLooper, null)
    }
}