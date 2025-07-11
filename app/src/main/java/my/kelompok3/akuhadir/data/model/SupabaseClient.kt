package my.kelompok3.akuhadir.data.model

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.realtime.Realtime
import my.kelompok3.akuhadir.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log

import io.github.jan.supabase.postgrest.postgrest

object SupabaseInstance {

    val client by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Auth)
            install(Realtime)
        }
    }

    // Fungsi untuk mengecek koneksi Supabase
    suspend fun testConnection(): Boolean {
        return try {
            Log.d("SupabaseTest", "Memulai test koneksi...")
            println("SupabaseTest: Memulai test koneksi...")

            // Uji koneksi dengan mengambil data dari tabel
            val response = client.postgrest["user"].select {
                limit(1)
            }

            Log.d("SupabaseTest", "Koneksi berhasil!")
            Log.d("SupabaseTest", "Response: $response")
            println("SupabaseTest: Koneksi berhasil!")
            println("SupabaseTest: Response: $response")

            true
        } catch (e: Exception) {
            Log.e("SupabaseTest", "Koneksi gagal: ${e.message}")
            Log.e("SupabaseTest", "Error detail: ", e)
            println("SupabaseTest: Koneksi gagal: ${e.message}")
            println("SupabaseTest: Error detail: ${e.printStackTrace()}")

            false
        }
    }

    // Fungsi untuk test koneksi async (non-blocking)
    fun testConnectionAsync() {
        CoroutineScope(Dispatchers.IO).launch {
            testConnection()
        }
    }
}

class SupabaseClient {
    val client = SupabaseInstance.client

    init {
        // Test koneksi saat inisialisasi
        SupabaseInstance.testConnectionAsync()
    }
}