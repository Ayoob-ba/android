package com.tonapps.tonkeeper.ui.screen.staking.stake

import com.tonapps.icu.Coins
import com.tonapps.wallet.data.staking.entities.PoolEntity
import com.tonapps.wallet.data.staking.entities.PoolInfoEntity
import io.tonapi.models.PoolInfo

sealed class StakingEvent {
    data object OpenOptions: StakingEvent()
    data object OpenAmount: StakingEvent()
    data class OpenDetails(
        val pool: PoolInfoEntity
    ): StakingEvent()
    data class OpenConfirm(
        val pool: PoolEntity,
        val amount: Coins
    ): StakingEvent()
    data object Finish: StakingEvent()
}