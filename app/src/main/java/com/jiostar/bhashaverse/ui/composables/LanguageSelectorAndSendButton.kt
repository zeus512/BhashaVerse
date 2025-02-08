package com.jiostar.bhashaverse.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectorAndSendButton(
    onLanguageSelected: (String) -> Unit,
    onSendClicked: () -> Unit
) {
    val indianLanguages = mapOf(
        "Assamese" to "as",
        "Bengali" to "bn",
        "Gujarati" to "gu",
        "Hindi" to "hi",
        "Kannada" to "kn",
        "Konkani" to "kok",
        "Malayalam" to "ml",
        "Marathi" to "mr",
        "Nepali" to "ne",
        "Odia" to "or",
        "Punjabi" to "pa",
        "Sanskrit" to "sa",
        "Sindhi" to "sd",
        "Tamil" to "ta",
        "Telugu" to "te",
        "Urdu" to "ur"
    )

    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("Hindi") }
    var selectedLanguageCode by remember { mutableStateOf("hi") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedLanguage,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                indianLanguages.forEach { (language, code) ->
                    DropdownMenuItem(
                        text = { Text(text = language) },
                        onClick = {
                            selectedLanguage = language
                            selectedLanguageCode = code
                            onLanguageSelected(code)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.padding(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                onSendClicked()
            }) {
                Text(text = "Send")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageSelectorAndSendButtonPreview() {
    LanguageSelectorAndSendButton(
        onLanguageSelected = { languageCode ->
            println("Selected language code: $languageCode")
        },
        onSendClicked = {
            println("Send button clicked")
        }
    )
}