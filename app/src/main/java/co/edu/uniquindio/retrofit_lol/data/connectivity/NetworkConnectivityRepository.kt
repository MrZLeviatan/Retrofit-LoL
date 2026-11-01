package co.edu.uniquindio.retrofit_lol.data.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import co.edu.uniquindio.retrofit_lol.domain.repository.ConnectivityRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 Observa la red del sistema con ConnectivityManager y la expone como un Flow<Boolean>.
 */
class NetworkConnectivityRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : ConnectivityRepository {

    override val isOnline: Flow<Boolean> = callbackFlow {
        val manager = context.getSystemService(ConnectivityManager::class.java)

        // Callback que Android llama cada vez que cambia la red por defecto.
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }

            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                trySend(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
            }
        }

        // Estado inicial, antes de que llegue el primer evento.
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        trySend(capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true)

        manager.registerDefaultNetworkCallback(callback)
        // Cuando nadie observa el Flow, se quita el callback (evita fugas de memoria).
        awaitClose { manager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}
