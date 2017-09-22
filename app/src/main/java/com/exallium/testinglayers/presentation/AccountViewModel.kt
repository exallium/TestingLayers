package com.exallium.testinglayers.presentation

import android.databinding.ObservableField
import com.exallium.testinglayers.entities.Account

class AccountViewModel(private val account: Account,
                       private val router: Router): Diffable<AccountViewModel> {

    val title = ObservableField<String>(account.id.value)

    override fun sameAs(other: AccountViewModel): Boolean = account.id == other.account.id
    override fun sameContentsAs(other: AccountViewModel) = account == other.account

    fun click() {
        router.startTransaction(account)
    }
}