package com.tonapps.wallet.data.staking

import io.tonapi.models.PoolImplementationType

object StakingPool {

    enum class Implementation(
        val title: String
    ) {
        Whales("whales"), TF("tf"), LiquidTF("liquidTF")
    }

    fun implementation(type: PoolImplementationType): Implementation {
        return when (type) {
            PoolImplementationType.whales -> Implementation.Whales
            PoolImplementationType.tf -> Implementation.TF
            PoolImplementationType.liquidTF -> Implementation.LiquidTF
        }
    }

    fun getTitle(implementation: Implementation): Int {
        return when (implementation) {
            Implementation.Whales -> R.string.stake_whales
            Implementation.TF -> R.string.stake_nominators
            Implementation.LiquidTF -> R.string.stake_tonstakers
        }
    }

    fun getIcon(implementation: Implementation): Int {
        return when (implementation) {
            Implementation.Whales -> R.drawable.whales
            Implementation.TF -> R.drawable.tf
            Implementation.LiquidTF -> R.drawable.ic_tonstakers
        }
    }

}