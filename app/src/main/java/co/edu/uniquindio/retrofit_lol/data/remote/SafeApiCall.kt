package co.edu.uniquindio.retrofit_lol.data.remote

import co.edu.uniquindio.retrofit_lol.domain.error.ApiException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

/**
 Ejecuta una petición y convierte las excepciones de red en [ApiException].
 Así los repositorios siempre devuelven un Result y la app nunca se cierra por un error de red.
 */
suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        // La corrutina se canceló (ej. el usuario cambió el filtro): no es un error, se propaga.
        throw e
    } catch (e: ApiException) {
        Result.failure(e)
    } catch (e: IOException) {
        // Sin internet, timeout o DNS.
        Result.failure(ApiException.NoConnection(e))
    } catch (e: HttpException) {
        // La API respondió, pero con un código de error.
        Result.failure(if (e.code() == 404) ApiException.NotFound() else ApiException.Server(e.code()))
    } catch (e: Exception) {
        Result.failure(ApiException.Unknown(e))
    }
