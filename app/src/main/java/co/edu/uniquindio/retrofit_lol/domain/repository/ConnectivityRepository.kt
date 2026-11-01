package co.edu.uniquindio.retrofit_lol.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 Contrato para conocer el estado de la conexión del dispositivo.
 */
interface ConnectivityRepository {

    // Emite true cuando hay internet y false cuando se pierde.
    val isOnline: Flow<Boolean>
}
