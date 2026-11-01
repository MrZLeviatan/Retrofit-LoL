package co.edu.uniquindio.retrofit_lol.di

import co.edu.uniquindio.retrofit_lol.BuildConfig
import co.edu.uniquindio.retrofit_lol.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 Crea los objetos de red. Usa @Provides (y no @Binds) porque Retrofit y OkHttp se construyen con builders.
 Todos son @Singleton: una sola instancia para toda la app.
 */
// Anotación de Hilt que indica que este objeto es un módulo de Dagger encargado de proveer dependencias
@Module
// Define que todas las dependencias provistas en este módulo vivirán durante todo el ciclo de vida de la app (ámbito global)
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Cliente HTTP: tiempos de espera y log de peticiones.
    // Indica a Hilt que esta función es una fábrica que provee una instancia gestionable
    @Provides
    // Garantiza que solo exista una única instancia de OkHttpClient en toda la memoria de la aplicación
    @Singleton
    // Función que instancia y configura el cliente HTTP encargado de manejar las conexiones de red
    fun provideOkHttpClient(): OkHttpClient {
        // Imprime en Logcat la URL, el código y el tiempo de cada petición.
        // Crea un interceptor para registrar los detalles de las peticiones y respuestas HTTP
        val logging = HttpLoggingInterceptor().apply {
            // Define el nivel de detalle del log: muestra información básica en desarrollo (DEBUG) y nada en producción
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }
        // Inicia el patrón Builder para configurar las reglas y políticas del cliente HTTP OkHttpClient
        return OkHttpClient.Builder()
            // Agrega el interceptor de registro de red previamente configurado
            .addInterceptor(logging)
            // Establece un tiempo límite de 15 segundos para intentar conectar con el servidor antes de dar error
            .connectTimeout(15, TimeUnit.SECONDS)
            // Establece un tiempo límite de 20 segundos para esperar la llegada de datos desde el servidor
            .readTimeout(20, TimeUnit.SECONDS)
            // Construye y devuelve la instancia inmutable final de OkHttpClient
            .build()
    }

    // Retrofit: une la URL base, el cliente HTTP y el convertidor JSON -> Kotlin (Gson).
    @Provides
    // Garantiza que solo exista una única instancia global de Retrofit en toda la app
    @Singleton
    // Función que construye Retrofit recibiendo automáticamente el cliente HTTP inyectado por Hilt
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        // Inicia el patrón Builder para configurar el núcleo de Retrofit
        Retrofit.Builder()
            // Configura la URL base raíz de la cual se desprenderán todos los endpoints de la API
            .baseUrl(ApiService.BASE_URL)
            // Asocia el cliente OkHttpClient configurado para gestionar las peticiones y los tiempos de espera
            .client(client)
            // Añade el conversor de Gson para serializar y deserializar automáticamente JSON a objetos de Kotlin (DTOs)
            .addConverterFactory(GsonConverterFactory.create())
            // Construye y devuelve la instancia final lista para operar de Retrofit
            .build()

    // Retrofit genera la implementación de la interfaz ApiService.
    @Provides
    // Asegura que la instancia del servicio de la API sea única en todo el ciclo de vida de la app
    @Singleton
    // Utiliza la instancia de Retrofit inyectada para generar en tiempo de ejecución la implementación de la interfaz ApiService
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}