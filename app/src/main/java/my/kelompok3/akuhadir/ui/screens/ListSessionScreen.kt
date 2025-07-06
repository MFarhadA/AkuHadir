package my.kelompok3.akuhadir.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.kelompok3.akuhadir.data.model.SessionItem
import my.kelompok3.akuhadir.ui.theme.*

// ListSessionScreen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListSessionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSessionDetails: (String) -> Unit
) {

    val sessions = listOf(
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Hadir", GreenColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Izin", PrimaryColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Alpha", RedColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Izin", PrimaryColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Izin", PrimaryColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Hadir", GreenColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Hadir", GreenColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Hadir", GreenColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Hadir", GreenColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Izin", PrimaryColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Izin", PrimaryColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Hadir", GreenColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Hadir", GreenColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Hadir", GreenColor),
        SessionItem("Pembelajaran UI/UX", "Pertemuan 15", "Hadir", GreenColor)
    )

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
                        text = "List Sesi",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(horizontal = 14.dp),
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
                        text = session.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        lineHeight = 16.sp
                    )
                    Text(
                        text = session.meeting,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        lineHeight = 14.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(session.statusColor, RoundedCornerShape(100))
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text(
                    text = session.status,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}