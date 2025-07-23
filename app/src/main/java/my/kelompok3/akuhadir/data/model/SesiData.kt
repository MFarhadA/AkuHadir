package my.kelompok3.akuhadir.data.model


import kotlinx.serialization.Serializable

@Serializable
data class SesiData(
    val id_sesi: Int,
    val waktu_masuk: String,
    val divisi: String,
    val jenis_sesi: String,
    val pertemuan: Int,
    val link_meet: String? = null,
    val ruangan: String? = null,
    val nama_materi: String? = null,
    val keterangan: String? = null,
    val id_user_profile: Int? = null
)