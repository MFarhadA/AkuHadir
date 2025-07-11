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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailsScreen(
    title: String,
    meeting: String,
    onNavigateBack: () -> Unit
) {

    val attendees = listOf(
        AttendeeItem("Prabowo Subianto", "2355201063", "Hadir", GreenColor),
        AttendeeItem("Prabowo Subianto", "2355201063", "Izin", PrimaryColor),
        AttendeeItem("Prabowo Subianto", "2355201063", "Alpha", GrayColor),
        AttendeeItem("Prabowo Subianto", "2355201063", "Hadir", GreenColor),
        AttendeeItem("Prabowo Subianto", "2355201063", "Hadir", GreenColor),
        AttendeeItem("Prabowo Subianto", "2355201063", "Hadir", GreenColor),
        AttendeeItem("Prabowo Subianto", "2355201063", "Sakit", RedColor),
        AttendeeItem("Prabowo Subianto", "2355201063", "Hadir", GreenColor)
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
                        text = "Detail Sesi",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Session Details
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
                            contentDescription = "Person",
                            modifier = Modifier.size(40.dp),
                            tint = RedColor
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
                }
            }

            // Attendees Type (Anggota/Pengurus)
            AttendeeTypeSelector()

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
                    AttendeeListItem(attendee = attendee)
                }
            }
        }

        // Floating AttendanceBarWithLegend dengan Card dan shadow
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
                hadirCount = attendees.count { it.status.equals("Hadir", ignoreCase = true) },
                izinCount = attendees.count { it.status.equals("Izin", ignoreCase = true) },
                alphaCount = attendees.count { it.status.equals("Alpha", ignoreCase = true) },
                sakitCount = attendees.count { it.status.equals("Sakit", ignoreCase = true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun AttendeeTypeSelector() {
    var selectedType by remember { mutableStateOf("Anggota") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val types = listOf("Anggota", "Pengurus")

        types.forEachIndexed { index, type ->
            val isSelected = selectedType == type
            val buttonModifier = Modifier
                .weight(1f)
                .padding(
                    start = if (index > 0) 4.dp else 0.dp,
                    end = if (index < types.size - 1) 4.dp else 0.dp
                )

            if (isSelected) {
                Button(
                    onClick = { selectedType = type },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = buttonModifier
                ) {
                    Text(text = type, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { selectedType = type },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = PrimaryColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = buttonModifier
                ) {
                    Text(text = type, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AttendeeListItem(
    attendee: AttendeeItem
) {
    Card(
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