package com.tipcalculator.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TipCalculationDao {

    @Insert
    suspend fun insert(calculation: TipCalculation)

    @Query("SELECT * FROM calculations ORDER BY timestamp DESC")
    fun getAllCalculations(): Flow<List<TipCalculation>>

    @Delete
    suspend fun delete(calculation: TipCalculation)

    @Query("DELETE FROM calculations")
    suspend fun deleteAll()
}
