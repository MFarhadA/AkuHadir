package my.kelompok3.akuhadir

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import my.kelompok3.akuhadir.ui.screens.HomeScreen
import my.kelompok3.akuhadir.ui.screens.LoginScreen
import my.kelompok3.akuhadir.ui.screens.ProfileScreen
import my.kelompok3.akuhadir.ui.screens.RegisterScreen
import my.kelompok3.akuhadir.ui.screens.SplashScreen
import my.kelompok3.akuhadir.ui.theme.AkuHadirTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AkuHadirTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AkuHadirApp()
                }
            }
        }
    }
}

@Composable
fun AkuHadirApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onNavigateToLogin = { navController.navigate("login") }
            )
        }
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToHome = { navController.navigate("home") }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }
        composable("profile") {
            ProfileScreen(
                onNavigateToHome = { navController.navigate("home") }
            )
        }
        composable("home") {
            HomeScreen(
                onNavigateToSessions = { navController.navigate("sessions") },
                onNavigateToMembers = { navController.navigate("members") }
            )
        }
        /*
        composable("sessions") {
            SessionsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddSession = { navController.navigate("add_session") }
            )
        }
        composable("members") {
            MembersScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("add_session") {
            AddSessionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        */
    }
}