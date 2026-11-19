package com.pe.qr

import android.Manifest
import android.content.pm.PackageManager

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import android.media.Image
import androidx.camera.core.ImageProxy
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner

import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

import com.pe.presentation.theme.ScannerTop
import com.pe.presentation.theme.ScannerBottom
import com.pe.presentation.theme.ScanLine

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@androidx.annotation.OptIn(
    markerClass = [ExperimentalGetImage::class]
)
@Composable
fun QrScannerScreen(
    onQrDetected: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var tienePermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        tienePermiso = concedido
    }

    LaunchedEffect(Unit) {
        if (!tienePermiso) {
            permisoLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val controller = remember {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
        }
    }

    val executor = remember {
        Executors.newSingleThreadExecutor()
    }

    val scanner = remember {
        val opciones = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        BarcodeScanning.getClient(opciones)
    }

    val detectado = remember { AtomicBoolean(false) }
    val resultadoActual by rememberUpdatedState(onQrDetected)

    var linterna by remember { mutableStateOf(false) }
    var errorCamara by remember { mutableStateOf<String?>(null) }

    DisposableEffect(tienePermiso, lifecycleOwner) {
        if (tienePermiso) {
            controller.setImageAnalysisAnalyzer(
                executor,
                ImageAnalysis.Analyzer { imagenProxy ->

                    if (detectado.get()) {
                        imagenProxy.close()
                    } else {
                        val imagen = obtenerImagen(imagenProxy)

                        if (imagen == null) {
                            imagenProxy.close()
                        } else {
                            val imagenML = InputImage.fromMediaImage(
                                imagen,
                                imagenProxy.imageInfo.rotationDegrees
                            )

                            scanner.process(imagenML)
                                .addOnSuccessListener { codigos ->
                                    val codigo = codigos
                                        .firstOrNull {
                                            !it.rawValue.isNullOrBlank()
                                        }
                                        ?.rawValue
                                        ?.trim()

                                    if (
                                        codigo != null &&
                                        detectado.compareAndSet(false, true)
                                    ) {
                                        resultadoActual(codigo)
                                    }
                                }
                                .addOnCompleteListener {
                                    imagenProxy.close()
                                }
                        }
                    }
                }
            )

            try {
                controller.bindToLifecycle(lifecycleOwner)
            } catch (e: Exception) {
                errorCamara = "No se pudo abrir la cámara"
            }
        }

        onDispose {
            controller.clearImageAnalysisAnalyzer()
            controller.unbind()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            scanner.close()
            executor.shutdown()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(ScannerTop, ScannerBottom)
                )
            )
    ) {
        if (tienePermiso && errorCamara == null) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        implementationMode =
                            PreviewView.ImplementationMode.COMPATIBLE
                        scaleType =
                            PreviewView.ScaleType.FILL_CENTER
                        this.controller = controller
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        ScannerTop.copy(alpha = 0.40f)
                    )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cerrar escáner",
                    tint = Color.White
                )
            }

            Text(
                text = "Escanear equipo",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(
                onClick = {
                    linterna = !linterna
                    controller.enableTorch(linterna)
                },
                enabled = tienePermiso && errorCamara == null
            ) {
                Icon(
                    imageVector = if (linterna)
                        Icons.Default.FlashOn
                    else
                        Icons.Default.FlashOff,
                    contentDescription = "Linterna",
                    tint = Color.White
                )
            }
        }

        if (tienePermiso && errorCamara == null) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(
                    modifier = Modifier.size(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val largo = 28.dp.toPx()
                        val grosor = 4.dp.toPx()
                        val w = size.width
                        val h = size.height

                        val esquinas = listOf(
                            Pair(Offset(0f, largo), Offset(0f, 0f)),
                            Pair(Offset(0f, 0f), Offset(largo, 0f)),
                            Pair(Offset(w - largo, 0f), Offset(w, 0f)),
                            Pair(Offset(w, 0f), Offset(w, largo)),
                            Pair(Offset(0f, h - largo), Offset(0f, h)),
                            Pair(Offset(0f, h), Offset(largo, h)),
                            Pair(Offset(w - largo, h), Offset(w, h)),
                            Pair(Offset(w, h - largo), Offset(w, h))
                        )

                        esquinas.forEach { (inicio, fin) ->
                            drawLine(
                                color = Color.White,
                                start = inicio,
                                end = fin,
                                strokeWidth = grosor
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(225.dp)
                            .height(3.dp)
                            .background(ScanLine)
                    )
                }

                Text(
                    text = "Apunta al código QR del equipo",
                    color = Color.White
                )
            }
        } else {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = errorCamara ?: "Necesitamos permiso para usar la cámara",
                    color = Color.White
                )

                if (!tienePermiso) {
                    Button(
                        onClick = {
                            permisoLauncher.launch(
                                Manifest.permission.CAMERA
                            )
                        }
                    ) {
                        Text("Permitir cámara")
                    }
                }
            }
        }
    }
}


@androidx.annotation.OptIn(
    markerClass = [ExperimentalGetImage::class]
)
private fun obtenerImagen(
    imageProxy: ImageProxy
): Image? {
    return imageProxy.image
}

