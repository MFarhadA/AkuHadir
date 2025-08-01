package my.kelompok3.akuhadir.data.model

// Mengimpor pustaka yang diperlukan
import org.mindrot.jbcrypt.BCrypt // Untuk hashing password
import java.util.Locale.filter // Mengimpor fungsi filter dari Locale
import my.kelompok3.akuhadir.data.model.SupabaseInstance // Mengimpor instance Supabase
import my.kelompok3.akuhadir.data.model.User // Mengimpor model User
import io.github.jan.supabase.postgrest.from // Mengimpor fungsi untuk mengambil data dari Supabase
import io.github.jan.supabase.postgrest.query.Columns // Mengimpor query kolom
import kotlinx.coroutines.Dispatchers // Mengimpor dispatcher untuk coroutine
import kotlinx.coroutines.withContext // Mengimpor fungsi untuk menjalankan kode dalam konteks tertentu
import kotlinx.serialization.Serializable // Mengimpor anotasi Serializable untuk serialisasi objek

@Serializable // Menandai kelas ini dapat diserialisasi
class AuthManager {
    // Fungsi untuk menangani login pengguna
    suspend fun login(email: String, password: String): Pair<Boolean, String?> {
        // Mengambil instance Supabase untuk berinteraksi dengan database
        val supabase = SupabaseInstance.client

        // Mengambil data pengguna dari tabel "user" berdasarkan email
        val userResponse = supabase.from("user").select {
            filter {
                eq("email", email) // Mencari pengguna dengan email yang sesuai
            }
        }.decodeSingle<User>() // Mengambil satu pengguna berdasarkan email

        // Memeriksa apakah pengguna ditemukan
        return if (userResponse != null) {
            // Mengambil hash password dari pengguna yang ditemukan
            val hashedPassword = userResponse.password

            // Membandingkan password yang dimasukkan dengan hash yang disimpan
            if (BCrypt.checkpw(password, hashedPassword)) {
                // Jika password cocok, ambil ID pengguna dan konversi ke String
                val currentUserId = userResponse.id_user.toString() // Konversi ID pengguna ke String
                Pair(true, currentUserId) // Mengembalikan true dan ID pengguna
            } else {
                Pair(false, null) // Password tidak cocok
            }
        } else {
            Pair(false, null) // Pengguna tidak ditemukan
        }
    }
}
