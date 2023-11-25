package com.tonkeeper.dialog.fiat.list

import android.view.ViewGroup
import com.tonkeeper.core.fiat.models.FiatItem
import uikit.list.BaseListHolder
import uikit.list.BaseListItem
import uikit.list.DiffListAdapter
import uikit.list.ListCell

class MethodAdapter(
    items: List<MethodItem>,
    private val onClick: (item: MethodItem) -> Unit
): DiffListAdapter(items) {

    companion object {
        fun buildMethodItems(list: List<FiatItem>): List<MethodItem> {
            val items = mutableListOf<MethodItem>()
            for ((index, item) in list.withIndex()) {
                val position = ListCell.getPosition(list.size, index)
                items.add(MethodItem(item, position))
            }
            return items
        }
    }

    override fun createHolder(parent: ViewGroup, viewType: Int): BaseListHolder<out BaseListItem> {
        return MethodHolder(parent, onClick)
    }
}