// SessionSekretarisCard.kt
package my.kelompok3.akuhadir.ui.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import my.kelompok3.akuhadir.data.model.SupabaseInstance
import my.kelompok3.akuhadir.data.manager.UserRegistrationManager
import my.kelompok3.akuhadir.ui.theme.*
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable
import androidx.compose.runtime.*
import androidx.navigation.NavController
import my.kelompok3.akuhadir.ui.components.SessionOwnerCard
import my.kelompok3.akuhadir.ui.components.SesiData

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

// Tambahkan parameter untuk navigation dan state management
@Composable
fun SessionSekretarisCard(
    onNavigateToAddSession: () -> Unit,
    onEditSession: (SesiData) -> Unit = {},
    onViewParticipants: (SesiData) -> Unit = {},
    refreshTrigger: Boolean = false // Tambahkan parameter refresh
) {
    val supabase = SupabaseInstance.client
    val currentUserId = UserRegistrationManager.getCurrentUserId()
    val coroutineScope = rememberCoroutineScope()

    var sesiList by remember { mutableStateOf<List<SesiData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var showConfirmDialog by remember { mutableStateOf<SesiData?>(null) }

    // Fetch sesi data - tambahkan refreshTrigger sebagai key
    LaunchedEffect(refreshTrigger) {
        isLoading = true
        error = null

        try {
            val result = try {
                supabase.from("sesi")
                    .select()
                    .decodeList<SesiData>()
                    .filter { it.keterangan != "selesai" }
            } catch (e: Exception) {
                Log.w("SessionSekretarisCard", "Error filtering by keterangan: ${e.message}")
                supabase.from("sesi")
                    .select()
                    .decodeList<SesiData>()
                    .filter { sesi ->
                        sesi.keterangan?.lowercase() != "selesai"
                    }
            }

            sesiList = result.sortedByDescending { it.waktu_masuk }
            Log.d("SessionSekretarisCard", "Loaded ${sesiList.size} active sessions")

        } catch (e: Exception) {
            error = "Error loading sessions: ${e.message}"
            Log.e("SessionSekretarisCard", "Error loading sessions", e)
        } finally {
            isLoading = false
        }
    }

    // Function to close session
    fun closeSession(sesi: SesiData) {
        coroutineScope.launch {
            try {
                // Method 1: Try updating with correct column name
                supabase.from("sesi")
                    .update(
                        mapOf("keterangan" to "selesai")
                    ) {
                        filter {
                            eq("id_sesi", sesi.id_sesi)
                        }
                    }

                // Remove from local list immediately for better UX
                sesiList = sesiList.filter { it.id_sesi != sesi.id_sesi }
                Log.d("SessionSekretarisCard", "Session ${sesi.id_sesi} closed successfully")

            } catch (e: Exception) {
                Log.e("SessionSekretarisCard", "Error closing session with 'keterangan': ${e.message}")

                // Method 2: Try alternative approach if first method fails
                try {
                    // Try with different column name that might exist
                    supabase.from("sesi")
                        .update(
                            mapOf("status" to "selesai")
                        ) {
                            filter {
                                eq("id_sesi", sesi.id_sesi)
                            }
                        }

                    sesiList = sesiList.filter { it.id_sesi != sesi.id_sesi }
                    Log.d("SessionSekretarisCard", "Session ${sesi.id_sesi} closed with alternative method")

                } catch (e2: Exception) {
                    Log.e("SessionSekretarisCard", "Error with alternative method: ${e2.message}")

                    // Method 3: If both fail, try to delete the record
                    try {
                        supabase.from("sesi")
                            .delete {
                                filter {
                                    eq("id_sesi", sesi.id_sesi)
                                }
                            }

                        sesiList = sesiList.filter { it.id_sesi != sesi.id_sesi }
                        Log.d("SessionSekretarisCard", "Session ${sesi.id_sesi} deleted successfully")

                    } catch (e3: Exception) {
                        // If all methods fail, just remove from local list
                        sesiList = sesiList.filter { it.id_sesi != sesi.id_sesi }
                        error = "Sesi dihapus dari tampilan, namun mungkin masih ada di database"
                        Log.e("SessionSekretarisCard", "All methods failed, removing from local list only: ${e3.message}")
                    }
                }
            }
        }
    }

    // Header
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Sesi",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        TextButton(
            onClick = onNavigateToAddSession,
            modifier = Modifier.height(32.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Session",
                tint = PrimaryColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Tambah Sesi",
                color = PrimaryColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    // Content
    when {
        isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = PrimaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        error != null -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: $error",
                    color = RedColor,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        sesiList.isEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.EventNote,
                        contentDescription = "No Sessions",
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Belum ada sesi aktif",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Update SessionOwnerCard usage untuk passing onEditSession
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                sesiList.forEach { sesi ->
                    SessionOwnerCard(
                        title = sesi.nama_materi ?: sesi.jenis_sesi,
                        meeting = buildString {
                            append("Pertemuan ${sesi.pertemuan}")
                            append(" • ${sesi.divisi}")
                            append(" • ${sesi.waktu_masuk}")
                            if (sesi.link_meet != null) {
                                append(" • Online")
                            } else if (sesi.ruangan != null) {
                                append(" • ${sesi.ruangan}")
                            }
                        },
                        onEditSession = { onEditSession(sesi) }, // Pass sesi data
                        onCloseSession = { showConfirmDialog = sesi },
                        onViewParticipants = { onViewParticipants(sesi) },
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }

    // Confirmation Dialog
    showConfirmDialog?.let { sesi ->
        AlertDialog(
            onDismissRequest = { showConfirmDialog = null },
            title = {
                Text(
                    text = "Tutup Sesi",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menutup sesi ${sesi.jenis_sesi} pertemuan ${sesi.pertemuan}?",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        closeSession(sesi)
                        showConfirmDialog = null
                    }
                ) {
                    Text(
                        text = "Ya, Tutup",
                        color = RedColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = null }
                ) {
                    Text(
                        text = "Batal",
                        color = Color.Gray
                    )
                }
            }
        )
    }
}