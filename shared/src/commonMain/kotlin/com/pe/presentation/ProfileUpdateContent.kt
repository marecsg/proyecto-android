package com.pe.presentation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileUpdateContent(
    onBack: () -> Unit,
    onSave: (ByteArray?) -> Unit
){
    var name by remember { mutableStateOf("Toñito") }
    var lastname by remember { mutableStateOf("Kun") }
    var phone by remember { mutableStateOf("999999999") }
}