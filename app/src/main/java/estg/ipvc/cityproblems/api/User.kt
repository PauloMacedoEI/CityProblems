package estg.ipvc.cityproblems.api

class User (
    val id: Int,
    val username: String,
    val password: String
)

class Anomalia(
        val id: Int,
        val titulo: String,
        val descricao: String,
        val tipo: String,
        val foto: String,
        val latitude: Double,
        val longitude: Double,
        val user_id: Int
)