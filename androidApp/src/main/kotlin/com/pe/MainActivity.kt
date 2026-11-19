
package com.pe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import com.pe.qr.QrScannerScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {

            // Indica desde dónde se abrió la cámara
            var origenEscaneo by remember {
                mutableStateOf<OrigenEscaneoQr?>(null)
            }

            // Guarda temporalmente el resultado del QR
            var lecturaQr by remember {
                mutableStateOf<LecturaQr?>(null)
            }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                // Aplicación principal de LabManager
                App(
                    onAbrirEscanerQr = { origen ->
                        lecturaQr = null
                        origenEscaneo = origen
                    },

                    lecturaQr = lecturaQr,

                    onQrConsumido = {
                        lecturaQr = null
                    }
                )

                // La cámara aparece encima de la aplicación
                if (origenEscaneo != null) {

                    QrScannerScreen(
                        onQrDetected = { codigo ->

                            val origen = origenEscaneo

                            if (origen != null) {

                                // Enviar código y origen a App.kt
                                lecturaQr = LecturaQr(
                                    codigo = codigo,
                                    origen = origen
                                )

                                // Cerrar la cámara
                                origenEscaneo = null
                            }
                        },

                        onClose = {
                            origenEscaneo = null
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
