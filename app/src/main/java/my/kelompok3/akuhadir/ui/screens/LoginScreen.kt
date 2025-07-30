package my.kelompok3.akuhadir.ui.screens

import android.R
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextField
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.kelompok3.akuhadir.data.manager.UserRegistrationManager

// logika dan keamanan
import my.kelompok3.akuhadir.ui.logika.useDelayState
import kotlinx.serialization.Serializable
import my.kelompok3.akuhadir.data.model.AuthManager
import org.mindrot.jbcrypt.BCrypt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val supabase = SupabaseInstance.client
    val context = LocalContext.current // Ambil context dari lingkungan Compose
    // Panggil fungsi delay

    val (isRegisterEnabled, triggerRegisterDelay) = useDelayState(15_000)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
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

            // Konten login di tengah dengan padding horizontal
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Login",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Email label dan field
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
                        shape = RoundedCornerShape(15.dp),
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

                // Password label dan field
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
                        shape = RoundedCornerShape(15.dp),
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

                Spacer(modifier = Modifier.height(32.dp))
                val coroutineScope = rememberCoroutineScope()

                Button(
                    onClick = {
                        triggerRegisterDelay() // Mulai jeda tombol
                        coroutineScope.launch {
                            val authManager = AuthManager()

                            // Lakukan login dan ambil hasilnya
                            val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt()) // menggunakan pw yang sudah di enkrip

                            val (isLoginSuccessful, currentUserId) = authManager.login(email, password)
                            val newUser = User(email = email, password = hashedPassword)


                            if (isLoginSuccessful) {
                                // Dapatkan email dari UserRegistrationManager jika diperlukan
                                val currentUserEmail = UserRegistrationManager.getCurrentUserEmail()
                                // Ambil ID pengguna berdasarkan email yang baru didaftarkan
                                val userResponse = supabase.from("user").select {
                                    filter {
                                        eq("email", email) // Filter berdasarkan email
                                    }
                                }.decodeSingle<User>() // Ambil satu pengguna

                                val currentUserId = userResponse.id_user // Ambil ID pengguna

                                // Simpan data login, termasuk ID pengguna
                                val isDataSaved = UserRegistrationManager.saveRegistrationData(email)
                                // pengetesan data
                                Log.d("HomeScreen", "User: ${newUser.email}, ID: $currentUserId")
                                print("ini datanya: ${newUser.email} dan ${newUser.password} dan ${currentUserId}")
                                // data sudah masuk
                                println("User data: ID=${currentUserId}")
                                onNavigateToHome()
                            } else {
                                // Tampilkan pesan kesalahan
                                Toast.makeText(context, "Email atau password salah", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },

                    enabled = isRegisterEnabled, // Gunakan state hasil useDelayState
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Text(
                        text = "Login",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                // Register Link
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "Daftar akun?",
                        color = PrimaryColor.copy(alpha = 0.7f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
