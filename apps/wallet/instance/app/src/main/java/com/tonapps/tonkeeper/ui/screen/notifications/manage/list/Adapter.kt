package com.tonapps.tonkeeper.ui.screen.notifications.manage.list

import android.net.Uri
import android.view.ViewGroup
import com.tonapps.tonkeeper.ui.screen.notifications.manage.list.holder.AppHolder
import com.tonapps.tonkeeper.ui.screen.notifications.manage.list.holder.AppsHeaderHolder
import com.tonapps.tonkeeper.ui.screen.notifications.manage.list.holder.SpaceHolder
import com.tonapps.tonkeeper.ui.screen.notifications.manage.list.holder.WalletPushHolder
import com.tonapps.uikit.list.BaseListAdapter
import com.tonapps.uikit.list.BaseListHolder
import com.tonapps.uikit.list.BaseListItem

class Adapter(
    private val onToggleCallback: (Uri, Boolean) -> Unit
): BaseListAdapter() {
    override fun createHolder(parent: ViewGroup, viewType: Int): BaseListHolder<out BaseListItem> {
        return when(viewType) {
            Item.TYPE_WALLET -> WalletPushHolder(parent)
            Item.TYPE_SPACE -> SpaceHolder(parent)
            Item.TYPE_APPS_HEADER -> AppsHeaderHolder(parent)
            Item.TYPE_APP -> AppHolder(parent, onToggleCallback)
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

}