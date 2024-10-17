package com.tonapps.tonkeeper.ui.screen.send.contacts.main.list.holder

import android.view.ViewGroup
import com.tonapps.tonkeeper.popup.ActionSheet
import com.tonapps.tonkeeper.ui.screen.send.contacts.main.list.Item
import com.tonapps.tonkeeperx.R
import com.tonapps.uikit.icon.UIKitIcon
import com.tonapps.wallet.localization.Localization
import uikit.extensions.drawable

class LatestHolder(
    parent: ViewGroup,
    private val onClick: (Item) -> Unit,
    private val onAction: (item: Item, actionId: Long) -> Unit
): ContactHolder<Item.LatestContact>(parent) {

    init {
        emojiView.setEmoji("\uD83D\uDD57")
        iconView.setImageResource(R.drawable.ic_ellipsis_16)
    }

    override fun onBind(item: Item.LatestContact) {
        itemView.setOnClickListener { onClick(item) }
        itemView.background = item.position.drawable(context)

        nameView.text = item.name
        iconView.setOnClickListener { showMenu(item) }
    }

    private fun showMenu(item: Item.LatestContact) {
        val actionSheet = ActionSheet(context)
        actionSheet.addItem(ADD_TO_CONTACTS_ID, Localization.add_to_contact, UIKitIcon.ic_plus_alternate_16)
        actionSheet.addItem(HIDE_ID, Localization.hide, UIKitIcon.ic_block_16)
        actionSheet.doOnItemClick = { i ->
            onAction(item, i.id)
        }
        actionSheet.show(iconView)
    }
}