package com.tonapps.wallet.data.battery.entity

import android.os.Parcelable
import com.tonapps.icu.Coins
import io.batteryapi.models.RechargeMethodsMethodsInner
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.RoundingMode

@Parcelize
data class RechargeMethodEntity(
    val type: RechargeMethodType,
    val rate: String,
    val symbol: String,
    val decimals: Int,
    val supportGasless: Boolean,
    val supportRecharge: Boolean,
    val image: String? = null,
    val jettonMaster: String? = null,
    val minBootstrapValue: String? = null
) : Parcelable {

    private companion object {

        private fun RechargeMethodsMethodsInner.Type.toRechargeMethodType(): RechargeMethodType {
            return when (this) {
                RechargeMethodsMethodsInner.Type.jetton -> RechargeMethodType.JETTON
                RechargeMethodsMethodsInner.Type.ton -> RechargeMethodType.TON
            }
        }
    }

    constructor(method: RechargeMethodsMethodsInner) : this(
        type = method.type.toRechargeMethodType(),
        rate = method.rate,
        symbol = method.symbol,
        decimals = method.decimals,
        supportGasless = method.supportGasless,
        supportRecharge = method.supportRecharge,
        image = method.image,
        jettonMaster = method.jettonMaster,
        minBootstrapValue = method.minBootstrapValue
    )


    fun fromTon(amount: BigDecimal): Coins {
        return Coins.of(amount.divide(rate.toBigDecimal(), decimals, RoundingMode.HALF_UP), decimals)
    }

    fun fromTon(amount: String) = fromTon(amount.toBigDecimal())
}