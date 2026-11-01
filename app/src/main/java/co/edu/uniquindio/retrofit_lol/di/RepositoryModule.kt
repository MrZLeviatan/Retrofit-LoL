package co.edu.uniquindio.retrofit_lol.di

import co.edu.uniquindio.retrofit_lol.data.connectivity.NetworkConnectivityRepository
import co.edu.uniquindio.retrofit_lol.data.repository.remote.CatalogRepositoryImpl
import co.edu.uniquindio.retrofit_lol.data.repository.remote.ChampionRepositoryImpl
import co.edu.uniquindio.retrofit_lol.domain.repository.CatalogRepository
import co.edu.uniquindio.retrofit_lol.domain.repository.ChampionRepository
import co.edu.uniquindio.retrofit_lol.domain.repository.ConnectivityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

/**
 Une cada interfaz del dominio con su implementación.
 Anota la clase abstracta como un módulo de Dagger e indica a Hilt que se instale en el componente Singleton.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // Enlaza la implementación con Retrofit del repositorio de campeones (singleton: conserva su caché).
    @Binds @Singleton abstract fun bindChampion(impl: ChampionRepositoryImpl): ChampionRepository
    // Enlaza la implementación con Retrofit del repositorio de catálogos (versiones e idiomas).
    @Binds @Singleton abstract fun bindCatalog(impl: CatalogRepositoryImpl): CatalogRepository
    // Enlaza el observador de red del sistema con la interfaz ConnectivityRepository.
    @Binds @Singleton abstract fun bindConnectivity(impl: NetworkConnectivityRepository): ConnectivityRepository
}
