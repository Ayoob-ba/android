package com.tonapps.tonkeeper.ui.screen.events

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tonapps.tonkeeper.core.history.list.HistoryAdapter
import com.tonapps.tonkeeper.core.history.list.HistoryItemDecoration
import com.tonapps.tonkeeper.core.history.list.item.HistoryItem
import com.tonapps.tonkeeper.koin.walletViewModel
import com.tonapps.tonkeeper.ui.screen.main.MainScreen
import com.tonapps.tonkeeper.ui.screen.purchase.main.PurchaseScreen
import com.tonapps.tonkeeper.ui.screen.qr.QRScreen
import com.tonapps.tonkeeperx.R
import com.tonapps.uikit.color.backgroundTransparentColor
import com.tonapps.uikit.list.ListPaginationListener
import com.tonapps.wallet.api.entity.TokenEntity
import com.tonapps.wallet.data.account.entities.WalletEntity
import com.tonapps.wallet.localization.Localization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import uikit.drawable.BarDrawable
import uikit.extensions.collectFlow
import uikit.navigation.Navigation.Companion.navigation
import uikit.widget.EmptyLayout
import uikit.widget.HeaderView

class EventsScreen(wallet: WalletEntity) : MainScreen.Child(R.layout.fragment_main_events_list, wallet) {

    override val viewModel: EventsViewModel by walletViewModel()

    private val legacyAdapter = HistoryAdapter()
    private val paginationListener = object : ListPaginationListener() {
        override fun onLoadMore() {
            viewModel.loadMore()
        }
    }

    private lateinit var headerView: HeaderView
    private lateinit var containerView: View
    private lateinit var listView: RecyclerView
    private lateinit var filtersView: RecyclerView
    private lateinit var emptyView: EmptyLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headerView = view.findViewById(R.id.header)
        headerView.title = getString(Localization.history)
        headerView.setSubtitle(Localization.updating)
        headerView.setColor(requireContext().backgroundTransparentColor)

        containerView = view.findViewById(R.id.container)
        filtersView = view.findViewById(R.id.filters)

        listView = view.findViewById(R.id.list)
        listView.adapter = legacyAdapter
        listView.addOnScrollListener(paginationListener)
        listView.addItemDecoration(HistoryItemDecoration())

        emptyView = view.findViewById(R.id.empty)
        emptyView.doOnButtonClick = { first ->
            if (first) {
                navigation?.add(PurchaseScreen.newInstance(screenContext.wallet))
            } else {
                openQRCode()
            }
        }

        collectFlow(viewModel.uiStateFlow, ::applyState)
    }

    private suspend fun applyState(state: EventsUiState) = withContext(Dispatchers.Main) {
        if (state.isEmpty) {
            setEmptyState()
        } else {
            setListState()
            if (state.isLoading) {
                headerView.setSubtitle(Localization.updating)
            }
            legacyAdapter.submitList(state.items) {
                if (!state.isLoading) {
                    headerView.setSubtitle(null)
                }
            }
        }
    }

    override fun scrollUp() {
        super.scrollUp()
        listView.scrollToPosition(0)
        viewModel.refresh()
    }

    private fun openQRCode() {
        navigation?.add(QRScreen.newInstance(screenContext.wallet, TokenEntity.TON))
    }

    private fun setEmptyState() {
        if (emptyView.visibility == View.VISIBLE) {
            return
        }
        headerView.setSubtitle(null)
        emptyView.visibility = View.VISIBLE
        containerView.visibility = View.GONE
    }

    private fun setListState() {
        if (containerView.visibility == View.VISIBLE) {
            return
        }
        emptyView.visibility = View.GONE
        containerView.visibility = View.VISIBLE
    }

    override fun getRecyclerView(): RecyclerView? {
        if (this::listView.isInitialized) {
            return listView
        }
        return null
    }

    override fun getHeaderDividerOwner(): BarDrawable.BarDrawableOwner? {
        if (this::headerView.isInitialized) {
            return headerView
        }
        return null
    }

    companion object {
        fun newInstance(wallet: WalletEntity) = EventsScreen(wallet)
    }
}