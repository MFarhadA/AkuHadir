// SessionAvailableCard.kt
package my.kelompok3.akuhadir.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.kelompok3.akuhadir.ui.theme.GrayColor
import my.kelompok3.akuhadir.ui.theme.GreenColor
import my.kelompok3.akuhadir.ui.theme.RedColor

@Composable
fun SessionOwnerCard(
    title: String,
    meeting: String,
    onEditSession: () -> Unit,
    onCloseSession: () -> Unit,
    onViewParticipants: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 16.sp,
                color = Color.Black
            )
            Text(
                text = meeting,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(9.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Clickable Icon Edit Sesi
                IconButton(
                    onClick = onEditSession,
                    modifier = Modifier
                        .height(40.dp),
                    colors = IconButtonDefaults.iconButtonColors(containerColor = GrayColor),
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Session",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }



                // Tombol Tutup Sesi
                Button(
                    onClick = onCloseSession,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RedColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RemoveCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tutup sesi",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Icon Lihat Peserta
                IconButton(
                    onClick = onViewParticipants,
                    modifier = Modifier
                        .height(40.dp),
                    colors = IconButtonDefaults.iconButtonColors(containerColor = GrayColor),
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Peserta",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp) // ukuran ikon bisa disesuaikan
                    )
                }
            }
        }
    }
}