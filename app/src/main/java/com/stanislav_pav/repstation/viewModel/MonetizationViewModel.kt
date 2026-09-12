package com.stanislav_pav.repstation.viewModel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stanislav_pav.repstation.RepStationApplication
import com.stanislav_pav.repstation.monetization.PlayBillingRepository

class MonetizationViewModel(private val billingRepository: PlayBillingRepository) : ViewModel() {
    val uiState = billingRepository.state

    fun refresh() = billingRepository.refresh()
    fun purchase(activity: Activity) = billingRepository.purchase(activity)
    fun restore() = billingRepository.refresh(restoring = true)
    fun clearMessage() = billingRepository.clearMessage()
    fun unlockWithCode(code: String) = billingRepository.unlockWithCode(code)

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MonetizationViewModel((context.applicationContext as RepStationApplication).billingRepository)
            }
        }
    }
}
