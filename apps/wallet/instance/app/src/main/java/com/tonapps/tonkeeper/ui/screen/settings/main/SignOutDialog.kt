package com.tonapps.tonkeeper.ui.screen.settings.main

import android.content.Context
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.AppCompatTextView
import com.tonapps.tonkeeper.extensions.getStringCompat
import com.tonapps.tonkeeper.extensions.getTitle
import com.tonapps.tonkeeper.ui.screen.backup.main.BackupScreen
import com.tonapps.tonkeeperx.R
import com.tonapps.wallet.data.account.Wallet
import com.tonapps.wallet.data.account.entities.WalletEntity
import com.tonapps.wallet.localization.Localization
import uikit.dialog.modal.ModalDialog
import uikit.navigation.Navigation.Companion.navigation
import uikit.widget.CheckBoxView
import uikit.widget.HeaderView

class SignOutDialog(
    context: Context,
    private val wallet: WalletEntity,
): ModalDialog(context, R.layout.dialog_signout) {

    private val confirmationTextView: AppCompatTextView
    private val checkbox: CheckBoxView
    private val logoutButton: Button

    init {
        confirmationTextView = findViewById(R.id.confirmation_text)!!
        logoutButton = findViewById(R.id.logout)!!
        checkbox = findViewById(R.id.checkbox)!!
        checkbox.doOnCheckedChanged = { logoutButton.isEnabled = it }

        findViewById<HeaderView>(R.id.header)?.doOnActionClick = { dismiss() }
        findViewById<View>(R.id.confirmation)?.setOnClickListener { checkbox.toggle() }
        findViewById<View>(R.id.backup)?.setOnClickListener { openBackup() }
    }

    fun show(onClick: () -> Unit) {
        super.show()
        confirmationTextView.text = context.getStringCompat(Localization.logout_confirmation, wallet.label.getTitle(context, confirmationTextView))
        findViewById<View>(R.id.logout)?.setOnClickListener {
            onClick()
            dismiss()
        }
    }

    private fun openBackup() {
        navigation?.add(BackupScreen.newInstance(wallet))
        dismiss()
    }

}