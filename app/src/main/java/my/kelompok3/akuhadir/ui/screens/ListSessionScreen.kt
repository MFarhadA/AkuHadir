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
import my.kelompok3.akuhadir.data.model.SessionItem

// ListSessionScreen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListSessionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSessionDetails: (String) -> Unit
) {
    val primaryColor = Color(0xFF6366F1)
    val greenColor = Color(0xFF10B981)
    val redColor = Color(0xFFEF4444)
    val grayColor = Color(0xFF9CA3AF)
    val backgroundColor = Color(0xFFF3F4F6)

    val sessions = listOf(
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Hadir", greenColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Izin", primaryColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Alpha", redColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Izin", primaryColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Izin", primaryColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Hadir", greenColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Hadir", greenColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Hadir", greenColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Hadir", greenColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Izin", primaryColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Izin", primaryColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Hadir", greenColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Hadir", greenColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Hadir", greenColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Hadir", greenColor)
    )

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
                        text = "List Sesi",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Session List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sessions) { session ->
                    SessionListItem(
                        session = session,
                        onNavigateToSessionDetails = { onNavigateToSessionDetails(session.title) }
                    )
                }
            }
        }
    }
}

@Composable
fun SessionListItem(
    session: SessionItem,
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
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(40.dp)
                        .background(Color.Red, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = session.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = session.meeting,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(session.statusColor, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = session.status,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}