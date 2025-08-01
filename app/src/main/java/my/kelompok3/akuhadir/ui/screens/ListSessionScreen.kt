package my.kelompok3.akuhadir.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.kelompok3.akuhadir.data.model.SessionItem
import my.kelompok3.akuhadir.data.model.SesiData
import my.kelompok3.akuhadir.data.model.SupabaseInstance
import my.kelompok3.akuhadir.data.manager.UserRegistrationManager
import my.kelompok3.akuhadir.ui.theme.*
import kotlinx.coroutines.launch
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import my.kelompok3.akuhadir.data.manager.RoleManager
import my.kelompok3.akuhadir.data.model.UserProfile
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListSessionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSessionDetails: (Int, String, String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    // State for user profile
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoadingProfile by remember { mutableStateOf(true) }
    var profileError by remember { mutableStateOf<String?>(null) }

    // State for sessions data
    var sessionsFromDb by remember { mutableStateOf<List<SesiData>>(emptyList()) }
    var isLoadingSessions by remember { mutableStateOf(true) }
    var sessionsError by remember { mutableStateOf<String?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }

    // Load user profile
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                isLoadingProfile = true
                profileError = null

                val currentUserId = UserRegistrationManager.getCurrentUserId()
                if (currentUserId != null) {
                    val result = SupabaseInstance.client.from("user_profile")
                        .select {
                            filter {
                                eq("id_user", currentUserId)
                            }
                        }
                        .decodeSingle<UserProfile>()

                    userProfile = result
                    Log.d("ListSessionScreen", "User Profile loaded: ${userProfile?.nama}, Divisi: ${userProfile?.divisi}")
                }
            } catch (e: Exception) {
                profileError = "Error loading profile: ${e.message}"
                Log.e("ListSessionScreen", "Profile error: ${e.message}", e)
            } finally {
                isLoadingProfile = false
            }
        }
    }

    // Load sessions based on user's role and division
    LaunchedEffect(userProfile, refreshTrigger) {
        coroutineScope.launch {
            try {
                isLoadingSessions = true
                sessionsError = null

                // Get current user ID
                val currentUserId = UserRegistrationManager.getCurrentUserId()
                if (currentUserId == null) {
                    sessionsError = "User ID tidak ditemukan"
                    isLoadingSessions = false
                    return@launch
                }

                // Create role manager instance
                val roleManager = RoleManager()

                // Check user role
                val userRole = roleManager.getUserRoleByUserId(currentUserId)
                val isSekretaris = userRole?.let { roleManager.isSekretaris(currentUserId) } ?: false

                // Query sessions based on role
                val result = if (isSekretaris) {
                    // If user is sekretaris, show all sessions
                    Log.d("ListSessionScreen", "User is sekretaris, showing all sessions")
                    SupabaseInstance.client.from("sesi")
                        .select {
                            order("pertemuan", Order.DESCENDING)
                        }
                        .decodeList<SesiData>()
                        .sortedByDescending { it.pertemuan.toIntOrNull() ?: 0 }
                } else {
                    // If user is anggota, filter by division
                    userProfile?.divisi?.let { divisi ->
                        Log.d("ListSessionScreen", "User is anggota, filtering by divisi: $divisi")
                        SupabaseInstance.client.from("sesi")
                            .select {
                                filter {
                                    ilike("divisi", divisi.lowercase())
                                }
                                order("pertemuan", Order.DESCENDING)
                            }
                            .decodeList<SesiData>()
                            .sortedByDescending { it.pertemuan.toIntOrNull() ?: 0 }
                    } ?: emptyList()
                }

                sessionsFromDb = result ?: emptyList()
                Log.d("ListSessionScreen", "Loaded ${sessionsFromDb.size} sessions")

            } catch (e: Exception) {
                sessionsError = "Gagal memuat sesi: ${e.message}"
                Log.e("ListSessionScreen", "Error loading sessions: ${e.message}", e)
            } finally {
                isLoadingSessions = false
            }
        }
    }

    // Function to refresh data
    fun refreshData() {
        refreshTrigger++
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 10.dp)
                    .background(
                        color = PrimaryColor,
                        shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(top = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowCircleLeft,
                                contentDescription = "Back",
                                modifier = Modifier.size(30.dp),
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "List Sesi",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Refresh button
                    IconButton(
                        onClick = { refreshData() },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            // Content area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(horizontal = 14.dp)
            ) {
                when {
                    isLoadingProfile || isLoadingSessions -> {
                        // Loading state
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = PrimaryColor,
                                modifier = Modifier.size(50.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Memuat sesi...",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }

                    profileError != null -> {
                        // Profile error state
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "❌",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = profileError ?: "Terjadi kesalahan saat memuat profil",
                                color = Color.Red,
                                fontSize = 14.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { refreshData() },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                            ) {
                                Text("Coba Lagi")
                            }
                        }
                    }

                    sessionsError != null -> {
                        // Sessions error state
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "❌",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = sessionsError ?: "Terjadi kesalahan saat memuat sesi",
                                color = Color.Red,
                                fontSize = 14.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { refreshData() },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                            ) {
                                Text("Coba Lagi")
                            }
                        }
                    }

                    sessionsFromDb.isEmpty() -> {
                        // Empty state
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "📋",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Belum ada sesi yang tersedia",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Sesi akan muncul di sini setelah dibuat",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    else -> {
                        // Success state with data
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Header info with division
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DateRange,
                                                contentDescription = null,
                                                tint = PrimaryColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Total ${sessionsFromDb.size} sesi ditemukan",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color.Black
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Divisi: ${userProfile?.divisi ?: "Tidak diketahui"}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // List sessions
                            items(sessionsFromDb) { sesi ->
                                SessionListItemFromDb(
                                    sesi = sesi,
                                    onNavigateToSessionDetails = onNavigateToSessionDetails
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SessionListItemFromDb(
    sesi: SesiData,
    onNavigateToSessionDetails: (Int, String, String) -> Unit
) {

    // Tentukan warna box berdasarkan jenis divisi
    val divisiColor = when (sesi.divisi?.lowercase()) {
        "software" -> GreenColor
        "hardware" -> PrimaryColor
        "game" -> RedColor
        "alpha" -> GrayColor
        else -> GrayColor
    }

    // Tentukan warna status berdasarkan jenis sesi
    val statusColor = when (sesi.jenis_sesi?.lowercase()) {
        "hadir" -> GreenColor
        "izin" -> PrimaryColor
        "sakit" -> RedColor
        "alpha" -> GrayColor
        else -> GrayColor
    }

    // Format pertemuan untuk display
    val displayPertemuan = sesi.pertemuan ?: "Pertemuan tidak tersedia"

    Card(
        onClick = {
            onNavigateToSessionDetails(
                sesi.id_sesi ?: 0,
                sesi.nama_materi ?: "Sesi tanpa nama",
                sesi.pertemuan ?: "-"
            )
        },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(15.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .width(10.dp)
                        .height(32.dp)
                        .background(divisiColor, RoundedCornerShape(15.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = sesi.nama_materi ?: "Sesi tanpa nama",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        lineHeight = 16.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Pertemuan $displayPertemuan",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        lineHeight = 14.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .background(statusColor, RoundedCornerShape(100))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = sesi.jenis_sesi ?: "-",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// Fallback component untuk SessionItem (jika masih dibutuhkan)
@Composable
fun SessionListItem(
    session: SessionItem,
    onNavigateToSessionDetails: (String, String) -> Unit
) {
    Card(
        onClick = { onNavigateToSessionDetails(session.title, session.meeting) },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(10.dp)
                        .height(32.dp)
                        .background(RedColor, RoundedCornerShape(15.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = session.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        lineHeight = 16.sp
                    )
                    Text(
                        text = session.meeting,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        lineHeight = 14.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(session.statusColor, RoundedCornerShape(100))
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text(
                    text = session.status,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}