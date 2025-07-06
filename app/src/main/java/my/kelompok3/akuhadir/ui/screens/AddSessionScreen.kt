package my.kelompok3.akuhadir.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                            unfocusedBorderColor = Gray
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
                                unfocusedBorderColor = Gray
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
                            onValueChange = { meetingTime = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(15.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = PrimaryColor,
                                unfocusedBorderColor = Gray
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
                                unfocusedBorderColor = Gray
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
                            listOf("Hardware", "Software", "Game", "Pengurus").forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        selectedCategory = category
                                        showDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

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
                        primaryColor = my.kelompok3.akuhadir.ui.theme.PrimaryColor,
                        modifier = Modifier.weight(1f)
                    )
                    ModeChipSession(
                        text = "Online",
                        isSelected = selectedMode == "Online",
                        onClick = { selectedMode = "Online" },
                        primaryColor = my.kelompok3.akuhadir.ui.theme.PrimaryColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Link meet / Lokasi meet (berubah berdasarkan mode)
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
                            unfocusedBorderColor = Gray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Buka Sesi Button
                Button(
                    onClick = onCreateSession,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenColor),
                    shape = RoundedCornerShape(15.dp)
                ) {
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