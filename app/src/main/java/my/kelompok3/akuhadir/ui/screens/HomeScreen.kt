// HomeScreen.kt - Fixed version
@file:Suppress("NAME_SHADOWING")

package my.kelompok3.akuhadir.ui.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import my.kelompok3.akuhadir.data.model.StatusData
import my.kelompok3.akuhadir.data.model.User
import my.kelompok3.akuhadir.ui.theme.*
import my.kelompok3.akuhadir.ui.components.AttendanceBottomSheet
import my.kelompok3.akuhadir.data.model.SupabaseInstance

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.result.PostgrestResult

import my.kelompok3.akuhadir.data.manager.UserRegistrationManager
import my.kelompok3.akuhadir.data.model.UserProfile
import my.kelompok3.akuhadir.data.manager.RoleManager
import my.kelompok3.akuhadir.data.model.RoleData
import my.kelompok3.akuhadir.data.model.RoleType
import my.kelompok3.akuhadir.ui.components.SessionCardMember
import my.kelompok3.akuhadir.ui.components.SessionSekretarisCard
import my.kelompok3.akuhadir.data.model.SesiData
import kotlinx.coroutines.launch
import my.kelompok3.akuhadir.data.model.IdSesiResponse
import my.kelompok3.akuhadir.data.model.Presensi
import my.kelompok3.akuhadir.data.model.Sesi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSessionDetails: (Int,String, String) -> Unit,
    onNavigateToAddSession: () -> Unit,
    onNavigateToListSessions: () -> Unit,
    onNavigateToEditSession: (SesiData) -> Unit
) {
    val roleManager = remember { RoleManager() }
    val coroutineScope = rememberCoroutineScope()

    var currentUserRole by remember { mutableStateOf<RoleData?>(null) }
    var isLoadingRole by remember { mutableStateOf(false) }
    var roleError by remember { mutableStateOf<String?>(null) }
    var roleStatistics by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    var currentSession by remember { mutableStateOf<SesiData?>(null) }
    var isLoadingScreen by remember { mutableStateOf(true) }
    var connectionStatus by remember { mutableStateOf("Testing connection...") }

    val supabase = SupabaseInstance.client
    val currentUserId = UserRegistrationManager.getCurrentUserId()
    val currentUserEmail = UserRegistrationManager.getCurrentUserEmail()

    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoadingProfile by remember { mutableStateOf(false) }
    var profileError by remember { mutableStateOf<String?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }

    var sessionsFromDb by remember { mutableStateOf<List<SesiData>>(emptyList()) }
    var isLoadingSessions by remember { mutableStateOf(true) }
    var sessionsError by remember { mutableStateOf<String?>(null) }

    var showAttendanceBottomSheet by remember { mutableStateOf(false) }
    var sessionType by remember { mutableStateOf("none") }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scrollState = rememberScrollState()

    // Test koneksi database
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                Log.d("HomeScreen", "Starting connection test from HomeScreen")
                println("HomeScreen: Welcome, $currentUserEmail! Your ID is $currentUserId and your role is ${currentUserRole?.role}")

                val isConnected = SupabaseInstance.testConnection()
                connectionStatus = if (isConnected) {
                    "Database connected successfully!"
                } else {
                    "Database connection failed!"
                }

                Log.d("HomeScreen", "Connection status: $connectionStatus")

                // Test pengambilan data user (commented untuk mencegah error)
                /*
                val users = supabase.from("user").select().decodeList<User>()
                val user = supabase.from("user").select(columns = Columns.list("id_user, email,password")).decodeSingle<User>()

                // Hapus operasi delete yang berbahaya
                // val deletedCity = supabase.from("user").delete { ... }

                Log.d("HomeScreen", "User: ${user.email}, ${user.password}")
                Log.d("HomeScreen", "Users count: ${users.size}")
                */

            } catch (e: Exception) {
                connectionStatus = "Connection error: ${e.message}"
                Log.e("HomeScreen", "Connection error: ${e.message}", e)
            }
        }
    }

    // Load user profile
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                isLoadingProfile = true
                profileError = null

                val currentUserId = UserRegistrationManager.getCurrentUserId()
                if (currentUserId != null) {
                    val result = supabase.from("user_profile")
                        .select {
                            filter {
                                eq("id_user", currentUserId)
                            }
                        }
                        .decodeSingle<UserProfile>()

                    userProfile = result
                    Log.d("HomeScreen", "User Profile: ${userProfile?.nama}, NIM: ${userProfile?.nim} Role: ${userProfile?.role} Divisi: ${userProfile?.divisi}")
                }
            } catch (e: Exception) {
                profileError = "Error loading profile: ${e.message}"
                Log.e("HomeScreen", "Profile error: ${e.message}", e)
            } finally {
                isLoadingProfile = false
            }
        }
    }

    suspend fun loadAttendanceStatusForUser(userProfile: UserProfile): Map<String, Int> {
        val supabase = SupabaseInstance.client

        Log.d("HomeScreen", "Loading attendance status for user: ${userProfile.id_user_profile}, divisi: ${userProfile.divisi}")

        try {
            // Get all sessions for the user's division
            val allSessions = supabase.from("sesi")
                .select {
                    filter {
                        ilike("divisi", userProfile.divisi.lowercase())
                    }
                }
                .decodeList<SesiData>()
            Log.d("HomeScreen", "All sessions for ${userProfile.divisi}: ${allSessions.size}")

            // Get all presences for the user
            val presences = supabase.from("presensi")
                .select {
                    filter {
                        eq("id_user_profile", userProfile.id_user_profile)
                    }
                }
                .decodeList<Presensi>()
            Log.d("HomeScreen", "Presences for user ${userProfile.id_user_profile}: ${presences.size}")

            // Count attendance by status
            val hadirCount = presences.count { it.kehadiran.equals("hadir", ignoreCase = true) }
            val izinCount = presences.count { it.kehadiran.equals("izin", ignoreCase = true) }
            val sakitCount = presences.count { it.kehadiran.equals("sakit", ignoreCase = true) }

            // Calculate alpha (absent) count
            val alphaCount = (allSessions.size - (hadirCount + izinCount + sakitCount)).coerceAtLeast(0)

            Log.d("HomeScreen", "Attendance stats - Hadir: $hadirCount, Izin: $izinCount, Sakit: $sakitCount, Alpha: $alphaCount")
            Log.d("HomeScreen", "Total sessions: ${allSessions.size}, Total presences: ${presences.size}")

            return mapOf(
                "hadir" to hadirCount,
                "izin" to izinCount,
                "sakit" to sakitCount,
                "alpha" to alphaCount
            )
        } catch (e: Exception) {
            Log.e("HomeScreen", "Error calculating attendance status: ${e.message}", e)
            // Return empty map in case of error
            return mapOf(
                "hadir" to 0,
                "izin" to 0,
                "sakit" to 0,
                "alpha" to 0
            )
        }
    }

    LaunchedEffect(userProfile) {
        userProfile?.let { profile ->
            try {
                val statusCounts = loadAttendanceStatusForUser(profile)
                roleStatistics = statusCounts
            } catch (e: Exception) {
                roleError = "Error loading attendance status: ${e.message}"
            }
        }
    }

    // Load sessions
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
                            limit(3)
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
                                limit(3)
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

    // Load user role
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                isLoadingRole = true
                roleError = null

                val currentUserId = UserRegistrationManager.getCurrentUserId()
                if (currentUserId != null) {
                    currentUserRole = roleManager.getUserRoleByUserId(currentUserId)
                    roleStatistics = roleManager.getRoleStatistics()

                    currentUserRole?.let { roleData ->
                        Log.d("HomeScreen", "Current User Role: ${roleData.role}")
                        Log.d("HomeScreen", "Role Display Name: ${roleManager.getRoleDisplayName(roleData.role)}")

                        val isAnggota = roleManager.isAnggota(currentUserId)
                        val isSekretaris = roleManager.isSekretaris(currentUserId)
                        val isPengurus = roleManager.isPengurus(currentUserId)

                        Log.d("HomeScreen", "Is Anggota: $isAnggota, Is Sekretaris: $isSekretaris, Is Pengurus: $isPengurus")
                    }

                    Log.d("HomeScreen", "Role Statistics: $roleStatistics")
                }
            } catch (e: Exception) {
                roleError = "Error loading role: ${e.message}"
                Log.e("HomeScreen", "Error loading user role: ${e.message}", e)
            } finally {
                isLoadingRole = false
            }
        }
    }

    fun refreshData() {
        refreshTrigger++
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // Header Section - Sticky
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
        ) {
            // Background header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .height(110.dp)
                    .background(
                        color = Color(0xFF6B7DDC),
                        shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                    )
            )

            // Header content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-94).dp)
                    .padding(horizontal = 30.dp)
            ) {
                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = "Selamat Datang!",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(5.dp))

                // Profile Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(BackgroundColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = userProfile?.nama ?: "Loading...",
                                color = Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 18.sp
                            )
                            Text(
                                text = userProfile?.nim ?: "Loading...",
                                color = Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 140.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = 30.dp)
                .padding(bottom = 100.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Statistics Section
            if (isLoadingProfile || userProfile == null) {
                // Show loading indicator while profile is loading
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pie Chart Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Use the actual data from roleStatistics
                            PieChartWithCenter(
                                data = listOf(
                                    StatusData("Hadir", roleStatistics["hadir"] ?: 0, GreenColor),
                                    StatusData("Izin", roleStatistics["izin"] ?: 0, PrimaryColor),
                                    StatusData("Sakit", roleStatistics["sakit"] ?: 0, RedColor),
                                    StatusData("Alpha", roleStatistics["alpha"] ?: 0, GrayColor)
                                )
                            )
                        }
                    }

                    // Status Items Card
                    Card(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Use the actual data from roleStatistics
                            StatusItemHorizontal(color = GreenColor, count = (roleStatistics["hadir"] ?: 0).toString(), label = "Hadir")
                            StatusItemHorizontal(color = PrimaryColor, count = (roleStatistics["izin"] ?: 0).toString(), label = "Izin")
                            StatusItemHorizontal(color = RedColor, count = (roleStatistics["sakit"] ?: 0).toString(), label = "Sakit")
                            StatusItemHorizontal(color = GrayColor, count = (roleStatistics["alpha"] ?: 0).toString(), label = "Alpha")
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(0.2f)
                            .fillMaxHeight()
                            .background(PrimaryColor, RoundedCornerShape(15.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Role-based Session Cards
            if (isLoadingRole) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                currentUserRole?.let { roleData ->
                    when (roleManager.getRoleType(roleData.role)) {
                        RoleType.PENGURUS -> {
                            SessionCardMember()
                        }
                        RoleType.SEKRETARIS -> {
                            SessionSekretarisCard(
                                onNavigateToAddSession = onNavigateToAddSession,
                                onNavigateToSessionDetails = onNavigateToSessionDetails,
                                onEditSession = onNavigateToEditSession,
                                refreshData = { refreshData() }
                            )
                        }
                        RoleType.ANGGOTA -> {
                            SessionCardMember()
                        }
                        null -> {
                            Text(
                                text = "Role tidak dikenali",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Session List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "List Sesi",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                TextButton(
                    onClick = onNavigateToListSessions,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        text = "Lihat semua",
                        color = PrimaryColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sessions List
            if (isLoadingSessions) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (sessionsError != null) {
                Text(
                    text = sessionsError ?: "",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                sessionsFromDb.forEach { sesi ->

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
                        else -> GrayColor
                    }

                    SessionItemCard(
                        id_sesi = sesi.id_sesi ?: 0,
                        title = sesi.nama_materi ?: "Sesi tanpa nama",
                        meeting = "pertemuan ${sesi.pertemuan ?: "-"}",
                        jenisSesi = divisiColor,
                        status = sesi.jenis_sesi?.let { it to statusColor } ?: ("-" to GrayColor),
                        onNavigateToSessionDetails = onNavigateToSessionDetails,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                    showAttendanceBottomSheet = true
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(vertical = 25.dp, horizontal = 30.dp)
                .fillMaxWidth()
                .height(60.dp)
                .zIndex(1f),
            containerColor = PrimaryColor,
            shape = RoundedCornerShape(15.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Fingerprint,
                    contentDescription = "Aku Hadir",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Aku Hadir",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Bottom Sheet
        if (showAttendanceBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showAttendanceBottomSheet = false
                    refreshData() // Call refreshData here
                },
                sheetState = bottomSheetState,
                containerColor = Color.Transparent,
                contentColor = Color.Transparent,
                dragHandle = null
            ) {
                AttendanceBottomSheet(
                    userProfile = userProfile,
                    onDismiss = {
                        showAttendanceBottomSheet = false
                        refreshData() // Also call refreshData here if you want to ensure it's refreshed on submit
                    },
                    onSubmitAttendance = {
                        showAttendanceBottomSheet = false
                        refreshData() // Call refreshData here as well
                    }
                )
            }
        }
    }
}

@Composable
fun StatusItemHorizontal(
    color: Color,
    count: String,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(25.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PieChartWithCenter(
    data: List<StatusData>,
    modifier: Modifier = Modifier,
    chartSize: Dp = 120.dp,
    strokeWidth: Dp = 15.dp
) {
    val total = data.sumOf { it.count }

    Box(
        modifier = modifier.size(chartSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 - strokeWidth.toPx() / 2

            var currentAngle = -90f

            data.forEach { item ->
                val sweepAngle = (item.count.toFloat() / total) * 360f

                drawArc(
                    color = item.color,
                    startAngle = currentAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(strokeWidth.toPx()),
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )

                currentAngle += sweepAngle
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = total.toString(),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = GreenColor,
                lineHeight = 1.sp
            )
            Text(
                text = "Sesi",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Black,
                lineHeight = 1.sp
            )
        }
    }
}

@Composable
fun SessionItemCard(
    id_sesi: Int,
    title: String,
    meeting: String,
    jenisSesi: Color,
    status: Pair<String, Color>,
    onNavigateToSessionDetails: (Int, String, String) -> Unit
) {
    Card(
        onClick = { onNavigateToSessionDetails(id_sesi, title, meeting) },
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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .width(10.dp)
                        .height(32.dp)
                        .background(jenisSesi, RoundedCornerShape(15.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        lineHeight = 16.sp
                    )
                    Text(
                        text = meeting,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        lineHeight = 14.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(status.second, RoundedCornerShape(100))
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text(
                    text = status.first,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}