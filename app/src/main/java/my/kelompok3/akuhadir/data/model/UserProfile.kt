package my.kelompok3.akuhadir.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id_user_profile: Int,
    val nama: String,
    val nim: String,
    val divisi: String,
    val role: String,
    val id_user: Int
)

// RoleData.kt - Data class khusus untuk role operations
@Serializable
data class RoleData(
    val id_user_profile: Int,
    val nama: String,
    val nim: String,
    val role: String,
    val id_user: Int
)

// RoleType.kt - Enum untuk role types berdasarkan database
enum class RoleType(val value: String) {
    ANGGOTA("anggota"),
    SEKRETARIS("sekretaris"),
    PENGURUS("pengurus");

    companion object {
        fun fromString(value: String): RoleType? {
            return values().find { it.value.equals(value, ignoreCase = true) }
        }
    }
}