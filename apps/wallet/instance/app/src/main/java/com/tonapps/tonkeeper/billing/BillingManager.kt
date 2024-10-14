package com.tonapps.tonkeeper.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.consumePurchase
import com.tonapps.extensions.MutableEffectFlow
import com.tonapps.extensions.filterList
import com.tonapps.wallet.data.account.entities.WalletEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration.Companion.seconds

class BillingManager(
    context: Context,
    scope: CoroutineScope,
) : PurchasesUpdatedListener {

    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    private val _productsFlow = MutableStateFlow<List<ProductDetails>?>(null)
    val productsFlow = _productsFlow.asStateFlow().filterNotNull().filter {
        it.isNotEmpty()
    }

    private val _purchasesUpdatedFlow = MutableEffectFlow<PurchasesUpdated>()
    val purchasesUpdatedFlow = _purchasesUpdatedFlow.shareIn(scope, SharingStarted.Lazily, 0).distinctUntilChanged()

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        if (result.isSuccess && !purchases.isNullOrEmpty()) {
            _purchasesUpdatedFlow.tryEmit(PurchasesUpdated(result, purchases))
        }
    }

    suspend fun consumeProduct(purchaseToken: String) = billingClient.ready { client ->
        val params = ConsumeParams.newBuilder().setPurchaseToken(purchaseToken).build()
        client.consumePurchase(params)
    }

    suspend fun getProducts(
        productIds: List<String>,
        productType: String = ProductType.INAPP
    ) = billingClient.ready { client ->
        if (productIds.isEmpty()) {
            return@ready
        }

        val productList = productIds.map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(productType)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        client.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _productsFlow.value = productDetailsList
            } else {
                _productsFlow.value = emptyList()  // In case of an error
            }
        }
    }

    fun setEmptyProducts() {
        _productsFlow.value = emptyList()
    }

    private suspend fun getPendingPurchases(client: BillingClient): List<Purchase> {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(ProductType.INAPP)

        return queryPurchases(client, params.build()).filter { !it.isAcknowledged }
    }

    private suspend fun queryPurchases(client: BillingClient, params: QueryPurchasesParams): List<Purchase> = suspendCancellableCoroutine { continuation ->
        client.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                continuation.resume(purchasesList)
            } else {
                continuation.resumeWithException(BillingException(billingResult))
            }
        }
    }

    suspend fun requestPurchase(
        activity: Activity,
        wallet: WalletEntity,
        productDetails: ProductDetails
    ) = billingClient.ready { client ->
        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setObfuscatedAccountId(wallet.accountId)
            .setObfuscatedProfileId(wallet.accountId)
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        client.launchBillingFlow(activity, billingFlowParams)
        Unit
    }

    @OptIn(FlowPreview::class)
    fun productFlow(productId: String) = productsFlow.take(1).filterList { product ->
        product.productId == productId
    }.mapNotNull { it.firstOrNull() }.timeout(5.seconds)

    suspend fun restorePurchases(): List<Purchase> = billingClient.ready {
        getPendingPurchases(it)
    }
}