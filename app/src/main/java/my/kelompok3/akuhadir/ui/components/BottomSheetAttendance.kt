package my.kelompok3.akuhadir.ui.components

import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.kelompok3.akuhadir.ui.theme.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import my.kelompok3.akuhadir.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.Serializable
import my.kelompok3.akuhadir.data.model.IdSesiResponse
import my.kelompok3.akuhadir.data.model.PresensiInsert
import my.kelompok3.akuhadir.data.model.Sesi
import my.kelompok3.akuhadir.data.model.SesiData
import my.kelompok3.akuhadir.data.model.SupabaseClient
import my.kelompok3.akuhadir.data.model.SupabaseInstance
import java.io.InputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceBottomSheet(
    userProfile: UserProfile?,
    onDismiss: () -> Unit,
    onSubmitAttendance: (status: String) -> Unit
) {

    Log.d("AttendanceBottomSheet", "Bottom sheet opened")
    Log.d("AttendanceBottomSheet", "userId: $userProfile")


    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var activeSessionIds by remember { mutableStateOf<List<Int>>(emptyList()) }

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedStatus by remember { mutableStateOf("Hadir") }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    var isAvailable = activeSessionIds.isNotEmpty()

    // Menggunakan SupabaseClient yang sudah ada
    val supabase = SupabaseInstance.client

    // Launcher untuk ambil foto dengan kamera (TakePicture dengan URI)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            photoUri = tempCameraUri
            Log.d("AttendanceBottomSheet", "Camera success, URI: $tempCameraUri")
        } else {
            tempCameraUri = null
            Log.d("AttendanceBottomSheet", "Camera failed or cancelled")
        }
    }

    // Launcher untuk pilih foto dari galeri (GetContent)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUri = uri
        }
    }

    // Launcher untuk meminta permission kamera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageUri(context)
            if (uri != null) {
                tempCameraUri = uri
                cameraLauncher.launch(uri)
                Log.d("AttendanceBottomSheet", "Launching camera with URI: $uri")
            } else {
                Log.e("AttendanceBottomSheet", "Failed to create image URI")
            }
        } else {
            Log.e("AttendanceBottomSheet", "Camera permission denied")
        }
    }

    // Fungsi untuk upload gambar ke Supabase Storage
    suspend fun uploadImageToSupabase(uri: Uri): String? {
        return try {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("Cannot open input stream")

            val fileName = "attendance_${UUID.randomUUID()}_${System.currentTimeMillis()}.jpg"
            val filePath = "public/$fileName"

            val bucket = supabase.storage.from("bukti-presensi")

            // Upload file
            bucket.upload(filePath, inputStream.readBytes())

            // Get public URL
            val publicUrl = bucket.publicUrl(filePath)

            Log.d("AttendanceBottomSheet", "Image uploaded successfully: $publicUrl")
            publicUrl
        } catch (e: Exception) {
            Log.e("AttendanceBottomSheet", "Error uploading image", e)
            null
        }
    }

    // Fungsi untuk mendapatkan sesi aktif berdasarkan divisi user yang belum ada presensinya
    suspend fun getActiveSessionsNotYetAttendedByUser(
        divisi: String,
        idUserProfile: Int
    ): List<Int> {
        isAvailable = false
        return try {
            // Ambil semua sesi aktif divisi tertentu dengan filter
            val sesiList = supabase.from("sesi")
                .select {
                    filter {
                        ilike("divisi", divisi.lowercase()) // Menggunakan eq untuk kecocokan yang tepat
                        eq("Keterangan", "berjalan")
                    }
                }
                .decodeList<Sesi>()

            if (sesiList.isEmpty()) {
                Log.w("AttendanceBottomSheet", "No active sessions found for division: $divisi")
                return emptyList()
            }

            // Ambil semua id_sesi yang sudah user presensi
            val attendedSessions = supabase.from("presensi")
                .select {
                    filter {
                        eq("id_user_profile", idUserProfile)
                    }
                }
                .decodeList<IdSesiResponse>() // Gunakan data class khusus
                .map { it.id_sesi }

            // Filter sesi yang belum ada di attendedSessions
            val filteredSessions = sesiList.filter { it.id_sesi !in attendedSessions }

            if (filteredSessions.isEmpty()) {
                Log.w("AttendanceBottomSheet", "No active sessions not yet attended by user: $idUserProfile")
                return emptyList()
            }

            isAvailable = true

            Log.d("AttendanceBottomSheet", "Is available: $isAvailable")

            Log.d("AttendanceBottomSheet", "Filtered sessions count: ${filteredSessions.size}")

            filteredSessions.map { it.id_sesi }
        } catch (e: Exception) {
            Log.e("AttendanceBottomSheet", "Error fetching or filtering sessions", e)
            emptyList()
        }
    }

    LaunchedEffect(userProfile) {
        if (userProfile != null) {
            activeSessionIds = getActiveSessionsNotYetAttendedByUser(userProfile.divisi, userProfile.id_user_profile)
        }
    }

    // Fungsi untuk menyimpan presensi ke database
    suspend fun savePresensiToDatabase(
        idUserProfile: Int,
        kehadiran: String,
        imagePath: String,
        idSesi: Int
    ): Boolean {
        return try {
            val presensiData = PresensiInsert(
                id_user_profile = idUserProfile,
                kehadiran = kehadiran,
                image_path = imagePath,
                id_sesi = idSesi
            )

            supabase.from("presensi").insert(presensiData)

            Log.d("AttendanceBottomSheet", "Presensi saved successfully")
            true
        } catch (e: Exception) {
            Log.e("AttendanceBottomSheet", "Error saving presensi", e)
            false
        }
    }

    // Fungsi untuk submit presensi
    fun submitAttendance() {
        if (userProfile == null) {
            Log.e("AttendanceBottomSheet", "User profile is null")
            return
        }

        if (photoUri == null) {
            Log.e("AttendanceBottomSheet", "No photo selected")
            Toast.makeText(context, "Harap upload bukti presensi", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isAvailable) {
            Toast.makeText(context, "Tidak ada sesi yang tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        isSubmitting = true

        coroutineScope.launch {
            try {
                val imageUrl = withContext(Dispatchers.IO) {
                    uploadImageToSupabase(photoUri!!)
                }

                if (imageUrl == null) {
                    Log.e("AttendanceBottomSheet", "Failed to upload image")
                    Toast.makeText(context, "Gagal upload bukti presensi", Toast.LENGTH_SHORT).show()
                    isSubmitting = false
                    return@launch
                }

                var allSuccessful = true

                for (sessionId in activeSessionIds) {
                    val success = withContext(Dispatchers.IO) {
                        savePresensiToDatabase(
                            idUserProfile = userProfile.id_user_profile,
                            kehadiran = selectedStatus,
                            imagePath = imageUrl,
                            idSesi = sessionId
                        )
                    }

                    if (!success) {
                        Log.e("AttendanceBottomSheet", "Failed to save attendance for session ID: $sessionId")
                        allSuccessful = false
                    }
                }

                if (allSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Berhasil melakukan presensi untuk ${activeSessionIds.size} sesi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Log.d("AttendanceBottomSheet", "Attendance submitted successfully for all sessions")
                    onSubmitAttendance(selectedStatus)
                    onDismiss()
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Sebagian presensi gagal disimpan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("AttendanceBottomSheet", "Error in submitAttendance", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Terjadi kesalahan: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } finally {
                isSubmitting = false
            }
        }
    }

    LaunchedEffect(photoUri) {
        photoUri?.let {
            Log.d("AttendanceBottomSheet", "Photo URI: $it")
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = BackgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Bottom sheet indicator
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Submit Kehadiran",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Photo Upload Area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(photoUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Foto Bukti Kehadiran",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            onLoading = {
                                Log.d("AttendanceBottomSheet", "Image loading: $photoUri")
                            },
                            onSuccess = {
                                Log.d("AttendanceBottomSheet", "Image loaded successfully")
                            },
                            onError = { error ->
                                Log.e("AttendanceBottomSheet", "Image load error: ${error.result.throwable}")
                            }
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = PrimaryColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Foto Bukti Kehadiran",
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryColor,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Photo Upload Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(15.dp),
                    enabled = !isSubmitting
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Upload Foto",
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = {
                        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    shape = RoundedCornerShape(15.dp),
                    enabled = !isSubmitting
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ambil Foto",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(Color.White, shape = RoundedCornerShape(100.dp))
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Status Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusChip(
                    text = "Hadir",
                    isSelected = selectedStatus == "Hadir",
                    onClick = { selectedStatus = "Hadir" },
                    color = GreenColor,
                    modifier = Modifier.weight(1f),
                    enabled = !isSubmitting
                )
                StatusChip(
                    text = "Izin",
                    isSelected = selectedStatus == "Izin",
                    onClick = { selectedStatus = "Izin" },
                    color = PrimaryColor,
                    modifier = Modifier.weight(1f),
                    enabled = !isSubmitting
                )
                StatusChip(
                    text = "Sakit",
                    isSelected = selectedStatus == "Sakit",
                    onClick = { selectedStatus = "Sakit" },
                    color = RedColor,
                    modifier = Modifier.weight(1f),
                    enabled = !isSubmitting
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(
                onClick = { submitAttendance() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenColor),
                shape = RoundedCornerShape(15.dp),
                enabled = !isSubmitting && photoUri != null && isAvailable
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mengirim...",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = if (isAvailable) "Submit Kehadiran" else "Tidak ada sesi aktif",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun createImageUri(context: android.content.Context): Uri? {
    return try {
        val contentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "attendance_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/AkuHadir")
            }
        }
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        Log.d("AttendanceBottomSheet", "Created URI: $uri")
        uri
    } catch (e: Exception) {
        Log.e("AttendanceBottomSheet", "Error creating image URI", e)
        null
    }
}

@Composable
fun StatusChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color else Color.White,
            contentColor = if (isSelected) Color.White else color
        ),
        shape = RoundedCornerShape(15.dp),
        enabled = enabled
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}