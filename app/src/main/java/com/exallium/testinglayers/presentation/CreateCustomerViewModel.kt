package com.exallium.testinglayers.presentation

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.exallium.testinglayers.domain.CreateCustomerUseCase
import com.exallium.testinglayers.domain.CreateCustomerUseCase.Response as R

class CreateCustomerViewModel(private val createCustomerUseCase: CreateCustomerUseCase,
                              private val router: Router) {

    val displayName = ObservableField<String>("")
    val isInProgress = ObservableBoolean()
    val errorMessage = ObservableField<String>("")

    fun createCustomer() {
        createCustomerUseCase.createCustomerWithDisplayName(displayName.get())
                .subscribe(this::onCreateCustomerResponse)
    }

    private fun onCreateCustomerResponse(response: CreateCustomerUseCase.Response) {
        isInProgress.set(false)
        errorMessage.set("")
        when (response) {
            is R.InProgress -> isInProgress.set(true)
            is R.EmptyDisplayName -> errorMessage.set("Must have non-blank name")
            is R.Failure -> errorMessage.set(response.throwable.message?:"An Error Occurred")
            is R.Success -> router.goToAccounts(response.customer)
        }
    }

}