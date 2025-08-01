package my.kelompok3.akuhadir.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val nama_materi: String,
    val waktu_masuk: String,
    val divisi: String,
    val jenis_sesi: String,
    val pertemuan: Int,
    val link_meet: String? = null,
    val ruangan: String? = null
)