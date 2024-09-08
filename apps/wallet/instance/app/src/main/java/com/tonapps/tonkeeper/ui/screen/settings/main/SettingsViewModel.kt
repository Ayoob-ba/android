package com.tonapps.tonkeeper.ui.screen.settings.main

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tonapps.blockchain.ton.contract.BaseWalletContract
import com.tonapps.blockchain.ton.contract.WalletVersion
import com.tonapps.blockchain.ton.extensions.toAccountId
import com.tonapps.tonkeeper.core.AnalyticsHelper
import com.tonapps.tonkeeper.extensions.capitalized
import com.tonapps.tonkeeper.ui.base.BaseWalletVM
import com.tonapps.tonkeeper.ui.screen.settings.main.list.Item
import com.tonapps.uikit.list.ListCell
import com.tonapps.wallet.api.API
import com.tonapps.wallet.data.account.entities.WalletEntity
import com.tonapps.wallet.data.account.AccountRepository
import com.tonapps.wallet.data.account.Wallet
import com.tonapps.wallet.data.backup.BackupRepository
import com.tonapps.wallet.data.core.SearchEngine
import com.tonapps.wallet.data.core.WalletCurrency
import com.tonapps.wallet.data.push.PushManager
import com.tonapps.wallet.data.settings.SettingsRepository
import com.tonapps.wallet.data.tonconnect.TonConnectRepository
import com.tonapps.wallet.localization.Language
import com.tonapps.wallet.localization.Localization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uikit.extensions.getString

class SettingsViewModel(
    application: Application,
    private val wallet: WalletEntity,
    private val accountRepository: AccountRepository,
    private val settingsRepository: SettingsRepository,
    private val api: API,
    private val backupRepository: BackupRepository,
    private val pushManager: PushManager,
    private val tonConnectRepository: TonConnectRepository,
): BaseWalletVM(application) {

    private val _uiItemsFlow = MutableStateFlow<List<Item>>(emptyList())
    val uiItemsFlow = _uiItemsFlow.asStateFlow().filter { it.isNotEmpty() }

    init {
        combine(
            accountRepository.selectedWalletFlow,
            settingsRepository.currencyFlow,
            settingsRepository.languageFlow,
            settingsRepository.searchEngineFlow,
            backupRepository.stream,
        ) { newWallet, currency, language, searchEngine, backups ->
            val hasBackup = backups.indexOfFirst { it.walletId == newWallet.id } > -1
            buildUiItems(newWallet, currency, language, searchEngine, hasBackup)
        }.launchIn(viewModelScope)
    }

    fun setSearchEngine(searchEngine: SearchEngine?) {
        settingsRepository.searchEngine = searchEngine ?: SearchEngine.GOOGLE
    }

    fun signOut(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            AnalyticsHelper.trackEvent("delete_wallet")
            settingsRepository.setPushWallet(wallet.id, false)
            tonConnectRepository.deleteApps(wallet, settingsRepository.firebaseToken)
            accountRepository.delete(wallet.id)
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    private suspend fun hasW5(wallet: WalletEntity): Boolean {
        if (wallet.version == WalletVersion.V5R1) {
            return true
        } else if (wallet.type == Wallet.Type.Watch || wallet.type == Wallet.Type.Lockup || wallet.type == Wallet.Type.Ledger) {
            return true
        }
        val w5Contact = BaseWalletContract.create(wallet.publicKey, "v5r1", wallet.testnet)
        val accountId = w5Contact.address.toAccountId()
        return accountRepository.getWalletByAccountId(accountId, wallet.testnet) != null
    }

    private suspend fun buildUiItems(
        newWallet: WalletEntity,
        currency: WalletCurrency,
        language: Language,
        searchEngine: SearchEngine,
        hasBackup: Boolean
    ) {
        val hasW5 = hasW5(newWallet)
        val uiItems = mutableListOf<Item>()
        uiItems.add(Item.Account(newWallet))
        uiItems.add(Item.Space)

        uiItems.add(Item.Tester(ListCell.Position.SINGLE, "https://t.me/tonkeeper_android"))

        uiItems.add(Item.Space)
        if (!newWallet.isExternal && !newWallet.isWatchOnly) {
            uiItems.add(Item.Backup(ListCell.Position.FIRST, hasBackup))
            uiItems.add(Item.Security(ListCell.Position.LAST))
        } else {
            uiItems.add(Item.Security(ListCell.Position.SINGLE))
        }

        uiItems.add(Item.Space)
        uiItems.add(Item.Notifications(ListCell.Position.FIRST))
        if (!newWallet.testnet) {
            uiItems.add(Item.Currency(currency.code, ListCell.Position.MIDDLE))
            uiItems.add(Item.SearchEngine(searchEngine, ListCell.Position.MIDDLE))
        }
        uiItems.add(Item.Language(language.nameLocalized.ifEmpty {
            getString(Localization.system)
        }.capitalized, ListCell.Position.MIDDLE))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            uiItems.add(Item.Widget(ListCell.Position.MIDDLE))
        }
        if (!hasW5) {
            uiItems.add(Item.W5(ListCell.Position.MIDDLE))
        }
        if (!newWallet.isExternal && !api.config.batteryDisabled) {
            uiItems.add(Item.Battery(ListCell.Position.MIDDLE))
        }
        uiItems.add(Item.Theme(ListCell.Position.LAST))

        uiItems.add(Item.Space)
        uiItems.add(Item.FAQ(ListCell.Position.FIRST, api.config.faqUrl))
        uiItems.add(Item.Support(ListCell.Position.MIDDLE, api.config.directSupportUrl))
        uiItems.add(Item.News(ListCell.Position.MIDDLE, api.config.tonkeeperNewsUrl))
        uiItems.add(Item.Contact(ListCell.Position.MIDDLE, api.config.supportLink))
        uiItems.add(Item.Rate(ListCell.Position.MIDDLE))
        uiItems.add(Item.Legal(ListCell.Position.LAST))

        uiItems.add(Item.Space)
        if (newWallet.type == Wallet.Type.Watch) {
            uiItems.add(Item.DeleteWatchAccount(ListCell.Position.SINGLE))
        } else {
            uiItems.add(Item.Logout(ListCell.Position.SINGLE, newWallet.label, !newWallet.hasPrivateKey))
        }
        uiItems.add(Item.Space)
        uiItems.add(Item.Logo)

        _uiItemsFlow.value = uiItems
    }
}