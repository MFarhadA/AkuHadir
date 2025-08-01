package my.kelompok3.akuhadir.data.model

import androidx.compose.ui.graphics.Color

data class SessionItem(
    val id: Int,
    val title: String,
    val meeting: String,
    val status: String,
    val statusColor: Color
)