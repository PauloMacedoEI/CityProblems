package estg.ipvc.cityproblems.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

class User (
    val id: Int,
    val username: String,
    val password: String
)

@Parcelize
class Anomalia(
        val id: Int,
        val titulo: String,
        val descricao: String,
        val tipo: String,
        val foto: String,
        val latitude: Double,
        val longitude: Double,
        val user_id: Int
) : Parcelable