package my.kelompok3.akuhadir.data.model

import androidx.compose.ui.graphics.Color

data class AttendeeItem(
    val name: String,
    val id: String,
    val status: String,
    val statusColor: Color,
    val imagePath: String? = null
)