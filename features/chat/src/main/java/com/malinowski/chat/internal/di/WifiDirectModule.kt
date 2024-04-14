package com.malinowski.chat.internal.di

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.example.common_arch.di.ViewModelKey
import com.malinowski.chat.internal.model.wifi.WifiDirectCore
import com.malinowski.chat.internal.model.wifi.WifiDirectCoreImpl
import com.malinowski.chat.internal.viewmodel.WifiDirectViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(
    includes = [WifiDirectModule.BindsModule::class]
)
class WifiDirectModule {

    @Module
    interface BindsModule {

        @Binds
        fun getWifiDirectCore(impl: WifiDirectCoreImpl): WifiDirectCore

        @Binds
        @IntoMap
        @ViewModelKey(WifiDirectViewModel::class)
        abstract fun bindWifiDirectViewModel(viewModel: WifiDirectViewModel): ViewModel
    }

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