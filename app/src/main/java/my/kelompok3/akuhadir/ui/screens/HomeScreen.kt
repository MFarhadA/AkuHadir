package my.kelompok3.akuhadir.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import my.kelompok3.akuhadir.data.model.StatusData
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
                .padding(horizontal = 10.dp)
                .height(110.dp)
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
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFC4C4C4), CircleShape),
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

            Spacer(modifier = Modifier.height(16.dp))
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
                        .fillMaxHeight(), // Tambahkan fillMaxHeight
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
                    modifier = Modifier.weight(1f).fillMaxHeight(), // Tambahkan fillMaxHeight
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
                        .weight(0.4f)
                        .fillMaxHeight() // Tambahkan fillMaxHeight
                        .background(PrimaryColor, RoundedCornerShape(15.dp))
                )
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
                            fontWeight = FontWeight.SemiBold,
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
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Buka Sesi Pertemuan",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
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

            // Session List - Only 3 items
            val sessions = listOf(
                Triple("Pembelajaran UI/UX", "Pertemuan 15", "Hadir" to GreenColor),
                Triple("Pembelajaran UI/UX", "Pertemuan 15", "Izin" to PrimaryColor),
                Triple("Pembelajaran UI/UX", "Pertemuan 15", "Sakit" to RedColor),
                Triple("Pembelajaran UI/UX", "Pertemuan 16", "Alpha" to GrayColor)
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
        }

        // Floating Action Button - Aku Hadir
        FloatingActionButton(
            onClick = {
                // Tampilkan BottomSheet ketika tombol "Aku Hadir" ditekan
                showAttendanceBottomSheet = true
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(vertical = 25.dp, horizontal = 30.dp)
                .fillMaxWidth()
                .height(60.dp),
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
    onNavigateToSessionDetails: () -> Unit
) {
    Card(
        onClick = onNavigateToSessionDetails,
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