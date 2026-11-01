// Bloque de plugins: declara las herramientas y extensiones base del proyecto
plugins {
    // Habilita este módulo como una aplicación de Android.
    alias(libs.plugins.android.application)
    // Activa Jetpack Compose para la interfaz gráfica con Kotlin.
    alias(libs.plugins.kotlin.compose)
    // Serialización de Kotlin: la usa Navigation Compose para las rutas tipadas.
    alias(libs.plugins.kotlinx.serialization)
    // Agrega Hilt para la inyección de dependencias.
    alias(libs.plugins.hilt.android)
    // Agrega KSP, el procesador que genera el código de Hilt.
    alias(libs.plugins.devtools.ksp)
}

// Bloque principal de configuración específica para el ecosistema Android
android {
    // Define el identificador único del paquete base para las clases
    namespace = "co.edu.uniquindio.retrofit_lol"
    // Define la versión del SDK de Android usada para compilar el código
    compileSdk {
        // Actual: 37
        version = release(37)
    }

    // Configuración por defecto aplicable a todas las compilaciones
    defaultConfig {
        // ID único "mundial" de la app
        applicationId = "co.edu.uniquindio.retrofit_lol"
        // Versión mínima de Android compatible (Android 7.0 - Nougat)
        minSdk = 24
        // Versión de Android optimizada y objetivo principal de la app
        targetSdk = 37
        // Número entero interno para control de versiones y actualizaciones
        versionCode = 1
        // Nombre visible de la versión para el usuario final
        versionName = "1.0"
        // Motor para ejecutar pruebas instrumentadas en dispositivos/emuladores
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Define las reglas de empaquetado para desarrollo y producción
    buildTypes {
        // Configuración especifica para la version final del proudcto.
        release {
            // Optimización del código
            optimization {
                // Activa la optimización y reducción de código (Minify/R8)
                enable = true
                // Limita el ámbito de optimización a librerías base
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    // Define la compatibilidad de versiones de Java para el código
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    // Activa características especiales del framework de Android
    buildFeatures {
        compose = true
        // Genera BuildConfig.DEBUG: el log de peticiones solo se activa en debug.
        buildConfig = true
    }
}

dependencies {
    // Base de Compose y Android
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Librerías implementadas:

    // Retrofit: cliente HTTP para consumir la API REST.
    implementation(libs.retrofit)
    // Convierte el JSON de la respuesta en clases de Kotlin (DTOs) usando Gson.
    implementation(libs.converter.gson)
    // Muestra en Logcat cada petición y respuesta (solo en debug).
    implementation(libs.logging.interceptor)

    // Rutas tipadas de Navigation (@Serializable).
    implementation(libs.kotlinx.serialization.json)
    // Sistema de navegación entre pantallas usando Compose.
    implementation(libs.androidx.navigation.compose)
    // Para usar viewModel() y observar estados en Compose.
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    // Iconos extendidos de Material Design.
    implementation(libs.material.icons.extended)

    // Coil para cargar imágenes desde URL (íconos y splash arts de los campeones).
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Hilt: inyección de dependencias.
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Pruebas
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
