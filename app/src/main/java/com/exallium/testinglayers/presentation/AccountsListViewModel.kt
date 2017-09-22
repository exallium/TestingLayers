package com.exallium.testinglayers.presentation

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.exallium.testinglayers.domain.AccountRepository
import com.exallium.testinglayers.domain.CreateAccountUseCase
import com.exallium.testinglayers.domain.CreateAccountUseCase.Response as R
import com.exallium.testinglayers.domain.CustomerRepository
import com.exallium.testinglayers.entities.Customer
import io.reactivex.Observable

class AccountsListViewModel(private val customer: Customer,
                            customerRepository: CustomerRepository,
                            private val accountRepository: AccountRepository,
                            private val createAccountUseCase: CreateAccountUseCase,
                            private val router: Router) {

    val errorMessage = ObservableField<String>("")
    val isAddEnabled = ObservableBoolean(false)

    var accounts: Observable<List<AccountViewModel>> = customerRepository
            .getCustomerById(customer.id)
            .doOnSuccess { isAddEnabled.set(true) }
            .doOnComplete { errorMessage.set("Customer Does Not Exist") }
            .flatMapObservable { accountRepository.getAccountsForCustomer(it) }
            .map { it.map { AccountViewModel(it, router) } }

    fun addAccount() {
        createAccountUseCase.createAccountForCustomer(customer).subscribe(this::handleResponse)
    }

    private fun handleResponse(response: CreateAccountUseCase.Response) {
        errorMessage.set("")
        isAddEnabled.set(true)
        when (response) {
            is R.CustomerDoesNotExistInDatabase -> {
                errorMessage.set("Customer Not Found")
                isAddEnabled.set(false)
            }
            is R.InProgress -> isAddEnabled.set(false)
            is R.Failure -> errorMessage.set("Something bad happend.  Try again later.")
        }
    }
}