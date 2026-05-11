package com.stanislav_pav.repstation.viewModel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stanislav_pav.repstation.monetization.MonetizationState
import com.stanislav_pav.repstation.monetization.RevenueCatBillingRepository
import com.revenuecat.purchases.Package as RevenueCatPackage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MonetizationViewModel(
    private val billingRepository: RevenueCatBillingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        MonetizationState(isRevenueCatConfigured = billingRepository.isConfigured)
    )
    val uiState: StateFlow<MonetizationState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    message = null,
                    isRevenueCatConfigured = billingRepository.isConfigured
                )
            }

            runCatching {
                val customerInfo = billingRepository.getCustomerInfo()
                val packages = billingRepository.getPackages()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isPro = billingRepository.isPro(customerInfo),
                        packages = packages,
                        isRevenueCatConfigured = billingRepository.isConfigured
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        message = billingRepository.errorMessage(error)
                    )
                }
            }
        }
    }

    fun purchase(activity: Activity, packageToPurchase: RevenueCatPackage) {
        viewModelScope.launch {
            _uiState.update { it.copy(isPurchasing = true, message = null) }

            runCatching {
                billingRepository.purchase(activity, packageToPurchase)
            }.onSuccess { customerInfo ->
                _uiState.update {
                    it.copy(
                        isPurchasing = false,
                        isPro = billingRepository.isPro(customerInfo),
                        message = if (billingRepository.isPro(customerInfo)) {
                            "Pro unlocked"
                        } else {
                            "Purchase completed, but Pro is not active yet"
                        }
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isPurchasing = false,
                        message = billingRepository.errorMessage(error)
                    )
                }
            }
        }
    }

    fun restore() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRestoring = true, message = null) }

            runCatching {
                billingRepository.restorePurchases()
            }.onSuccess { customerInfo ->
                val isPro = billingRepository.isPro(customerInfo)
                _uiState.update {
                    it.copy(
                        isRestoring = false,
                        isPro = isPro,
                        message = if (isPro) "Purchase restored" else "No Pro purchase found"
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isRestoring = false,
                        message = billingRepository.errorMessage(error)
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun reportError(message: String?) {
        _uiState.update { it.copy(message = message ?: "Something went wrong") }
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MonetizationViewModel(RevenueCatBillingRepository())
            }
        }
    }
}
