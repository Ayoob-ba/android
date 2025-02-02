package com.tonapps.tonkeeper.ui.screen.browser.dapp

import android.app.Application
import android.net.Uri
import com.tonapps.extensions.appVersionName
import com.tonapps.extensions.filterList
import com.tonapps.tonkeeper.manager.tonconnect.TonConnectManager
import com.tonapps.tonkeeper.manager.tonconnect.bridge.JsonBuilder
import com.tonapps.tonkeeper.manager.tonconnect.bridge.model.BridgeError
import com.tonapps.tonkeeper.ui.base.BaseWalletVM
import com.tonapps.tonkeeper.worker.DAppPushToggleWorker
import com.tonapps.wallet.data.account.entities.WalletEntity
import com.tonapps.wallet.data.dapps.entities.AppConnectEntity
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.json.JSONObject

class DAppViewModel(
    app: Application,
    private val wallet: WalletEntity,
    private val url: Uri,
    private val tonConnectManager: TonConnectManager
): BaseWalletVM(app) {

    val connectionFlow = tonConnectManager.walletConnectionsFlow(wallet).filterList { connection ->
        connection.appUrl == url && connection.type == AppConnectEntity.Type.Internal
    }.map { it.firstOrNull() }

    fun mute() {
        DAppPushToggleWorker.run(
            context = context,
            wallet = wallet,
            appUrl = url,
            enable = false
        )
    }

    fun disconnect() {
        tonConnectManager.disconnect(wallet, url, AppConnectEntity.Type.Internal)
    }

    suspend fun restoreConnection(): JSONObject {
        val connection = connectionFlow.firstOrNull()
        return if (connection == null) {
            JsonBuilder.connectEventError(BridgeError.UNKNOWN_APP)
        } else {
            JsonBuilder.connectEventSuccess(wallet, null, null, context.appVersionName)
        }
    }
}