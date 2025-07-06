package my.kelompok3.akuhadir.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.kelompok3.akuhadir.ui.theme.*
import my.kelompok3.akuhadir.ui.components.AttendanceBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSessionDetails: () -> Unit,
    onNavigateToAddSession: () -> Unit,
    onNavigateToListSessions: () -> Unit,
    onNavigateToAttendance: () -> Unit,
) {

    // State untuk mengontrol visibility BottomSheet
    var showAttendanceBottomSheet by remember { mutableStateOf(false) }

    // Buat SheetState dengan expanded = true agar bottomsheet terbuka penuh
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // Ini akan membuat bottomsheet langsung terbuka penuh
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
                .height(130.dp)
                .background(
                    color = Color(0xFF6B7DDC),
                    shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                )
                .align(Alignment.TopCenter)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(horizontal = 14.dp)
                .padding(bottom = 80.dp) // Add bottom padding for FAB
        ) {

            Spacer(modifier = Modifier.height(25.dp))

            // Welcome Message Card
            Text(
                text = "Selamat Datang!",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFE5E7EB), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Muhammad Farhad Ajilla",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "2355201063",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Cards Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Sesi Card
                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total Sesi",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .height(50.dp)
                                .background(GreenColor, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "19",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Status Items Card
                Card(
                    modifier = Modifier.weight(1f),
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            // No Session Available Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f), // agar Box mengisi ruang vertikal
                        contentAlignment = Alignment.Center // teks di tengah vertikal & horizontal
                    ) {
                        Text(
                            text = "Tidak ada sesi tersedia",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Create Session Button
                    Button(
                        onClick = onNavigateToAddSession,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Buka Sesi Pertemuan",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
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
                    fontWeight = FontWeight.Bold,
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
                        fontSize = 12.sp
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Session List - Only 3 items
            val sessions = listOf(
                Triple("Pembelajaran UI/UX", "Pertemuan 15", "Hadir" to GreenColor),
                Triple("Pembelajaran UI/UX", "Pertemuan 15", "Izin" to PrimaryColor),
                Triple("Pembelajaran UI/UX", "Pertemuan 15", "Sakit" to RedColor),
                Triple("Pembelajaran UI/UX", "Pertemuan 16", "alpha" to GrayColor)
            )

            sessions.forEach { (title, meeting, status) ->
                SessionItemCard(
                    title = title,
                    meeting = meeting,
                    status = status,
                    onNavigateToSessionDetails = onNavigateToSessionDetails
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        // Floating Action Button - Aku Hadir
        FloatingActionButton(
            onClick = {
                // Tampilkan BottomSheet ketika tombol "Aku Hadir" ditekan
                showAttendanceBottomSheet = true
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .width(200.dp)
                .height(56.dp),
            containerColor = PrimaryColor,
            shape = RoundedCornerShape(28.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fingerprint icon placeholder
                Text(
                    text = "👆",
                    fontSize = 20.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Aku Hadir",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
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
                dragHandle = null // Hapus handle drag default
            ) {
                AttendanceBottomSheet(
                    onDismiss = { showAttendanceBottomSheet = false },
                    onSubmitAttendance = {
                        // Lakukan aksi submit kehadiran
                        onNavigateToAttendance() // Opsional: navigasi ke halaman kehadiran setelah submit
                        showAttendanceBottomSheet = false
                    }
                )
            }
        }
    }
}

// Fungsi-fungsi pendukung tetap sama
@Composable
fun StatusItemHorizontal(
    color: Color,
    count: String,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SessionItemCard(
    title: String,
    meeting: String,
    status: Pair<String, Color>,
    onNavigateToSessionDetails: () -> Unit
) {
    Card(
        onClick = onNavigateToSessionDetails,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(32.dp)
                        .background(Color.Red, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = meeting,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(status.second, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = status.first,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}