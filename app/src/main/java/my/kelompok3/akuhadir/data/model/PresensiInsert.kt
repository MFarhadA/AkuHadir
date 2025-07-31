package my.kelompok3.akuhadir.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PresensiInsert(
    val id_user_profile: Int,
    val kehadiran: String,
    val image_path: String,
    val id_sesi: Int
)
