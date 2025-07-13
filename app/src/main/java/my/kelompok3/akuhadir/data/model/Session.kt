package my.kelompok3.akuhadir.data.model

data class Session(
    val id: String = "",
    val title: String = "",
    val meeting: String = "",
    val date: String = "",
    val time: String = "",
    val status: AttendanceStatus = AttendanceStatus.ALPHA,
    val isOnline: Boolean = false,
    val meetLink: String = "",
    val room: String = ""
)

