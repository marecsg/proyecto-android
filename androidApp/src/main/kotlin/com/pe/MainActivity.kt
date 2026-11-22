
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
import com.pe.notifications.AndroidLocalNotificationPlatform
import com.pe.qr.QrScannerScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val notificationPlatform =
            AndroidLocalNotificationPlatform(applicationContext)

        setContent {

            var origenEscaneo by remember {
                mutableStateOf<OrigenEscaneoQr?>(null)
            }

            var lecturaQr by remember {
                mutableStateOf<LecturaQr?>(null)
            }

            Box(modifier = Modifier.fillMaxSize()) {

                // ÚNICA llamada a App
                App(
                    notificationPlatform = notificationPlatform,

                    onAbrirEscanerQr = { origen ->
                        lecturaQr = null
                        origenEscaneo = origen
                    },

                    lecturaQr = lecturaQr,

                    onQrConsumido = {
                        lecturaQr = null
                    }
                )

                // Escáner QR
                if (origenEscaneo != null) {
                    QrScannerScreen(
                        onQrDetected = { codigo ->
                            val origen = origenEscaneo

                            if (origen != null) {
                                lecturaQr = LecturaQr(
                                    codigo = codigo,
                                    origen = origen
                                )
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
