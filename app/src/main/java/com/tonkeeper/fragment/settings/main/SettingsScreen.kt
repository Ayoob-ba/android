package com.tonkeeper.fragment.settings.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.tonkeeper.R
import com.tonkeeper.dialog.LogoutDialog
import com.tonkeeper.fragment.currency.CurrencyScreen
import com.tonkeeper.fragment.settings.accounts.AccountsScreen
import com.tonkeeper.fragment.settings.legal.LegalFragment
import com.tonkeeper.fragment.settings.list.SettingsAdapter
import com.tonkeeper.fragment.settings.list.item.SettingsIdItem
import com.tonkeeper.fragment.settings.security.SecurityFragment
import uikit.decoration.ListCellDecoration
import uikit.mvi.UiScreen
import uikit.navigation.Navigation.Companion.nav

class SettingsScreen: UiScreen<SettingsScreenState, SettingsScreenEffect, SettingsScreenFeature>(R.layout.fragment_settings) {

    companion object {
        fun newInstance() = SettingsScreen()
    }

    override val feature: SettingsScreenFeature by viewModels()

    private val logoutDialog: LogoutDialog by lazy {
        LogoutDialog(requireContext())
    }

    private lateinit var listView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.list)
        listView.addItemDecoration(ListCellDecoration(view.context))
    }

    override fun newUiState(state: SettingsScreenState) {
        listView.adapter = SettingsAdapter(state.items) { item ->
            if (item is SettingsIdItem) {
                onCellClick(item)
            }
        }
    }

    override fun newUiEffect(effect: SettingsScreenEffect) {
        super.newUiEffect(effect)
        if (effect is SettingsScreenEffect.Logout) {
            nav()?.init(true)
        }
    }

    private fun showLogoutDialog() {
        logoutDialog.show {
            feature.logout()
        }
    }

    private fun onCellClick(item: SettingsIdItem) {
        val nav = nav() ?: return

        when (item.id) {
            SettingsIdItem.MANAGE_WALLETS_ID -> {
                nav.add(AccountsScreen.newInstance())
            }
            SettingsIdItem.LOGOUT_ID -> {
                showLogoutDialog()
            }
            SettingsIdItem.CURRENCY_ID -> {
                nav.add(CurrencyScreen.newInstance())
            }
            SettingsIdItem.LEGAL_ID -> {
                nav.add(LegalFragment.newInstance())
            }
            SettingsIdItem.SECURITY_ID -> {
                nav.add(SecurityFragment.newInstance())
            }
            SettingsIdItem.CONTACT_US_ID -> {
                nav.openURL(feature.supportLink, true)
            }
            SettingsIdItem.TONKEEPER_NEWS_ID -> {
                nav.openURL(feature.tonkeeperNewsUrl, true)
            }
            SettingsIdItem.SUPPORT_ID -> {
                nav.openURL(feature.directSupportUrl, true)
            }
        }
    }
}