package com.tipcalculator

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tipcalculator.data.TipCalculation
import com.tipcalculator.data.TipCalculatorDatabase
import com.tipcalculator.data.TipRepository
import com.tipcalculator.ui.theme.TipCalculatorTheme
import java.text.NumberFormat

class TipCalculatorApplication : Application() {
    val database by lazy { TipCalculatorDatabase.getDatabase(this) }
    val repository by lazy { TipRepository(database.tipCalculationDao()) }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TipCalculatorTheme {
                TipCalculatorApp()
            }
        }
    }
}

@Composable
fun TipCalculatorApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val app = context.applicationContext as TipCalculatorApplication
    val viewModel: TipViewModel = viewModel(factory = TipViewModelFactory(app.repository))

    val calculations by viewModel.allCalculations.collectAsState()

    var amountInput by remember { mutableStateOf("") }
    var tipPercent by remember { mutableStateOf(15f) }

    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val tip = calculateTip(amount, tipPercent.toDouble())
    val total = amount + tip

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tip Calculator")

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Bill Amount") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tip: ${tipPercent.toInt()}%")
                    Slider(
                        value = tipPercent,
                        onValueChange = { tipPercent = it },
                        valueRange = 0f..30f,
                        steps = 5,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Tip: ${formatCurrency(tip)}")
                Text("Total: ${formatCurrency(total)}")

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { viewModel.saveCalculation(amount, tipPercent.toInt(), tip, total) }) {
                        Text("Save")
                    }
                    Button(onClick = {
                        shareCalculation(context, amount, tipPercent.toInt(), tip, total)
                    }) {
                        Text("Share")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("History")
                Button(onClick = { viewModel.clearAll() }) {
                    Text("Clear All")
                }
            }
            CalculationHistory(calculations = calculations, onDelete = { viewModel.deleteCalculation(it) })
        }
    }
}

private fun shareCalculation(context: Context, bill: Double, tipPercent: Int, tip: Double, total: Double) {
    val shareText = "Tip Calculation:\n" +
            "Bill: ${formatCurrency(bill)}\n" +
            "Tip ($tipPercent%): ${formatCurrency(tip)}\n" +
            "Total: ${formatCurrency(total)}"

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(
        Intent.createChooser(intent, "Share Calculation")
    )
}

@Composable
fun CalculationHistory(calculations: List<TipCalculation>, onDelete: (TipCalculation) -> Unit) {
    LazyColumn {
        items(calculations) { calculation ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Bill: ${formatCurrency(calculation.billAmount)}")
                        Text("Tip: ${formatCurrency(calculation.tipAmount)} (${calculation.tipPercent}%)")
                        Text("Total: ${formatCurrency(calculation.totalAmount)}")
                    }
                    IconButton(onClick = { onDelete(calculation) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}

private fun calculateTip(amount: Double, tipPercent: Double = 15.0): Double {
    return amount * tipPercent / 100
}

private fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance().format(amount)
}

@Preview(showBackground = true)
@Composable
fun TipCalculatorAppPreview() {
    TipCalculatorTheme {
        TipCalculatorApp()
    }
}
