package my.kelompok3.akuhadir.ui.logika

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * State reusable untuk menonaktifkan tombol selama [delayMillis] setelah dipicu.
 *
 * Cara pakai:
 * val (isEnabled, triggerDelay) = useDelayState(20000)
 */
@Composable
fun useDelayState(delayMillis: Long = 20_000): Pair<Boolean, () -> Unit> {
    var isEnabled by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val triggerDelay: () -> Unit = {
        if (isEnabled) {
            isEnabled = false
            scope.launch {
                delay(delayMillis)
                isEnabled = true
            }
        }
    }

    return Pair(isEnabled, triggerDelay)
}
// Cara MENGGUNAKAN useDelayState
// import my.kelompok3.akuhadir.ui.logika.useDelayState // di gunakan untuk mengimportnya

//val (isRegisterEnabled, triggerRegisterDelay) = useDelayState(20_000) // cara mendefinisikannya sekaligus mengatur waktu delaynya

//triggerRegisterDelay() // Mulai jeda tombol di gunakan di paling atas sebelum logika penyimpanan di mulai
//enabled = isRegisterEnabled, // Gunakan state hasil useDelayState di gunakan setelah logika penyimpanan selesai