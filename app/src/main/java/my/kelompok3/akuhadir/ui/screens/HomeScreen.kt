// HomeScreen.kt - Updated version dengan sticky header dan scrollable content
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
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import my.kelompok3.akuhadir.data.model.StatusData
import my.kelompok3.akuhadir.data.model.User
import my.kelompok3.akuhadir.ui.theme.*
import my.kelompok3.akuhadir.ui.components.AttendanceBottomSheet
import my.kelompok3.akuhadir.ui.components.SessionAvailableCard
import my.kelompok3.akuhadir.ui.components.SessionOwnerCard
import my.kelompok3.akuhadir.ui.components.SessionOnlineCard
import my.kelompok3.akuhadir.ui.components.SessionOfflineCard
// untuk memamnggil databasenya
import kotlinx.serialization.Serializable
import my.kelompok3.akuhadir.data.model.SupabaseInstance

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSessionDetails: (String, String) -> Unit,
    onNavigateToAddSession: () -> Unit,
    onNavigateToListSessions: () -> Unit,
    onNavigateToAttendance: () -> Unit,
) {



    // Memanggil koneksi ke database
    var connectionStatus by remember { mutableStateOf("Testing connection...") }
    val  supabase = SupabaseInstance.client
    // Test koneksi saat composable pertama kali dimuat
    LaunchedEffect(Unit) {
        try {
            Log.d("HomeScreen", "Starting connection test from HomeScreen")
            println("HomeScreen: Starting connection test")

            val isConnected = SupabaseInstance.testConnection()

            connectionStatus = if (isConnected) {
                "Database connected successfully!"
            } else {
                "Database connection failed!"
            }

            Log.d("HomeScreen", "Connection status: $connectionStatus")
            println("HomeScreen: Connection status: $connectionStatus")

            // test pengambilan data

            val users = supabase.from("user").select().decodeList<User>()

            val user = supabase.from("user").select(columns = Columns.list("id_user, email,password")).decodeSingle<User>()
            Log.d("HomeScreen", "User: ${user.email},${user.password}")
            Log.d("HomeScreen", "User: ${users.size}, entah apa ini    ${users}")

        } catch (e: Exception) {
            connectionStatus = "Connection error: ${e.message}"
            Log.e("HomeScreen", "Connection error: ${e.message}", e)
            println("HomeScreen: Connection error: ${e.message}")
        }

    }

    // State untuk mengontrol visibility BottomSheet
    var showAttendanceBottomSheet by remember { mutableStateOf(false) }

    // State untuk menentukan jenis session yang ditampilkan
    var sessionType by remember { mutableStateOf("none") } // "none", "owner", "online", "offline"

    // Buat SheetState dengan expanded = true agar bottomsheet terbuka penuh
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // Scroll state untuk konten yang dapat di-scroll
    val scrollState = rememberScrollState()

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
                    .offset(y = (-94).dp) // Adjust positioning
                    .padding(horizontal = 30.dp)
            ) {
                Spacer(modifier = Modifier.height(25.dp))

                // Welcome Message
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
                                text = "Muhammad Farhad Ajilla",
                                color = Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 18.sp
                            )
                            Text(
                                text = "2355201063",
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
                .padding(top = 130.dp) // Beri ruang untuk header
                .verticalScroll(scrollState)
                .padding(horizontal = 30.dp)
                .padding(bottom = 100.dp) // Beri ruang untuk FAB
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Statistics Section
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
                        PieChartWithCenter(
                            data = listOf(
                                StatusData("Hadir", 13, GreenColor),
                                StatusData("Izin", 2, PrimaryColor),
                                StatusData("Sakit", 2, RedColor),
                                StatusData("Alpha", 1, GrayColor)
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
                        StatusItemHorizontal(
                            color = GreenColor,
                            count = "13",
                            label = "Hadir"
                        )
                        StatusItemHorizontal(
                            color = PrimaryColor,
                            count = "2",
                            label = "Izin"
                        )
                        StatusItemHorizontal(
                            color = RedColor,
                            count = "2",
                            label = "Sakit"
                        )
                        StatusItemHorizontal(
                            color = GrayColor,
                            count = "1",
                            label = "Alpha"
                        )
                    }
                }

                // Box untuk mengisi ruang kosong
                Box(
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxHeight()
                        .background(PrimaryColor, RoundedCornerShape(15.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Session Card - Menggunakan components yang berbeda berdasarkan sessionType
            when (sessionType) {
                "none" -> {
                    SessionAvailableCard(
                        onNavigateToAddSession = onNavigateToAddSession
                    )
                }
                "owner" -> {
                    SessionOwnerCard(
                        title = "Pembelajaran Design Wireframe",
                        meeting = "Pertemuan 16",
                        onEditSession = {
                            // Logic untuk mengedit sesi
                        },
                        onCloseSession = {
                            // Logic untuk menutup sesi
                            sessionType = "none"
                        },
                        onViewParticipants = {
                            // Logic untuk melihat peserta
                        }
                    )
                }
                "online" -> {
                    SessionOnlineCard(
                        title = "Pembelajaran Design Wireframe",
                        meeting = "Pertemuan 16",
                        time = "10:00 WIB",
                        onJoinMeeting = {
                            // Logic untuk join meeting (buka link meet)
                        }
                    )
                }
                "offline" -> {
                    SessionOfflineCard(
                        title = "Pembelajaran Design Wireframe",
                        meeting = "Pertemuan 16",
                        location = "B 201",
                        time = "11:00 WIB"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons untuk demo (hapus ini di production)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { sessionType = "none" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = GrayColor)
                ) {
                    Text("None", fontSize = 9.sp, color = Color.White)
                }
                Button(
                    onClick = { sessionType = "owner" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = RedColor)
                ) {
                    Text("Owner", fontSize = 9.sp, color = Color.White)
                }
                Button(
                    onClick = { sessionType = "online" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenColor)
                ) {
                    Text("Online", fontSize = 9.sp, color = Color.White)
                }
                Button(
                    onClick = { sessionType = "offline" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Text("Offline", fontSize = 9.sp, color = Color.White)
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

            Spacer(modifier = Modifier.height(8.dp))

            // Session List - Hanya 3 item terbaru
            val sessions = listOf(
                Triple("Pembelajaran UI/UX", "Pertemuan 15", "Hadir" to GreenColor),
                Triple("Pembelajaran UI/UX", "Pertemuan 14", "Izin" to PrimaryColor),
                Triple("Pembelajaran UI/UX", "Pertemuan 13", "Sakit" to RedColor)
            )

            sessions.forEach { (title, meeting, status) ->
                SessionItemCard(
                    title = title,
                    meeting = meeting,
                    status = status,
                    onNavigateToSessionDetails = onNavigateToSessionDetails,
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Extra space di bawah untuk memastikan semua konten bisa di-scroll
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Floating Action Button - Sticky (tidak ikut scroll)
        FloatingActionButton(
            onClick = {
                showAttendanceBottomSheet = true
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(vertical = 25.dp, horizontal = 30.dp)
                .fillMaxWidth()
                .height(60.dp)
                .zIndex(1f), // Pastikan FAB selalu di atas
            containerColor = PrimaryColor,
            shape = RoundedCornerShape(15.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Fingerprint,
                    contentDescription = "Point up",
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

        // Tampilkan BottomSheet jika showAttendanceBottomSheet = true
        if (showAttendanceBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAttendanceBottomSheet = false },
                sheetState = bottomSheetState,
                containerColor = Color.Transparent,
                contentColor = Color.Transparent,
                dragHandle = null
            ) {
                AttendanceBottomSheet(
                    onDismiss = { showAttendanceBottomSheet = false },
                    onSubmitAttendance = {
                        onNavigateToAttendance()
                        showAttendanceBottomSheet = false
                    }
                )
            }
        }
    }
}

// Existing components remain the same
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
        // Pie Chart
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
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
                    topLeft = Offset(
                        center.x - radius,
                        center.y - radius
                    ),
                    size = Size(radius * 2, radius * 2)
                )

                currentAngle += sweepAngle
            }
        }

        // Center Text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
    title: String,
    meeting: String,
    status: Pair<String, Color>,
    onNavigateToSessionDetails: (String, String) -> Unit
) {
    Card(
        onClick = { onNavigateToSessionDetails(title, meeting) },
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