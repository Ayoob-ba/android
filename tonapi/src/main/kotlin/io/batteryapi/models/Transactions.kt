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

import io.batteryapi.models.TransactionsTransactionsInner

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 
 *
 * @param totalTransactions 
 * @param transactions 
 * @param nextOffset if set, then there are more transactions to be loaded. Use this value as offset parameter in the next request.
 */


data class Transactions (

    @Json(name = "total_transactions")
    val totalTransactions: kotlin.Int,

    @Json(name = "transactions")
    val transactions: kotlin.collections.List<TransactionsTransactionsInner>,

    /* if set, then there are more transactions to be loaded. Use this value as offset parameter in the next request. */
    @Json(name = "next_offset")
    val nextOffset: kotlin.Int? = null

) {


}

