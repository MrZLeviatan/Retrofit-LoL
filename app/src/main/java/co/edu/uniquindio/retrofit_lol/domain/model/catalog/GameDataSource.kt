package co.edu.uniquindio.retrofit_lol.domain.model.catalog

/**
 Versión (parche) e idioma con los que se consultan los datos de la API.
 Cambiar cualquiera de los dos cambia el endpoint que se llama.
 */
data class GameDataSource(
    // Parche del juego (ej. "16.21.1"), sale del endpoint de versiones.
    val version: String,
    // Código de idioma (ej. "es_MX"), sale del endpoint de idiomas.
    val language: String,
) {
    companion object {
        const val DEFAULT_LANGUAGE = "es_MX"
    }
}
