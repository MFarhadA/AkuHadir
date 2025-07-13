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
import my.kelompok3.akuhadir.ui.theme.*


// database
import my.kelompok3.akuhadir.data.model.SupabaseInstance
import my.kelompok3.akuhadir.data.model.User

import kotlinx.coroutines.Dispatchers
import my.kelompok3.akuhadir.data.manager.UserRegistrationManager

//logika

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit
) {

    var fullName by remember { mutableStateOf("Muhammad Farhad Ajilla") }
    var nim by remember { mutableStateOf("23552011063") }
    var selectedDivision by remember { mutableStateOf("Hardware") }
    var expanded by remember { mutableStateOf(false) }
    // database
    val  supabase = SupabaseInstance.client
    val context = LocalContext.current // Ambil context dari lingkungan Compose
    // Panggil fungsi delay
    val (isRegisterEnabled, triggerRegisterDelay) = useDelayState(20_000)
    val currentUserId = UserRegistrationManager.getCurrentUserId()


    val divisions = listOf("hardware", "software", "game")

    println("User data: ID=$currentUserId")

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
                    color = Black
                )

                Spacer(modifier = Modifier.height(1.dp))

                Text(
                    text = "Masukkan identitas anda terlebih dahulu",
                    fontSize = 14.sp,
                    color = Color.Gray
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
                        color = Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = Gray
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
                        color = Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = nim,
                        onValueChange = { nim = it },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = Gray
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
                        color = Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedDivision,
                            onValueChange = {selectedDivision=it.lowercase()},

                            readOnly = true,
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
                                focusedBorderColor = PrimaryColor,
                                unfocusedBorderColor = Gray
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            divisions.forEach { division ->
                                DropdownMenuItem(
                                    text = { Text(division) },
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
                val coroutineScope = rememberCoroutineScope()


                // Submit Button
                Button(
                    onClick =
                    {
                        triggerRegisterDelay() // Mulai jeda tombol
                        coroutineScope.launch {
                            if (nim != "" && fullName != "" && selectedDivision != "") {
                                // Mengambil ID pengguna yang sudah disimpan saat registrasi
                                val currentUserId = UserRegistrationManager.getCurrentUserId()


                                // Memeriksa apakah ID pengguna valid
                                if (currentUserId != null) {
                                    // Membuat objek UserProfile dengan ID pengguna
                                    val userProfile = User(nama = fullName, nim = nim.toIntOrNull() ?: 0, divisi = selectedDivision, id_user = currentUserId)

                                    // Memeriksa apakah pengguna sudah terdaftar di user_profile
                                    val pengecekan = supabase.from("user_profile").select().decodeList<User>()
                                    val existingUser = pengecekan.find { it.nim == userProfile.nim }

                                    if (existingUser != null) {
                                        Toast.makeText(context, "nim anda sama dengan yang sudah terdaftar", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // Menyimpan data pengguna ke user_profile
                                        val response = withContext(Dispatchers.IO) {
                                            supabase.from("user_profile").insert(userProfile)
                                        }

                                        if (response.data != null) {
                                            onNavigateToHome()
                                            Toast.makeText(context, "Pengisian profile berhasil disimpan", Toast.LENGTH_SHORT).show()

                                        } else {
                                            println("Kesalahan saat menyisipkan pengguna")
                                        }
                                    }
                                } else {
                                    println("ID pengguna tidak ditemukan")
                                    Toast.makeText(context, "Kesalahan: ID pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                println("Data belum di input")
                                Toast.makeText(context, "Kolom belum di isi", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = isRegisterEnabled, // Gunakan state hasil useDelayState
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Text(
                        text = "Masuk",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
