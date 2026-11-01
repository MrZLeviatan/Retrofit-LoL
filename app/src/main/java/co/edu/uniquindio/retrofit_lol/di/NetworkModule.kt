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
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Cliente HTTP: tiempos de espera y log de peticiones.
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        // Imprime en Logcat la URL, el código y el tiempo de cada petición.
        // Solo en debug: en una versión publicada expondría datos y haría la app más lenta.
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit: une la URL base, el cliente HTTP y el convertidor JSON -> Kotlin (Gson).
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // Retrofit genera la implementación de la interfaz ApiService.
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}
