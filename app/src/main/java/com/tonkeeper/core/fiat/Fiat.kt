package com.tonkeeper.core.fiat

import android.app.Application
import com.tonkeeper.App
import com.tonkeeper.core.fiat.models.FiatData
import com.tonkeeper.core.fiat.models.FiatItem
import com.tonkeeper.api.internal.repositories.FiatMethodsRepository
import com.tonkeeper.api.internal.repositories.KeysRepository
import core.extensions.toHex
import core.keyvalue.KeyValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ton.crypto.digest.sha512
import ton.SupportedCurrency
import java.util.UUID

class Fiat(
    app: Application
) {

    private companion object {
        private const val SHOW_CONFIRMATION = "show_confirmation"
    }

    private val fiatMethodsRepository = FiatMethodsRepository(app)
    private val keysRepository = KeysRepository(app)
    private val keyValue = KeyValue(app,"fiat")

    suspend fun replaceUrl(
        url: String,
        address: String,
        currency: SupportedCurrency
    ): String {
        var replacedUrl = url.replace("{ADDRESS}", address)
        replacedUrl = replacedUrl.replace("{CUR_FROM}", currency.code)
        replacedUrl = replacedUrl.replace("{CUR_TO}", "TON")

        if (replacedUrl.contains("TX_ID")) {
            val mercuryoSecret = keysRepository.getValue("mercuryoSecret") ?: ""
            val signature = sha512((address+mercuryoSecret).toByteArray()).toHex()
            val tx = "mercuryo_" + UUID.randomUUID().toString()
            replacedUrl = replacedUrl.replace("{TX_ID}", tx)
            replacedUrl = replacedUrl.replace("=TON&", "=TONCOIN&")
            replacedUrl += "&signature=$signature"
        }
        return replacedUrl
    }

    suspend fun isShowConfirmation(
        id: String
    ): Boolean {
        val key = showConfirmationKey(id)
        return keyValue.getBoolean(key, true)
    }

    suspend fun disableShowConfirmation(
        id: String
    ) {
        val key = showConfirmationKey(id)
        keyValue.putBoolean(key, false)
    }

    private fun showConfirmationKey(id: String): String {
        return "$SHOW_CONFIRMATION-$id"
    }

    suspend fun init(
        countryCode: String
    ) = withContext(Dispatchers.IO) {
        fiatMethodsRepository.get(countryCode)
    }

    suspend fun getData(
        countryCode: String
    ): FiatData = withContext(Dispatchers.IO) {
        fiatMethodsRepository.get(countryCode)
    }

    suspend fun getMethods(
        countryCode: String
    ): List<FiatItem> {
        val data = getData(countryCode)
        val layout = data.layoutByCountry(countryCode)
        return data.getBuyItemsByMethods(layout.methods)
    }
}