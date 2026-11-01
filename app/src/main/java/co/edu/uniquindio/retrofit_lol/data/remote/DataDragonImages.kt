package co.edu.uniquindio.retrofit_lol.data.remote

/**
 Arma las URLs de las imágenes. La API no las devuelve completas: solo el nombre del archivo.
 */
object DataDragonImages {

    private const val BASE = ApiService.BASE_URL + "cdn/"

    // Ícono cuadrado del campeón (ej. .../16.21.1/img/champion/Ahri.png).
    fun championIcon(version: String, fileName: String) = "$BASE$version/img/champion/$fileName"

    // Imagen grande (splash art) de una skin; num = 0 es la skin por defecto.
    fun splash(championId: String, skinNum: Int) = "${BASE}img/champion/splash/${championId}_$skinNum.jpg"

    // Ícono de una habilidad Q, W, E o R.
    fun spellIcon(version: String, fileName: String) = "$BASE$version/img/spell/$fileName"

    // Ícono de la pasiva.
    fun passiveIcon(version: String, fileName: String) = "$BASE$version/img/passive/$fileName"
}
