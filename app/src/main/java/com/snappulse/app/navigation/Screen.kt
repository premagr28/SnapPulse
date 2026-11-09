package com.snappulse.app.navigation

sealed class Screen(val route: String) {
    object Permissions : Screen("permissions")
    object SnapMap : Screen("snap_map")
    object ChatList : Screen("chat_list")
    object ChatDetail : Screen("chat_detail/{conversationId}") {
        fun createRoute(conversationId: String) = "chat_detail/$conversationId"
    }
    object Camera : Screen("camera")
    object Profile : Screen("profile")
}
