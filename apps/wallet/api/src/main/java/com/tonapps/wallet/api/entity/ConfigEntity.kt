package com.tonapps.wallet.api.entity

import android.net.Uri
import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
data class ConfigEntity(
    val supportLink: String,
    val nftExplorer: String,
    val transactionExplorer: String,
    val mercuryoSecret: String,
    val tonapiMainnetHost: String,
    val tonapiTestnetHost: String,
    val stonfiUrl: String,
    val tonNFTsMarketplaceEndpoint: String,
    val directSupportUrl: String,
    val tonkeeperNewsUrl: String,
    val tonCommunityUrl: String,
    val tonCommunityChatUrl: String,
    val tonApiV2Key: String,
    val featuredPlayInterval: Int,
    val flags: FlagsEntity,
    val faqUrl: String,
    val aptabaseEndpoint: String,
    val aptabaseAppKey: String,
    val scamEndpoint: String,
    val batteryHost: String,
    val batteryTestnetHost: String,
    val batteryBeta: Boolean,
    val batteryDisabled: Boolean,
    val batterySendDisabled: Boolean,
    val batteryMeanFees: String,
    val batteryMeanPriceNft: String,
    val batteryMeanPriceSwap: String,
    val batteryMeanPriceJetton: String,
    val disableBatteryCryptoRecharge: Boolean,
): Parcelable {

    val swapUri: Uri
        get() = Uri.parse(stonfiUrl)

    val isBatteryDisabled: Boolean
        get() = batteryDisabled || batterySendDisabled

    constructor(json: JSONObject, debug: Boolean) : this(
        supportLink = json.getString("supportLink"),
        nftExplorer = json.getString("NFTOnExplorerUrl"),
        transactionExplorer = json.getString("transactionExplorer"),
        mercuryoSecret = json.getString("mercuryoSecret"),
        tonapiMainnetHost = json.getString("tonapiMainnetHost"),
        tonapiTestnetHost = json.getString("tonapiTestnetHost"),
        stonfiUrl = json.getString("stonfiUrl"),
        tonNFTsMarketplaceEndpoint = json.getString("tonNFTsMarketplaceEndpoint"),
        directSupportUrl = json.getString("directSupportUrl"),
        tonkeeperNewsUrl = json.getString("tonkeeperNewsUrl"),
        tonCommunityUrl = json.getString("tonCommunityUrl"),
        tonCommunityChatUrl = json.getString("tonCommunityChatUrl"),
        tonApiV2Key = json.getString("tonApiV2Key"),
        featuredPlayInterval = json.optInt("featured_play_interval", 3000),
        flags = if (debug) {
            FlagsEntity()
        } else {
            FlagsEntity(json.getJSONObject("flags"))
        },
        faqUrl = json.getString("faq_url"),
        aptabaseEndpoint = json.getString("aptabaseEndpoint"),
        aptabaseAppKey = json.getString("aptabaseAppKey"),
        scamEndpoint = json.optString("scamEndpoint", "https://scam.tonkeeper.com"),
        batteryHost = json.optString("batteryHost", "https://battery.tonkeeper.com"),
        batteryTestnetHost = json.optString("batteryTestnetHost", "https://testnet-battery.tonkeeper.com"),
        batteryBeta = json.optBoolean("battery_beta", true),
        batteryDisabled = json.optBoolean("disable_battery", false),
        batterySendDisabled = json.optBoolean("disable_battery_send", false),
        batteryMeanFees = json.optString("batteryMeanFees", "0.0055"),
        disableBatteryCryptoRecharge = json.optBoolean("disable_battery_crypto_recharge_module", false),
        batteryMeanPriceNft = json.optString("batteryMeanPrice_nft", "0.03"),
        batteryMeanPriceSwap = json.optString("batteryMeanPrice_swap", "0.22"),
        batteryMeanPriceJetton = json.optString("batteryMeanPrice_jetton", "0.06"),
    )

    constructor() : this(
        supportLink = "mailto:support@tonkeeper.com",
        nftExplorer = "https://tonviewer.com/nft/%s",
        transactionExplorer = "https://tonviewer.com/transaction/%s",
        mercuryoSecret = "",
        tonapiMainnetHost = "https://keeper.tonapi.io",
        tonapiTestnetHost = "https://testnet.tonapi.io",
        stonfiUrl = "https://tonkeeper.ston.fi/swap",
        tonNFTsMarketplaceEndpoint = "https://ton.diamonds",
        directSupportUrl = "https://t.me/tonkeeper_supportbot",
        tonkeeperNewsUrl = "https://t.me/tonkeeper_new",
        tonCommunityUrl = "https://t.me/toncoin",
        tonCommunityChatUrl = "https://t.me/toncoin_chat",
        tonApiV2Key = "",
        featuredPlayInterval = 3000,
        flags = FlagsEntity(),
        faqUrl = "https://tonkeeper.helpscoutdocs.com/",
        aptabaseEndpoint = "https://anonymous-analytics.tonkeeper.com",
        aptabaseAppKey = "A-SH-4314447490",
        scamEndpoint = "https://scam.tonkeeper.com",
        batteryHost = "https://battery.tonkeeper.com",
        batteryTestnetHost = "https://testnet-battery.tonkeeper.com",
        batteryBeta = true,
        batteryDisabled = false,
        batterySendDisabled = false,
        batteryMeanFees = "0.0055",
        disableBatteryCryptoRecharge = false,
        batteryMeanPriceNft = "0.03",
        batteryMeanPriceSwap = "0.22",
        batteryMeanPriceJetton = "0.06",
    )

    companion object {
        val default = ConfigEntity()
    }
}