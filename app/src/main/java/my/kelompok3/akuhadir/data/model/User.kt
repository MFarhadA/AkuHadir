package my.kelompok3.akuhadir.data.model
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val password: String ="",
    val fullName: String = "",
    val nim: String = "",
    val division: String = "",
    val nama: String = "",
    val divisi: String = "",
    val id_user: Int? = null
)