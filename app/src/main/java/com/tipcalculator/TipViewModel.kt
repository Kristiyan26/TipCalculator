package com.tipcalculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tipcalculator.data.TipCalculation
import com.tipcalculator.data.TipRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TipViewModel(private val repository: TipRepository) : ViewModel() {

    val allCalculations: StateFlow<List<TipCalculation>> = repository.getAllCalculations()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun saveCalculation(bill: Double, tipPercent: Int, tip: Double, total: Double) {
        viewModelScope.launch {
            val calculation = TipCalculation(
                billAmount = bill,
                tipPercent = tipPercent,
                tipAmount = tip,
                totalAmount = total
            )
            repository.insert(calculation)
        }
    }

    fun deleteCalculation(calculation: TipCalculation) {
        viewModelScope.launch {
            repository.delete(calculation)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}

class TipViewModelFactory(private val repository: TipRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TipViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TipViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
