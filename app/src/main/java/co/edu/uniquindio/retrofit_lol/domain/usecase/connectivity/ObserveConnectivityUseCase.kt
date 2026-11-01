package co.edu.uniquindio.retrofit_lol.domain.usecase.connectivity

import co.edu.uniquindio.retrofit_lol.domain.repository.ConnectivityRepository
import kotlinx.coroutines.flow.Flow

/**
 Caso de uso que observa si el dispositivo está online u offline.
 */
class ObserveConnectivityUseCase(

    // Inyecta el repositorio de conectividad.
    private val connectivityRepository: ConnectivityRepository
) {
    // Sobrescribe el operador 'invoke' para permitir ejecutar la clase como si fuera una función,
    // devolviendo un Flow reactivo booleano (true si hay conexión, false si no hay internet)
    operator fun invoke(): Flow<Boolean> = connectivityRepository.isOnline
}