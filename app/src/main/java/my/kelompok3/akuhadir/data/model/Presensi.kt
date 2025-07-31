package my.kelompok3.akuhadir.data.model

@kotlinx.serialization.Serializable
data class Presensi(
    val id_presensi: Int,
    val kehadiran: String,
    val id_user_profile: Int,
    val id_sesi: Int,
    val image_path: String? = null
)
