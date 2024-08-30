package com.tonapps.tonkeeper.ui.screen.collectibles

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tonapps.tonkeeper.ui.base.UiListState
import com.tonapps.tonkeeper.ui.screen.collectibles.list.Adapter
import com.tonapps.tonkeeper.ui.screen.collectibles.list.Item
import com.tonapps.tonkeeper.ui.screen.main.MainScreen
import com.tonapps.tonkeeper.ui.screen.qr.QRScreen
import com.tonapps.tonkeeperx.R
import com.tonapps.uikit.color.backgroundTransparentColor
import com.tonapps.wallet.api.entity.TokenEntity
import com.tonapps.wallet.localization.Localization
import org.koin.androidx.viewmodel.ext.android.viewModel
import uikit.drawable.BarDrawable
import uikit.extensions.collectFlow
import uikit.widget.EmptyLayout
import uikit.widget.HeaderView

class CollectiblesScreen: MainScreen.Child(R.layout.fragment_main_list) {

    override val viewModel: CollectiblesViewModel by viewModel()

    private val adapter = Adapter()

    private lateinit var headerView: HeaderView
    private lateinit var listView: RecyclerView
    private lateinit var emptyView: EmptyLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headerView = view.findViewById(R.id.header)
        headerView.title = getString(Localization.collectibles)
        headerView.setColor(requireContext().backgroundTransparentColor)

        listView = view.findViewById(R.id.list)
        listView.updatePadding(top = 0)
        listView.layoutManager = object : GridLayoutManager(context, 3) {
            override fun supportsPredictiveItemAnimations(): Boolean = false
        }
        listView.adapter = adapter

        emptyView = view.findViewById(R.id.empty)
        emptyView.doOnButtonClick = { first ->
            if (!first) {
                openQRCode()
            }
        }

        collectFlow(viewModel.uiListStateFlow) { state ->
            if (state is UiListState.Loading) {
                adapter.applySkeleton()
                headerView.setSubtitle(Localization.updating)
            } else if (state is UiListState.Empty) {
                setEmptyState()
            } else if (state is UiListState.Items) {
                setListState()
                adapter.submitList(state.items)
                if (!state.cache) {
                    headerView.setSubtitle(null)
                }
            }
        }
    }

    private fun openQRCode() {
        collectFlow(viewModel.openQRCode()) { walletEntity ->
            navigation?.add(QRScreen.newInstance(walletEntity.address, TokenEntity.TON, walletEntity.type))
        }
    }

    private fun setEmptyState() {
        if (emptyView.visibility == View.VISIBLE) {
            return
        }
        emptyView.visibility = View.VISIBLE
        listView.visibility = View.GONE
    }

    private fun setListState() {
        if (listView.visibility == View.VISIBLE) {
            return
        }
        emptyView.visibility = View.GONE
        listView.visibility = View.VISIBLE
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
        fun newInstance() = CollectiblesScreen()
    }
}