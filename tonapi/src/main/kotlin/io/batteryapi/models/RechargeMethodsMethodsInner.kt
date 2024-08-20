/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package io.batteryapi.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 
 *
 * @param type 
 * @param rate 
 * @param symbol 
 * @param decimals 
 * @param supportGasless 
 * @param supportRecharge 
 * @param image 
 * @param jettonMaster 
 * @param minBootstrapValue 
 */


data class RechargeMethodsMethodsInner (

    @Json(name = "type")
    val type: RechargeMethodsMethodsInner.Type,

    @Json(name = "rate")
    val rate: kotlin.String,

    @Json(name = "symbol")
    val symbol: kotlin.String,

    @Json(name = "decimals")
    val decimals: kotlin.Int,

    @Json(name = "support_gasless")
    val supportGasless: kotlin.Boolean,

    @Json(name = "support_recharge")
    val supportRecharge: kotlin.Boolean,

    @Json(name = "image")
    val image: kotlin.String? = null,

    @Json(name = "jetton_master")
    val jettonMaster: kotlin.String? = null,

    @Json(name = "min_bootstrap_value")
    val minBootstrapValue: kotlin.String? = null

) {

    /**
     * 
     *
     * Values: jetton,ton
     */
    @JsonClass(generateAdapter = false)
    enum class Type(val value: kotlin.String) {
        @Json(name = "jetton") jetton("jetton"),
        @Json(name = "ton") ton("ton");
    }

}

