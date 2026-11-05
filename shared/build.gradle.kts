import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Plugins necesarios para Multiplatform, Android, Compose y el Compilador de Compose
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    // 1. CONFIGURACIÓN DEL TARGET ANDROID
    // Esto le dice a Kotlin que genere código compatible con la JVM de Android
    androidTarget {
        compilerOptions {
            // Establecemos Java 11 como objetivo (estándar actual para Android)
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // 2. CONFIGURACIÓN DE TARGETS iOS
    // Definimos soporte para iPhones reales (Arm64) y simuladores (SimulatorArm64)
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared" // Nombre de la librería que usará Xcode
            isStatic = true     // Recomendado para Compose Multiplatform
        }
    }

    // 3. FUENTES DE CÓDIGO Y DEPENDENCIAS (SourceSets)
    sourceSets {
        // commonMain: Aquí va el 95% del código (Clean Architecture, Spring Boot REST, UI)
        // Este código se comparte entre Android e iOS
        commonMain.dependencies {
            // Dependencias de UI (Compose Multiplatform)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")

            // Dependencias de ciclo de vida y ViewModels compartidos
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            //librería de iconos
            implementation(compose.materialIconsExtended)
            //dependencia para serealizar los json
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)


        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.ktor.client.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.ios)
        }

        // commonTest: Para pruebas unitarias compartidas
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.pe.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    // Sintaxis alternativa si la anterior sigue en rojo:
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_11)
        targetCompatibility(JavaVersion.VERSION_11)
    }
}

// 5. DEPENDENCIAS DE HERRAMIENTAS
dependencies {
    // Permite ver la vista previa de Compose en el IDE
    debugImplementation(libs.compose.uiTooling)
}