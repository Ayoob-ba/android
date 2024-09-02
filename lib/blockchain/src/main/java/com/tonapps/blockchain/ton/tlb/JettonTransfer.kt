package com.tonapps.blockchain.ton.tlb

import com.tonapps.blockchain.ton.TONOpCode
import com.tonapps.blockchain.ton.extensions.storeOpCode
import org.ton.block.Coins
import org.ton.block.MsgAddressInt
import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.cell.invoke
import org.ton.tlb.TlbCodec
import org.ton.tlb.TlbConstructor
import org.ton.tlb.TlbObject
import org.ton.tlb.TlbPrettyPrinter
import org.ton.tlb.loadTlb
import org.ton.tlb.providers.TlbConstructorProvider
import org.ton.tlb.storeTlb

data class JettonTransfer(
    val queryId: Long,
    val coins: Coins,
    val toAddress: MsgAddressInt,
    val responseAddress: MsgAddressInt,
    val forwardAmount: Coins,
    val comment: String?
) : TlbObject {

    override fun print(printer: TlbPrettyPrinter): TlbPrettyPrinter = printer.type("JettonTransfer") {
        field("queryId", queryId)
        field("coins", coins)
        field("toAddress", toAddress)
        field("responseAddress", responseAddress)
        field("forwardAmount", forwardAmount)
        field("comment", comment)
    }

    companion object : TlbConstructorProvider<JettonTransfer> by JettonTransferTlbConstructor {

        @JvmStatic
        fun tlbCodec(): TlbCodec<JettonTransfer> = JettonTransferTlbConstructor
    }
}

private object JettonTransferTlbConstructor : TlbConstructor<JettonTransfer>(
    schema = "", id = null
) {

    override fun storeTlb(
        cellBuilder: CellBuilder, value: JettonTransfer
    ) = cellBuilder {
        storeOpCode(TONOpCode.JETTON_TRANSFER)
        storeUInt(value.queryId, 64)
        storeTlb(Coins, value.coins)
        storeTlb(MsgAddressInt, value.toAddress)
        storeTlb(MsgAddressInt, value.responseAddress)
        storeBit(false)
        storeTlb(Coins, value.forwardAmount)
        if (value.comment.isNullOrEmpty()) {
            storeBit(false)
        } else {
            storeBit(true)
            storeTlb(StringTlbConstructor, value.comment)
        }
    }

    override fun loadTlb(
        cellSlice: CellSlice
    ): JettonTransfer = cellSlice {
        loadUInt32()
        val queryId = loadUInt64().toLong()
        val coins = loadTlb(Coins)
        val toAddress = loadTlb(MsgAddressInt)
        val responseAddress = loadTlb(MsgAddressInt)
        loadBit()
        val forwardAmount = loadTlb(Coins)
        val comment = if (loadBit()) loadTlb(StringTlbConstructor) else null
        JettonTransfer(queryId, coins, toAddress, responseAddress, forwardAmount, comment)
    }
}
