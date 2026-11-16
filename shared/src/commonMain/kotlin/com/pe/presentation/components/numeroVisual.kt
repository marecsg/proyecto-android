
package com.pe.presentation.components

fun numeroVisual(numero: Int): String {
    return numero.toString().padStart(4, '0')
}
