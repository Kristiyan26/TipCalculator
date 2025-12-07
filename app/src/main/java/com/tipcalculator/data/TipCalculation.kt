package com.tipcalculator.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculations")
data class TipCalculation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val billAmount: Double,
    val tipPercent: Int,
    val tipAmount: Double,
    val totalAmount: Double,
    val timestamp: Long = System.currentTimeMillis()
)
