package co.edu.uniquindio.retrofit_lol.di

import co.edu.uniquindio.retrofit_lol.domain.repository.CatalogRepository
import co.edu.uniquindio.retrofit_lol.domain.repository.ChampionRepository
import co.edu.uniquindio.retrofit_lol.domain.repository.ConnectivityRepository
import co.edu.uniquindio.retrofit_lol.domain.usecase.catalog.GetLanguagesUseCase
import co.edu.uniquindio.retrofit_lol.domain.usecase.catalog.GetVersionsUseCase
import co.edu.uniquindio.retrofit_lol.domain.usecase.champion.GetChampionDetailUseCase
import co.edu.uniquindio.retrofit_lol.domain.usecase.champion.GetChampionsPageUseCase
import co.edu.uniquindio.retrofit_lol.domain.usecase.connectivity.ObserveConnectivityUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 Crea los casos de uso. Los casos de uso son clases simples de Kotlin (sin anotaciones de Hilt),
 así que se construyen aquí. Viven lo mismo que el ViewModel que los pide.
 */
@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides @ViewModelScoped
    fun provideGetChampionsPage(repository: ChampionRepository) = GetChampionsPageUseCase(repository)

    @Provides @ViewModelScoped
    fun provideGetChampionDetail(repository: ChampionRepository) = GetChampionDetailUseCase(repository)

    @Provides @ViewModelScoped
    fun provideGetVersions(repository: CatalogRepository) = GetVersionsUseCase(repository)

    @Provides @ViewModelScoped
    fun provideGetLanguages(repository: CatalogRepository) = GetLanguagesUseCase(repository)

    @Provides @ViewModelScoped
    fun provideObserveConnectivity(repository: ConnectivityRepository) = ObserveConnectivityUseCase(repository)
}
