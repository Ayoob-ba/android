package com.tonapps.tonkeeper.fragment.send.amount

import com.tonapps.blockchain.Coins
import com.tonapps.wallet.data.core.WalletCurrency
import com.tonapps.wallet.data.account.legacy.WalletLegacy
import com.tonapps.wallet.data.token.entities.AccountTokenEntity
import uikit.mvi.UiState

data class AmountScreenState(
    val wallet: WalletLegacy? = null,
    val amount: Coins = Coins.ZERO,
    val currency: WalletCurrency = WalletCurrency.TON,
    val available: CharSequence = "",
    val rate: CharSequence = "0",
    val insufficientBalance: Boolean = false,
    val remaining: CharSequence = "",
    val canContinue: Boolean = false,
    val maxActive: Boolean = false,
    val tokens: List<AccountTokenEntity> = emptyList(),
    val selectedTokenAddress: String = WalletCurrency.TON.code,
    val fiat: Boolean = false
): UiState() {

    val selectedToken: AccountTokenEntity?
        get() = tokens.firstOrNull { it.address == selectedTokenAddress }

    val decimals: Int
        get() = selectedToken?.decimals ?: 9

    val selectedTokenCode: String
        get() = selectedToken?.symbol ?: "TON"

    val selectedCurrencyCode: String
        get() = if (fiat) currency.code else selectedTokenCode
}