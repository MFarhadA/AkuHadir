// SessionCardMember.kt - Modified Version with Online/Offline Cards
package my.kelompok3.akuhadir.ui.components

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import my.kelompok3.akuhadir.ui.theme.GreenColor
import my.kelompok3.akuhadir.ui.theme.PrimaryColor
import my.kelompok3.akuhadir.data.model.SupabaseInstance
import my.kelompok3.akuhadir.data.manager.UserRegistrationManager
import io.github.jan.supabase.postgrest.from
import my.kelompok3.akuhadir.data.model.UserProfile
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class SessionData(
    @SerialName("id_sesi") val idSesi: Int,
    @SerialName("waktu_masuk") val waktuMasuk: String,
    val divisi: String,
    @SerialName("jenis_sesi") val jenisSesi: String,
    val pertemuan: Int,
    @SerialName("link_meet") val linkMeet: String? = null,
    val ruangan: String? = null,
    @SerialName("nama_materi") val namaMateri: String? = null,
    val keterangan: String = "berjalan"
)

@Composable
fun SessionCardMember(
    modifier: Modifier = Modifier,
    onNavigateToSessionDetails: ((String, String) -> Unit)? = null
) {
    var sessions by remember { mutableStateOf<List<SessionData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var userDivision by remember { mutableStateOf<String?>(null) }
    var userName by remember { mutableStateOf<String?>(null) }

    val supabase = SupabaseInstance.client
    val context = LocalContext.current

    // Fetch user division and sessions
    LaunchedEffect(Unit) {
        try {
            val currentUserId = UserRegistrationManager.getCurrentUserId()

            if (currentUserId != null) {
                Log.d("SessionCardMember", "Fetching data for user ID: $currentUserId")

                // Get user profile to find division
                val userProfileList = supabase.from("user_profile")
                    .select()
                    .decodeList<UserProfile>()
                    .filter { it.id_user == currentUserId }

                if (userProfileList.isEmpty()) {
                    error = "User profile not found"
                    Log.e("SessionCardMember", "User profile not found for ID: $currentUserId")
                    return@LaunchedEffect
                }

                val userProfile = userProfileList.first()
                userDivision = userProfile.divisi
                userName = userProfile.nama

                Log.d(
                    "SessionCardMember",
                    "User: ${userProfile.nama}, Division: ${userProfile.divisi}"
                )

                // Get sessions for user's division that are currently running
                val sessionsList = supabase.from("sesi")
                    .select {
                        filter {
                            ilike("divisi", userProfile.divisi.lowercase())
                            eq("Keterangan", "berjalan")
                        }
                    }
                    .decodeList<SessionData>()
                    .sortedByDescending { it.waktuMasuk }

                sessions = sessionsList
                Log.d("SessionCardMember", "Found ${sessions.size} running sessions for division: ${userProfile.divisi}")

                // Log each session for debugging
                sessions.forEach { session ->
                    Log.d("SessionCardMember", "Session: ${session.namaMateri ?: session.jenisSesi} - Pertemuan ${session.pertemuan}")
                }
            } else {
                error = "User not logged in"
                Log.e("SessionCardMember", "Current user ID is null")
            }
        } catch (e: Exception) {
            error = "Error loading sessions: ${e.message}"
            Log.e("SessionCardMember", "Error loading sessions: ${e.message}", e)
        } finally {
            isLoading = false
        }
    }

    when {
        isLoading -> {
            LoadingState()
        }

        error != null -> {
            ErrorState(error = error!!)
        }

        sessions.isEmpty() -> {
            EmptyState(
                userDivision = userDivision,
                userName = userName,
                modifier = modifier
            )
        }

        else -> {
            SessionsContent(
                sessions = sessions,
                userDivision = userDivision,
                onNavigateToSessionDetails = onNavigateToSessionDetails,
                onJoinMeeting = { meetingLink ->
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meetingLink))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Log.e("SessionCardMember", "Error opening meeting link: ${e.message}")
                    }
                },
                modifier = modifier
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = PrimaryColor,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Memuat sesi...",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ErrorState(error: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Gagal memuat sesi",
                    fontSize = 14.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = error,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    userDivision: String?,
    userName: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EventBusy,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tidak ada sesi aktif",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                userDivision?.let { division ->
                    Text(
                        text = "untuk divisi $division",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
                userName?.let { name ->
                    Text(
                        text = "Halo, $name!",
                        fontSize = 11.sp,
                        color = PrimaryColor,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionsContent(
    sessions: List<SessionData>,
    userDivision: String?,
    onNavigateToSessionDetails: ((String, String) -> Unit)?,
    onJoinMeeting: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        // Sessions list
        sessions.forEach { session ->
            val isOnline = session.linkMeet != null
            val sessionTitle = session.namaMateri ?: session.jenisSesi
            val sessionMeeting = "Pertemuan ${session.pertemuan}"
            val sessionTime = formatTime(session.waktuMasuk)

            if (isOnline) {
                SessionOnlineCard(
                    title = sessionTitle,
                    meeting = sessionMeeting,
                    time = sessionTime,
                    onJoinMeeting = {
                        session.linkMeet?.let { link ->
                            onJoinMeeting(link)
                        }
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                SessionOfflineCard(
                    title = sessionTitle,
                    meeting = sessionMeeting,
                    location = session.ruangan ?: "TBA",
                    time = sessionTime,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

// Helper function to format time
private fun formatTime(timeString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(timeString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        timeString
    }
}