package com.tonkeeper.core.currency

import com.tonkeeper.App
import com.tonkeeper.core.Coin
import ton.SupportedCurrency
import ton.SupportedTokens

fun from(
    fromCurrency: String,
    accountId: String
): CurrencyConverter {
    return CurrencyConverter(fromCurrency, accountId)
}

fun from(
    fromCurrency: SupportedTokens,
    accountId: String
): CurrencyConverter {
    return from(fromCurrency.code, accountId)
}

class CurrencyConverter(
    private val fromCurrency: String,
    private val accountId: String,
) {

    private var value = 0f

    fun value(value: Float) = apply {
        this.value = value
    }

    fun value(value: Long) = apply {
        value(Coin.toCoins(value))
    }

    suspend fun to(to: SupportedCurrency = App.settings.currency): Float {
        return to(to.code)
    }

    suspend fun to(to: String): Float {
        if (fromCurrency == to) {
            return value
        }
        val rates = CurrencyManager.getInstance().get(accountId)
        val token = rates[fromCurrency] ?: return 0f
        return token.to(to, value)
    }
}