package my.kelompok3.akuhadir.ui.screens

import android.app.TimePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import my.kelompok3.akuhadir.data.model.Session
import my.kelompok3.akuhadir.data.model.SupabaseInstance
import my.kelompok3.akuhadir.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSessionScreen(
    onNavigateBack: () -> Unit,
    onCreateSession: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Hardware") }
    var subjectName by remember { mutableStateOf("Pembelajaran UI/UX") }
    var meetingNumber by remember { mutableStateOf("9") }
    var meetingTime by remember { mutableStateOf("17:30") }
    var meetingLink by remember { mutableStateOf("meet.google.com/etr-rtr-qrq") }
    var meetingLocation by remember { mutableStateOf("Ruang Lab Komputer") }
    var selectedMode by remember { mutableStateOf("Offline") }
    var showDropdown by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val supabase = SupabaseInstance.client
    val context = LocalContext.current
    val calendar = java.util.Calendar.getInstance()
    val scope = rememberCoroutineScope()

    // Time Picker Dialog
    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hour: Int, minute: Int ->
                val formattedTime = String.format("%02d:%02d", hour, minute)
                meetingTime = formattedTime
            },
            calendar.get(java.util.Calendar.HOUR_OF_DAY),
            calendar.get(java.util.Calendar.MINUTE),
            true
        )
    }

    // Function to insert session to Supabase
    fun insertSession() {
        if (subjectName.isEmpty() || meetingNumber.isEmpty() || meetingTime.isEmpty() ||
            selectedCategory.isEmpty() || selectedMode.isEmpty()) {
            Toast.makeText(context, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate meeting link for online mode
        if (selectedMode == "Online" && meetingLink.isEmpty()) {
            Toast.makeText(context, "Link meet harus diisi untuk sesi online", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate meeting location for offline mode
        if (selectedMode == "Offline" && meetingLocation.isEmpty()) {
            Toast.makeText(context, "Lokasi ruangan harus diisi untuk sesi offline", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true

        scope.launch {
            try {
                val sessionData = Session(
                    nama_materi = subjectName,
                    waktu_masuk = meetingTime,
                    divisi = selectedCategory,
                    jenis_sesi = selectedMode,
                    pertemuan = (meetingNumber.toIntOrNull() ?: 0),
                    link_meet = if(selectedMode == "Online") meetingLink else null,
                    ruangan = if (selectedMode == "Offline") meetingLocation else null
                )

                // Insert to Supabase
                supabase.from("sesi").insert(sessionData)

                withContext(Dispatchers.Main) {
                    isLoading = false
                    Toast.makeText(context, "Sesi berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    onCreateSession()
                    onNavigateBack()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.d("AddSessionScreen", "Error: ${e.message}")
                }
            }
        }
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
                        text = "Tambah Sesi",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Form Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Nama Materi
                Column {
                    Text(
                        text = "Nama Materi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = { subjectName = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = Gray,
                            focusedTextColor = Black,
                            unfocusedTextColor = Black
                        )
                    )
                }

                // Pertemuan ke- dan Waktu Masuk
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pertemuan ke-
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Pertemuan ke-",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = meetingNumber,
                            onValueChange = { meetingNumber = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(15.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = PrimaryColor,
                                unfocusedBorderColor = Gray,
                                focusedTextColor = Black,
                                unfocusedTextColor = Black
                            )
                        )
                    }

                    // Waktu Masuk
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Waktu Masuk",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = meetingTime,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            enabled = true,
                            interactionSource = remember { MutableInteractionSource() }
                                .also { interactionSource ->
                                    LaunchedEffect(interactionSource) {
                                        interactionSource.interactions.collect {
                                            if (it is PressInteraction.Release) {
                                                timePickerDialog.show()
                                            }
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(15.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = PrimaryColor,
                                unfocusedBorderColor = Gray,
                                focusedTextColor = Black,
                                unfocusedTextColor = Black
                            ),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "Time",
                                    tint = Color.Gray
                                )
                            }
                        )
                    }
                }

                // Divisi
                Column {
                    Text(
                        text = "Divisi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = showDropdown,
                        onExpandedChange = { showDropdown = !showDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(15.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = PrimaryColor,
                                unfocusedBorderColor = Gray,
                                focusedTextColor = Black,
                                unfocusedTextColor = Black
                            ),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = showDropdown
                                )
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false }
                        ) {
                            listOf("Hardware", "Software", "Game").forEach { category ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = category,
                                            color = Black
                                        )
                                    },
                                    onClick = {
                                        selectedCategory = category
                                        showDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .background(Color.White, shape = RoundedCornerShape(100.dp))
                )

                // Mode Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModeChipSession(
                        text = "Offline",
                        isSelected = selectedMode == "Offline",
                        onClick = { selectedMode = "Offline" },
                        primaryColor = PrimaryColor,
                        modifier = Modifier.weight(1f)
                    )
                    ModeChipSession(
                        text = "Online",
                        isSelected = selectedMode == "Online",
                        onClick = { selectedMode = "Online" },
                        primaryColor = PrimaryColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Link meet / Lokasi meet
                Column {
                    Text(
                        text = if (selectedMode == "Online") "Link meet" else "Lokasi meet",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = if (selectedMode == "Online") meetingLink else meetingLocation,
                        onValueChange = {
                            if (selectedMode == "Online") {
                                meetingLink = it
                            } else {
                                meetingLocation = it
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = Gray,
                            focusedTextColor = Black,
                            unfocusedTextColor = Black
                        )
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Buka Sesi Button
                Button(
                    onClick = { insertSession()
                              onNavigateBack()},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenColor),
                    shape = RoundedCornerShape(15.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "Buka Sesi",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModeChipSession(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) primaryColor else Color.White,
            contentColor = if (isSelected) Color.White else primaryColor
        ),
        shape = RoundedCornerShape(15.dp),
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, primaryColor) else null
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}