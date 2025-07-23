package my.kelompok3.akuhadir

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import my.kelompok3.akuhadir.data.model.SesiData
import my.kelompok3.akuhadir.ui.screens.*
import my.kelompok3.akuhadir.ui.theme.AkuHadirTheme
import java.net.URLEncoder


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
                onNavigateToHome = { navController.navigate("home") {
//                        Hapus semua halaman sampai ke 'login', termasuk 'login' itu sendiri
                    popUpTo("login") {
                        inclusive = true
                    }
                    }
                }
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
                onNavigateToSessionDetails = { title, meeting ->
                    val encodedTitle = Uri.encode(title)
                    val encodedMeeting = Uri.encode(meeting)
                    navController.navigate("sessionDetails/$encodedTitle/$encodedMeeting")
                },
                onNavigateToAddSession = { navController.navigate("add_session") },
                onNavigateToListSessions = { navController.navigate("list_sessions") },
                onNavigateToAttendance = { navController.navigate("attendance") },
                onNavigateToEditSession = { sesiData: SesiData ->
                    val sesiDataJson = Json.encodeToString(sesiData)
                    val encodedJson = URLEncoder.encode(sesiDataJson, "UTF-8")
                    navController.navigate("edit_session/$encodedJson")
                }
            )
        }
        // Removed the duplicate "session_details" composable
        composable("add_session") {
            AddSessionScreen(
                onNavigateBack = { navController.popBackStack() },
                onCreateSession = { navController.popBackStack() }
            )
        }
        composable("list_sessions") {
            ListSessionScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSessionDetails = { title, meeting ->
                    val encodedTitle = Uri.encode(title)
                    val encodedMeeting = Uri.encode(meeting)
                    navController.navigate("sessionDetails/$encodedTitle/$encodedMeeting")
                }
            )
        }
        composable("attendance") {
            AttendanceScreen(
                onNavigateBack = { navController.popBackStack() },
                onSubmitAttendance = { navController.popBackStack() }
            )
        }
        composable(
            route = "sessionDetails/{title}/{meeting}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("meeting") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val meeting = backStackEntry.arguments?.getString("meeting") ?: ""
            SessionDetailsScreen(
                title = title,
                meeting = meeting,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "edit_session/{sesiDataJson}",
            arguments = listOf(navArgument("sesiDataJson") { type = NavType.StringType})
        ) { backStackEntry ->
            val sesiDataJson = backStackEntry.arguments?.getString("sesiDataJson") ?: ""
            EditSessionScreen(
                sesiDataJson = sesiDataJson,
                onNavigateBack = { navController.popBackStack() },
                onUpdateSession = { navController.popBackStack() }
            )
        }
    }
}