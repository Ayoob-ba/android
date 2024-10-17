package com.tonapps.tonkeeper.ui.screen.send.contacts.main.list.holder

import android.view.ViewGroup
import com.tonapps.tonkeeper.popup.ActionSheet
import com.tonapps.tonkeeper.ui.screen.send.contacts.edit.EditContactScreen
import com.tonapps.tonkeeper.ui.screen.send.contacts.main.list.Item
import com.tonapps.tonkeeperx.R
import com.tonapps.uikit.icon.UIKitIcon
import com.tonapps.wallet.localization.Localization
import uikit.extensions.drawable
import uikit.navigation.Navigation.Companion.navigation

class SavedHolder(
    parent: ViewGroup,
    private val onClick: (Item) -> Unit,
    private val onAction: (item: Item, actionId: Long) -> Unit
): ContactHolder<Item.SavedContact>(parent) {

    init {
        emojiView.setEmoji("\uD83D\uDCBE")
        iconView.setImageResource(R.drawable.ic_ellipsis_16)
    }

    override fun onBind(item: Item.SavedContact) {
        itemView.setOnClickListener { onClick(item) }
        itemView.background = item.position.drawable(context)

        nameView.text = item.name
        iconView.setOnClickListener { showMenu(item) }
    }

    private fun showMenu(item: Item.SavedContact) {
        val actionSheet = ActionSheet(context)
        actionSheet.addItem(EDIT_ID, Localization.edit, UIKitIcon.ic_pencil_16)
        actionSheet.addItem(DELETE_ID, Localization.delete, UIKitIcon.ic_trash_bin_16)
        actionSheet.doOnItemClick = { i ->
            onAction(item, i.id)
        }
        actionSheet.show(iconView)
    }

}