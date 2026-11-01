package co.edu.uniquindio.retrofit_lol.domain.error

/**
 Errores de red traducidos a casos que entiende el resto de la app.
 La UI decide qué texto mostrar para cada uno.
 */
sealed class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    // No hay internet o el servidor no respondió (timeout, DNS, etc.).
    class NoConnection(cause: Throwable? = null) : ApiException("Sin conexión", cause)

    // La API respondió 404 (ej. el idioma no existe para ese parche).
    class NotFound : ApiException("Recurso no encontrado")

    // La API respondió con otro código de error HTTP.
    class Server(val code: Int) : ApiException("Error del servidor ($code)")

    // Cualquier otro problema (ej. JSON con un formato inesperado).
    class Unknown(cause: Throwable? = null) : ApiException("Error inesperado", cause)
}
