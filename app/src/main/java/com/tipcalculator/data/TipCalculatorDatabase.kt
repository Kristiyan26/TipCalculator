package com.tipcalculator.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TipCalculation::class], version = 1, exportSchema = false)
abstract class TipCalculatorDatabase : RoomDatabase() {

    abstract fun tipCalculationDao(): TipCalculationDao

    companion object {
        @Volatile
        private var INSTANCE: TipCalculatorDatabase? = null

        fun getDatabase(context: Context): TipCalculatorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TipCalculatorDatabase::class.java,
                    "tip_calculator_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
