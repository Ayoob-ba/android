package com.tonkeeper.fragment.wallet.history

import com.tonkeeper.core.history.list.item.HistoryItem
import uikit.mvi.AsyncState
import uikit.mvi.UiState

data class HistoryScreenState(
    val asyncState: AsyncState = AsyncState.Default,
    val items: List<HistoryItem> = emptyList()
): UiState()