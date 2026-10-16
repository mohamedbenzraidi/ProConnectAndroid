package com.mohamedbenzraidi.proconnect.core.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object Register : Screen("register")
    data object Profile : Screen("profile")
    data object Opportunities : Screen("opportunities")
    data object Network : Screen("network")
    data object Conversations : Screen("conversations")
    data object Chat : Screen("chat/{recipientId}/{participantName}/{conversationId}") {
        fun createRoute(recipientId: String, participantName: String, conversationId: String = "") =
            "chat/$recipientId/$participantName/${conversationId.ifBlank { "none" }}"
    }
}
