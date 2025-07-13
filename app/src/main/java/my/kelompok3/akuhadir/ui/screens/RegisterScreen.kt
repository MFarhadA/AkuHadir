package my.kelompok3.akuhadir.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.kelompok3.akuhadir.ui.theme.*

// database
import my.kelompok3.akuhadir.data.model.SupabaseInstance
import my.kelompok3.akuhadir.data.model.User
import io.github.jan.supabase.postgrest.from

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.kelompok3.akuhadir.data.manager.UserRegistrationManager

//logika
import my.kelompok3.akuhadir.ui.logika.useDelayState
import kotlinx.serialization.Serializable
import org.mindrot.jbcrypt.BCrypt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToProfile: () -> Unit
) {

    val primaryColor = Color(0xFF6366F1)
    var email by remember { mutableStateOf("mfarhadainc@gmail.com") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    val  supabase = SupabaseInstance.client
    val context = LocalContext.current // Ambil context dari lingkungan Compose
    // Panggil fungsi delay

    val (isRegisterEnabled, triggerRegisterDelay) = useDelayState(20_000)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor) // Warna background abu-abu muda sesuai screenshot
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
                    text = "Daftar",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Email Field
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "E-mail",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

                // Password Field
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Password",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (showPassword) "Hide password" else "Show password"
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = Gray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password Field
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Konfirmasi password",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(
                                    imageVector = if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (showConfirmPassword) "Hide password" else "Show password"
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = Gray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                val coroutineScope = rememberCoroutineScope()

                // Register Button
                Button(
                    onClick = {
                        triggerRegisterDelay() // Mulai jeda tombol
                        coroutineScope.launch {
                            if (password == confirmPassword) {
                                // Hash password sebelum menyimpan

                                val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt()) // menggunakan pw yang sudah di enkrip

                                val newUser = User(email = email, password = hashedPassword)
                                val pengecekan = supabase.from("user").select().decodeList<User>()
                                val isUserRegistered = pengecekan.any { it.email == newUser.email }

                                if (isUserRegistered) {
                                    Toast.makeText(context, "Pengguna sudah terdaftar", Toast.LENGTH_SHORT).show()
                                    onNavigateToLogin()
                                } else {
                                    val user = User(email = email, password = hashedPassword) // Gunakan hashed password




                                    val response = withContext(Dispatchers.IO) {
                                        supabase.from("user").insert(user)



                                    }
                                    if (response.data != null) {
                                        // Ambil ID pengguna berdasarkan email yang baru didaftarkan
                                        val userResponse = supabase.from("user").select {
                                            filter {
                                                eq("email", email) // Filter berdasarkan email
                                            }
                                        }.decodeSingle<User>() // Ambil satu pengguna

                                        val currentUserId = userResponse.id_user // Ambil ID pengguna

                                        // Simpan data registrasi
                                        val isDataSaved = UserRegistrationManager.saveRegistrationData(email)

                                        Log.d("HomeScreen", "User: ${newUser.email}, ID: $currentUserId")
                                        print("ini datanya: ${newUser.email} dan ${newUser.password} dan ${currentUserId}")
                                        // data sudah masuk
                                        println("User data: ID=${currentUserId}")
                                        onNavigateToProfile()
                                        // kerika sudah pindah page data hila di page ini
                                        println("User data: ID=${currentUserId}")

                                    } else {
                                        println("Kesalahan saat menyisipkan pengguna")
                                    }
                                }
                            } else {
                                println("Password tidak cocok")
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
                        text = "Daftar",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                // Login Link
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Punya akun?",
                        color = PrimaryColor.copy(alpha = 0.7f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
