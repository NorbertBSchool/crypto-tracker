package com.example.cryptotracker.ui.portfolio

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHoldingSheet(
    tokenSymbol: String,
    tokenName: String,
    currentPriceUsd: String,
    onDismiss: () -> Unit,
    onConfirm: (buyPrice: Double, quantity: Double) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var buyPrice by remember { mutableStateOf(currentPriceUsd) }
    var quantity by remember { mutableStateOf("") }
    val buyPriceDouble = buyPrice.toDoubleOrNull() ?: 0.0
    val quantityDouble = quantity.toDoubleOrNull() ?: 0.0
    val isValid = buyPriceDouble > 0 && quantityDouble > 0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Add $tokenSymbol to Portfolio",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = tokenName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = buyPrice,
                onValueChange = { buyPrice = it },
                label = { Text("Buy Price (USD)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (buyPriceDouble > 0 && quantityDouble > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Total cost: $${String.format("%.2f", buyPriceDouble * quantityDouble)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onConfirm(buyPriceDouble, quantityDouble) },
                modifier = Modifier.fillMaxWidth(),
                enabled = isValid
            ) {
                Text("Add to Portfolio")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
