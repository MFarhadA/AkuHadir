package my.kelompok3.akuhadir.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Sesi(
    val id_sesi: Int,
    val divisi: String,
    val jenis_sesi: String,
    val pertemuan: String,
    val Keterangan: String
)
