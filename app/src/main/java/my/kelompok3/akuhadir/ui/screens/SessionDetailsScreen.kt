package my.kelompok3.akuhadir.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.kelompok3.akuhadir.data.model.AttendeeItem
import my.kelompok3.akuhadir.ui.theme.*

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import io.github.jan.supabase.auth.providers.Facebook
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import my.kelompok3.akuhadir.data.model.Presensi
import my.kelompok3.akuhadir.data.model.SesiData
import my.kelompok3.akuhadir.data.model.SupabaseInstance
import my.kelompok3.akuhadir.data.model.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailsScreen(
    id: Int,
    title: String,
    meeting: String,
    onNavigateBack: () -> Unit
) {
    // State untuk menyimpan data dari database
    var sesiData by remember { mutableStateOf<SesiData?>(null) }
    var userProfiles by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var presensiData by remember { mutableStateOf<List<Presensi>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedRole by remember { mutableStateOf("anggota") }

    // State untuk menampilkan dialog Gambar
    var showImageDialog by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    val supabaseClient = SupabaseInstance.client

    // LaunchedEffect untuk memuat data saat screen pertama kali ditampilkan
    LaunchedEffect(id) {
        try {
            isLoading = true
            error = null

            withContext(Dispatchers.IO) {
                val sesi = supabaseClient.from("sesi")
                    .select {
                        filter {
                            eq("id_sesi", id)
                        }
                    }
                    .decodeSingle<SesiData>()

                sesiData = sesi

                val profiles = supabaseClient.from("user_profile")
                    .select {
                        filter {
                            ilike("divisi", sesi.divisi.lowercase())
                        }
                    }
                    .decodeList<UserProfile>()

                userProfiles = profiles

                val presensi = supabaseClient.from("presensi")
                    .select {
                        filter {
                            eq("id_sesi", id)
                        }
                    }
                    .decodeList<Presensi>()

                presensiData = presensi

                Log.d("SessionDetailsScreen", "Data loaded successfully")
                Log.d("SessionDetailsScreen", "Sesi data: $sesi")
                Log.d("SessionDetailsScreen", "User profiles: $profiles")
                Log.d("SessionDetailsScreen", "Presensi data: $presensi")
            }
        } catch (e: Exception) {
            error = "Error loading data: ${e.message}"
            Log.e("SessionDetailsScreen", "Error loading data", e)
        } finally {
            isLoading = false
        }
    }


    // Warna icon buku berdasarkan divisi
    val bookColor = when (sesiData?.divisi) {
        "Software" -> GreenColor
        "Game" -> RedColor
        "Hardware" -> PrimaryColor
        else -> PrimaryColor
    }

    Log.d("Warna Buku", "Divisi: ${sesiData?.divisi}, Warna buku: $bookColor")

    // Filter berdasarkan role yang dipilih
    val displayedUsers = if (selectedRole.equals("pengurus", ignoreCase = true)) {
        userProfiles.filter {
            it.role.equals("pengurus", ignoreCase = true) || it.role.equals("sekretaris", ignoreCase = true)
        }
    } else {
        userProfiles.filter {
            it.role.equals(selectedRole, ignoreCase = true)
        }
    }

    // Buat AttendeeItem dari data yang difilter
    val attendees = displayedUsers.map { user ->
        val presensi = presensiData.find { it.id_user_profile == user.id_user_profile }
        val status = presensi?.kehadiran ?: "Alpha"
        val statusColor = when (status.lowercase()) {
            "hadir" -> GreenColor
            "izin" -> PrimaryColor
            "sakit" -> RedColor
            "alpha" -> GrayColor
            else -> GrayColor
        }
        AttendeeItem(user.nama, user.nim, status, statusColor, presensi?.image_path)
    }.sortedBy { it.name.lowercase() }

    // Hitung total kehadiran untuk semua user di divisi ini
    val allAttendeesInDivision = userProfiles.map { user ->
        val presensi = presensiData.find { it.id_user_profile == user.id_user_profile }
        presensi?.kehadiran ?: "Alpha"
    }

    val hadirCount = allAttendeesInDivision.count { it.equals("Hadir", ignoreCase = true) }
    val izinCount = allAttendeesInDivision.count { it.equals("Izin", ignoreCase = true) }
    val sakitCount = allAttendeesInDivision.count { it.equals("Sakit", ignoreCase = true) }
    val alphaCount = allAttendeesInDivision.count { it.equals("Alpha", ignoreCase = true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        if (isLoading) {
            // Loading indicator
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = PrimaryColor)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Memuat data sesi...",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        } else if (error != null) {
            // Error state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Terjadi kesalahan",
                        color = RedColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error!!,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // Reset states dan muat ulang data
                            isLoading = true
                            error = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                    ) {
                        Text("Coba Lagi", color = Color.White)
                    }
                }
            }
        } else {
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
                            text = "Detail Sesi${sesiData?.let { " - ${it.divisi}" } ?: ""}",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Session Details
                sesiData?.let { sesi ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                            .padding(horizontal = 30.dp),
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
                                Icon(
                                    imageVector = Icons.Default.Book,
                                    contentDescription = "Session",
                                    modifier = Modifier.size(40.dp),
                                    tint = bookColor
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${sesi.nama_materi}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black,
                                        lineHeight = 16.sp
                                    )
                                    Text(
                                        text = "Pertemuan ${sesi.pertemuan}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Gray,
                                        lineHeight = 14.sp
                                    )
                                    Text(
                                        text = "${sesi.jenis_sesi} - ${sesi.waktu_masuk}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.Gray,
                                        lineHeight = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Role Selector (Anggota/Pengurus)
                RoleSelector(
                    selectedRole = selectedRole,
                    onRoleSelected = { selectedRole = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Attendees List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 6.dp)
                        .padding(horizontal = 30.dp)
                        .padding(bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(attendees) { attendee ->
                        // sort by name
                        val context = LocalContext.current

                        AttendeeListItem(attendee = attendee, onClick = {
                            if (!attendee.imagePath.isNullOrBlank()) {
                                selectedImageUrl = attendee.imagePath
                                showImageDialog = true
                            } else {
                                Toast.makeText(context, "Tidak ada foto bukti", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }

                    if (attendees.isEmpty() && !isLoading) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Tidak ada $selectedRole di divisi ${sesiData?.divisi ?: ""}",
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Floating AttendanceBarWithLegend dengan total kehadiran divisi
            if (sesiData != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                        .padding(bottom = 20.dp)
                        .align(Alignment.BottomCenter),
                    shape = RoundedCornerShape(15.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    AttendanceBarWithLegend(
                        hadirCount = hadirCount,
                        izinCount = izinCount,
                        alphaCount = alphaCount,
                        sakitCount = sakitCount,
                        divisi = sesiData!!.divisi,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
    if (showImageDialog && selectedImageUrl != null) {
        Dialog(onDismissRequest = { showImageDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                Box {
                    AsyncImage(
                        model = selectedImageUrl,
                        contentDescription = "Presensi Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp, max = 400.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    // Tombol Close (X) di kanan atas
                    IconButton(
                        onClick = { showImageDialog = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(RedColor.copy(alpha = 0.6f), CircleShape)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoleSelector(
    selectedRole: String,
    onRoleSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val roles = listOf("anggota", "pengurus")

        roles.forEachIndexed { index, role ->
            val isSelected = selectedRole == role
            val buttonModifier = Modifier
                .weight(1f)
                .padding(
                    start = if (index > 0) 4.dp else 0.dp,
                    end = if (index < roles.size - 1) 4.dp else 0.dp
                )

            if (isSelected) {
                Button(
                    onClick = { onRoleSelected(role) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = buttonModifier
                ) {
                    Text(text = role, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { onRoleSelected(role) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = PrimaryColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = buttonModifier,
                    border = BorderStroke(1.dp, PrimaryColor)
                ) {
                    Text(text = role, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AttendeeListItem(
    attendee: AttendeeItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
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
                        tint = Color.White,
                        modifier = Modifier.size(25.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = attendee.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = attendee.id,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(attendee.statusColor, RoundedCornerShape(100))
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text(
                    text = attendee.status,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun AttendanceBarWithLegend(
    hadirCount: Int,
    izinCount: Int,
    alphaCount: Int,
    sakitCount: Int,
    divisi: String,
    modifier: Modifier = Modifier,
    height: Dp = 20.dp,
    cornerRadius: Dp = 10.dp
) {
    val total = (hadirCount + izinCount + alphaCount + sakitCount).takeIf { it > 0 } ?: 1

    val hadirPercent = hadirCount / total.toFloat()
    val izinPercent = izinCount / total.toFloat()
    val alphaPercent = alphaCount / total.toFloat()
    val sakitPercent = sakitCount / total.toFloat()

    Column(modifier = modifier) {
        // Title
        Text(
            text = "Rekap Kehadiran Divisi $divisi",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Bar horizontal
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(cornerRadius))
                .background(Color(0xFFE0E0E0))
        ) {
            if (hadirPercent > 0f) {
                Box(
                    modifier = Modifier
                        .weight(hadirPercent)
                        .fillMaxHeight()
                        .background(GreenColor)
                )
            }
            if (izinPercent > 0f) {
                Box(
                    modifier = Modifier
                        .weight(izinPercent)
                        .fillMaxHeight()
                        .background(PrimaryColor)
                )
            }
            if (sakitPercent > 0f) {
                Box(
                    modifier = Modifier
                        .weight(sakitPercent)
                        .fillMaxHeight()
                        .background(RedColor)
                )
            }
            if (alphaPercent > 0f) {
                Box(
                    modifier = Modifier
                        .weight(alphaPercent)
                        .fillMaxHeight()
                        .background(GrayColor)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Legend / deskripsi warna dan jumlah
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LegendItem(color = GreenColor, label = "Hadir", count = hadirCount)
            LegendItem(color = PrimaryColor, label = "Izin", count = izinCount)
            LegendItem(color = RedColor, label = "Sakit", count = sakitCount)
            LegendItem(color = GrayColor, label = "Alpha", count = alphaCount)
        }

        // Total
        Text(
            text = "Total: $total anggota",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp, start = 14.dp)
        )
    }
}

@Composable
fun LegendItem(
    color: Color,
    label: String,
    count: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(color, shape = RoundedCornerShape(100))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$count $label",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}