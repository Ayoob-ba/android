package com.tonapps.wallet.data.token.entities

import com.tonapps.blockchain.Coins
import com.tonapps.wallet.data.core.WalletCurrency

data class TokenRateEntity(
    val currency: WalletCurrency,
    val fiat: Coins,
    val rate: Coins,
    val rateDiff24h: String
)