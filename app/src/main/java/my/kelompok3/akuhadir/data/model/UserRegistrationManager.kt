package my.kelompok3.akuhadir.data.manager

import my.kelompok3.akuhadir.data.model.SupabaseInstance
import my.kelompok3.akuhadir.data.model.User
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable

/**
 * Manager untuk mengelola data registrasi user sementara
 */
object UserRegistrationManager {
    private var currentUserId: Int? = null
    private var currentUserEmail: String? = null

    /**
     * Menyimpan data registrasi sementara setelah user berhasil register
     * @param email Email user yang baru saja register
     * @return Boolean true jika berhasil, false jika gagal
     */
    suspend fun saveRegistrationData(email: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val supabase = SupabaseInstance.client

                // Ambil semua data user dan cari yang sesuai dengan email
                val columns = Columns.raw("""
                    id_user, email
                """.trimIndent())

                val users = supabase.from("user")
                    .select(columns = columns)
                    .decodeList<UserIdResponse>()

                // Cari user berdasarkan email
                val matchingUser = users.find { it.email == email }

                if (matchingUser != null) {
                    currentUserId = matchingUser.id_user
                    currentUserEmail = matchingUser.email
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            println("Error saving registration data: ${e.message}")
            false
        }
    }

    /**
     * Mendapatkan ID user yang sedang login/register
     * @return Int? ID user atau null jika tidak ada
     */
    fun getCurrentUserId(): Int? {
        return currentUserId
    }

    /**
     * Mendapatkan email user yang sedang login/register
     * @return String? Email user atau null jika tidak ada
     */
    fun getCurrentUserEmail(): String? {
        return currentUserEmail
    }

    /**
     * Mengecek apakah ada data registrasi yang tersimpan
     * @return Boolean true jika ada data, false jika tidak ada
     */
    fun hasRegistrationData(): Boolean {
        return currentUserId != null && currentUserEmail != null
    }

    /**
     * Menghapus data registrasi sementara
     */
    fun clearRegistrationData() {
        currentUserId = null
        currentUserEmail = null
    }

    /**
     * Menyimpan data registrasi manual (untuk kasus khusus)
     * @param userId ID user
     * @param email Email user
     */
    fun setRegistrationData(userId: Int, email: String) {
        currentUserId = userId
        currentUserEmail = email
    }
}

/**
 * Data class untuk response ID user
 */@Serializable
data class UserIdResponse(
    val id_user: Int,
    val email: String
)