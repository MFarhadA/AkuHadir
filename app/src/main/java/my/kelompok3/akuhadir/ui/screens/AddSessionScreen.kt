package my.kelompok3.akuhadir.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AddSessionScreen(
    onNavigateBack: () -> Unit,
    onCreateSession: () -> Unit
) {
    val primaryColor = Color(0xFF6366F1)
    val greenColor = Color(0xFF10B981)
    val redColor = Color(0xFFEF4444)
    val backgroundColor = Color(0xFFF3F4F6)

    var selectedCategory by remember { mutableStateOf("Hardware") }
    var subjectName by remember { mutableStateOf("") }
    var meetingNumber by remember { mutableStateOf("") }
    var meetingTime by remember { mutableStateOf("") }
    var meetingLink by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf("Offline") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = primaryColor),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tambah Sesi",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Form Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Category Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryChip(
                        text = "Hardware",
                        isSelected = selectedCategory == "Hardware",
                        onClick = { selectedCategory = "Hardware" },
                        modifier = Modifier.weight(1f)
                    )
                    CategoryChip(
                        text = "Software",
                        isSelected = selectedCategory == "Software",
                        onClick = { selectedCategory = "Software" },
                        modifier = Modifier.weight(1f)
                    )
                    CategoryChip(
                        text = "Game",
                        isSelected = selectedCategory == "Game",
                        onClick = { selectedCategory = "Game" },
                        color = redColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Subject Name
                InputField(
                    label = "Nama Materi",
                    value = subjectName,
                    onValueChange = { subjectName = it },
                    placeholder = "Pembelajaran UI/UX"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Meeting Number
                InputField(
                    label = "Pertemuan ke-",
                    value = meetingNumber,
                    onValueChange = { meetingNumber = it },
                    placeholder = "9"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Meeting Time
                InputField(
                    label = "Waktu Masuk",
                    value = meetingTime,
                    onValueChange = { meetingTime = it },
                    placeholder = "17:30",
                    trailingIcon = Icons.Default.AccessTime
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mode Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModeChip(
                        text = "Offline",
                        isSelected = selectedMode == "Offline",
                        onClick = { selectedMode = "Offline" },
                        modifier = Modifier.weight(1f)
                    )
                    ModeChip(
                        text = "Online",
                        isSelected = selectedMode == "Online",
                        onClick = { selectedMode = "Online" },
                        color = primaryColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Meeting Link
                InputField(
                    label = "Link meet",
                    value = meetingLink,
                    onValueChange = { meetingLink = it },
                    placeholder = "meet.google.com/eqr-rtrr-qrq",
                    trailingIcon = Icons.Default.Link
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Create Session Button
                Button(
                    onClick = onCreateSession,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Buka Sesi",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}