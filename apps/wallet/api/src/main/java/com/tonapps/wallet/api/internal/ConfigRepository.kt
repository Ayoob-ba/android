package com.tonapps.wallet.api.internal

import android.content.Context
import com.tonapps.extensions.file
import com.tonapps.extensions.toByteArray
import com.tonapps.extensions.toParcel
import com.tonapps.wallet.api.entity.ConfigEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class ConfigRepository(
    context: Context,
    scope: CoroutineScope,
    private val internalApi: InternalApi,
) {

    private val configFile = context.cacheDir.file("config")

    var configEntity: ConfigEntity = ConfigEntity.default
        private set

    init {
        scope.launch {
            readCache()?.let {
                configEntity = it
            }
            remote()?.let {
                configEntity = it
            }
        }
    }

    private suspend fun readCache(): ConfigEntity? = withContext(Dispatchers.IO) {
        configFile.readBytes().toParcel()
    }

    private suspend fun remote(): ConfigEntity? = withContext(Dispatchers.IO) {
        val config = internalApi.downloadConfig() ?: return@withContext null
        configFile.writeBytes(config.toByteArray())
        config
    }

}