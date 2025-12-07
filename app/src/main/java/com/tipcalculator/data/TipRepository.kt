package com.tipcalculator.data

import kotlinx.coroutines.flow.Flow

class TipRepository(private val tipCalculationDao: TipCalculationDao) {

    fun getAllCalculations(): Flow<List<TipCalculation>> = tipCalculationDao.getAllCalculations()

    suspend fun insert(calculation: TipCalculation) {
        tipCalculationDao.insert(calculation)
    }

    suspend fun delete(calculation: TipCalculation) {
        tipCalculationDao.delete(calculation)
    }

    suspend fun deleteAll() {
        tipCalculationDao.deleteAll()
    }
}
