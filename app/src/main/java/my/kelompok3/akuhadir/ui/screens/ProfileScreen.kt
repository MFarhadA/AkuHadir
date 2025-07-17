package my.kelompok3.akuhadir.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.postgrest.from

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import my.kelompok3.akuhadir.ui.logika.useDelayState

// database
import my.kelompok3.akuhadir.data.model.SupabaseInstance
import my.kelompok3.akuhadir.data.model.User

// color
import my.kelompok3.akuhadir.ui.theme.PrimaryColor
import my.kelompok3.akuhadir.ui.theme.BackgroundColor

import kotlinx.coroutines.Dispatchers
import my.kelompok3.akuhadir.data.manager.UserRegistrationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit
) {
    // Definisi warna lokal untuk mengatasi unresolved reference
    val primaryColor = Color(0xFF2196F3) // Biru Material
    val backgroundColor = Color(0xFFF5F5F5) // Abu-abu terang
    val blackColor = Color(0xFF2F2F2F) // Hitam
    val grayColor = Color(0xFF9CA3AF) // Abu-abu

    // State variables untuk form inputs
    var fullName by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }
    var selectedDivision by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Database dan context
    val supabase = SupabaseInstance.client
    val context = LocalContext.current

    // Delay state untuk button
    val (isRegisterEnabled, triggerRegisterDelay) = useDelayState(20_000)

    // Coroutine scope
    val coroutineScope = rememberCoroutineScope()

    // Daftar divisi
    val divisions = listOf("hardware", "software", "game")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header biru dengan sudut bawah membulat
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(horizontal = 24.dp)
                    .background(
                        color = PrimaryColor,
                        shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aku\nHadir",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    color = Color.White,
                    modifier = Modifier.wrapContentWidth()
                        .padding(start = 0.dp),
                    lineHeight = 40.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sebelum Memulai",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = blackColor
                )

                Spacer(modifier = Modifier.height(1.dp))

                Text(
                    text = "Masukkan identitas anda terlebih dahulu",
                    fontSize = 14.sp,
                    color = grayColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Nama Lengkap Field
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Nama Lengkap",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = blackColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        placeholder = { Text("Masukkan nama lengkap") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = grayColor
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // NIM Field
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "NIM",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = blackColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = nim,
                        onValueChange = { nim = it },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        placeholder = { Text("Masukkan NIM") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = grayColor
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Divisi Field
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Divisi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = blackColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedDivision,
                            onValueChange = { },
                            readOnly = true,
                            placeholder = { Text("Pilih divisi") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = grayColor
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            divisions.forEach { division ->
                                DropdownMenuItem(
                                    text = { Text(division.replaceFirstChar { it.uppercase() }) },
                                    onClick = {
                                        selectedDivision = division
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Submit Button
                Button(
                    onClick = {
                        triggerRegisterDelay() // Mulai jeda tombol
                        coroutineScope.launch {
                            // Validasi input tidak kosong
                            if (nim.isNotEmpty() && fullName.isNotEmpty() && selectedDivision.isNotEmpty()) {
                                try {
                                    // Mengambil ID pengguna yang sudah disimpan saat registrasi
                                    val currentUserId = UserRegistrationManager.getCurrentUserId()

                                    // Memeriksa apakah ID pengguna valid
                                    if (currentUserId != null) {
                                        // Validasi NIM harus berupa angka
                                        val nimInt = nim.toIntOrNull()
                                        if (nimInt == null) {
                                            Toast.makeText(context, "NIM harus berupa angka", Toast.LENGTH_SHORT).show()
                                            return@launch
                                        }

                                        // Validasi panjang NIM (opsional, sesuaikan dengan kebutuhan)
                                        if (nim.length < 8) {
                                            Toast.makeText(context, "NIM harus minimal 8 digit", Toast.LENGTH_SHORT).show()
                                            return@launch
                                        }

                                        // Membuat objek UserProfile dengan ID pengguna
                                        val userProfile = User(
                                            nama = fullName.trim(),
                                            nim = nimInt,
                                            divisi = selectedDivision.lowercase(),
                                            id_user = currentUserId
                                        )

                                        println("Data yang akan diinsert: $userProfile")

                                        // Memeriksa apakah NIM sudah terdaftar di user_profile
                                        val pengecekan = withContext(Dispatchers.IO) {
                                            supabase.from("user_profile")
                                                .select()
                                                .decodeList<User>()
                                        }

                                        val existingUser = pengecekan.find { it.nim == userProfile.nim }

                                        if (existingUser != null) {
                                            Toast.makeText(context, "NIM sudah terdaftar, silakan gunakan NIM lain", Toast.LENGTH_SHORT).show()
                                        } else {
                                            // Menyimpan data pengguna ke user_profile
                                            val response = withContext(Dispatchers.IO) {
                                                supabase.from("user_profile").insert(userProfile)
                                            }

                                            // Cek apakah insert berhasil
                                            println("Insert response: $response")
                                            Toast.makeText(context, "Profil berhasil disimpan", Toast.LENGTH_SHORT).show()
                                            onNavigateToHome()
                                        }
                                    } else {
                                        println("ID pengguna tidak ditemukan")
                                        Toast.makeText(context, "Kesalahan: ID pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    println("Error saat menyimpan profil: ${e.message}")
                                    e.printStackTrace()
                                    Toast.makeText(context, "Terjadi kesalahan saat menyimpan profil: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = isRegisterEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRegisterEnabled) PrimaryColor else grayColor
                    )
                ) {
                    Text(
                        text = if (isRegisterEnabled) "Masuk" else "Tunggu...",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}