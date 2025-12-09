package com.example.timetoshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.timetoshop.screens.AddCameraScreen
import com.example.timetoshop.screens.CameraScreen
import com.example.timetoshop.screens.CreatePostScreen
import com.example.timetoshop.screens.CreateShopScreen
import com.example.timetoshop.screens.ExploreScreen
import com.example.timetoshop.screens.HomeScreen
import com.example.timetoshop.screens.LoginScreen
import com.example.timetoshop.screens.NearbyScreen
import com.example.timetoshop.screens.ProfileScreen
import com.example.timetoshop.screens.QueueScreen
import com.example.timetoshop.screens.ShopProfileScreen
import com.example.timetoshop.screens.SignUpScreen
import com.example.timetoshop.ui.theme.TimeToShopTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimeToShopTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Explore.route,
        Screen.Nearby.route,
        Screen.Profile.route,
        Screen.Queue.route
    )

    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
        Screen.Home.route
    } else {
        "login"
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = startDestination, Modifier.padding(innerPadding)) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.Explore.route) { ExploreScreen() }
            composable(Screen.Nearby.route) { NearbyScreen() }
            composable(Screen.Profile.route) { ProfileScreen(navController) }
            composable(Screen.Queue.route) { QueueScreen() }
            composable("login") { LoginScreen(navController) }
            composable("signup") { SignUpScreen(navController) }
            composable("create_shop") { CreateShopScreen(navController) }
            composable("shop_profile") { ShopProfileScreen(navController) }
            composable("camera") { CameraScreen() }
            composable(
                route = "add_camera/{shopId}",
                arguments = listOf(navArgument("shopId") { type = NavType.StringType })
            ) { backStackEntry ->
                val shopId = backStackEntry.arguments?.getString("shopId") ?: ""
                AddCameraScreen(navController = navController, shopId = shopId)
            }
            composable(
                route = "create_post/{shopId}",
                arguments = listOf(navArgument("shopId") { type = NavType.StringType })
            ) { backStackEntry ->
                val shopId = backStackEntry.arguments?.getString("shopId") ?: ""
                CreatePostScreen(navController = navController, shopId = shopId)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Explore,
        Screen.Queue,
        Screen.Nearby,
        Screen.Profile
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Explore : Screen("explore", "Explore", Icons.Default.Explore)
    object Queue : Screen("queue", "Queue", Icons.Default.People)
    object Nearby : Screen("nearby", "Nearby", Icons.Default.Place)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TimeToShopTheme {
        MainScreen()
    }
}
