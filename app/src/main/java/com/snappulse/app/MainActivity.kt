package com.snappulse.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.snappulse.app.data.repository.PermissionsManager
import com.snappulse.app.navigation.Screen
import com.snappulse.app.ui.camera.SnapCameraScreen
import com.snappulse.app.ui.chat.ChatDetailScreen
import com.snappulse.app.ui.chat.ChatListScreen
import com.snappulse.app.ui.components.SnapBottomBar
import com.snappulse.app.ui.map.SnapMapScreen
import com.snappulse.app.ui.permissions.PermissionOnboardingScreen
import com.snappulse.app.ui.profile.ProfileScreen
import com.snappulse.app.ui.theme.SnapBlack
import com.snappulse.app.ui.theme.SnapPulseTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SnapPulseTheme {
                MainAppNavigation()
            }
        }
    }
}

@Composable
fun MainAppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check if initial permissions are already granted
    val context = androidx.compose.ui.platform.LocalContext.current
    val hasInitialPermissions = PermissionsManager.hasAllCorePermissions(context)
    val startDestination = if (hasInitialPermissions) Screen.SnapMap.route else Screen.Permissions.route

    val showBottomBar = currentRoute in listOf(
        Screen.SnapMap.route,
        Screen.ChatList.route,
        Screen.Camera.route,
        Screen.Profile.route
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(SnapBlack),
        bottomBar = {
            if (showBottomBar) {
                SnapBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(Screen.SnapMap.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                // Onboarding Permissions Screen
                composable(Screen.Permissions.route) {
                    PermissionOnboardingScreen(
                        onPermissionsCompleted = {
                            navController.navigate(Screen.SnapMap.route) {
                                popUpTo(Screen.Permissions.route) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }

                // Snap Map Screen
                composable(Screen.SnapMap.route) {
                    SnapMapScreen(
                        onNavigateToChat = { convId ->
                            navController.navigate(Screen.ChatDetail.createRoute(convId))
                        },
                        onNavigateToProfile = {
                            navController.navigate(Screen.Profile.route)
                        }
                    )
                }

                // Chat Conversations List
                composable(Screen.ChatList.route) {
                    ChatListScreen(
                        onNavigateToChat = { convId ->
                            navController.navigate(Screen.ChatDetail.createRoute(convId))
                        },
                        onNavigateToProfile = {
                            navController.navigate(Screen.Profile.route)
                        },
                        onNavigateToCamera = {
                            navController.navigate(Screen.Camera.route)
                        }
                    )
                }

                // Chat Detail / Message Thread
                composable(
                    route = Screen.ChatDetail.route,
                    arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val convId = backStackEntry.arguments?.getString("conversationId") ?: "conv_1"
                    ChatDetailScreen(
                        conversationId = convId,
                        onBack = { navController.popBackStack() },
                        onNavigateToMap = { _ ->
                            navController.navigate(Screen.SnapMap.route) {
                                popUpTo(Screen.SnapMap.route) {
                                    inclusive = false
                                }
                            }
                        }
                    )
                }

                // Snap Camera Screen
                composable(Screen.Camera.route) {
                    SnapCameraScreen(
                        onNavigateToChat = { convId ->
                            navController.navigate(Screen.ChatDetail.createRoute(convId))
                        },
                        onNavigateToProfile = {
                            navController.navigate(Screen.Profile.route)
                        }
                    )
                }

                // Profile & Settings
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
