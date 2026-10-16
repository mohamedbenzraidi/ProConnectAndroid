package com.mohamedbenzraidi.proconnect.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mohamedbenzraidi.proconnect.data.local.TokenManager
import com.mohamedbenzraidi.proconnect.presentation.auth.AuthViewModel
import com.mohamedbenzraidi.proconnect.presentation.auth.LoginScreen
import com.mohamedbenzraidi.proconnect.presentation.auth.RegisterScreen
import com.mohamedbenzraidi.proconnect.presentation.connections.NetworkScreen
import com.mohamedbenzraidi.proconnect.presentation.connections.NetworkViewModel
import com.mohamedbenzraidi.proconnect.presentation.home.HomeScreen
import com.mohamedbenzraidi.proconnect.presentation.messages.ChatScreen
import com.mohamedbenzraidi.proconnect.presentation.messages.ChatViewModel
import com.mohamedbenzraidi.proconnect.presentation.messages.ConversationsScreen
import com.mohamedbenzraidi.proconnect.presentation.messages.ConversationsViewModel
import com.mohamedbenzraidi.proconnect.presentation.opportunities.OpportunitiesScreen
import com.mohamedbenzraidi.proconnect.presentation.opportunities.OpportunitiesViewModel
import com.mohamedbenzraidi.proconnect.presentation.profile.ProfileScreen
import com.mohamedbenzraidi.proconnect.presentation.profile.ProfileUiState
import com.mohamedbenzraidi.proconnect.presentation.profile.ProfileViewModel
import com.mohamedbenzraidi.proconnect.data.remote.dto.Role
import com.mohamedbenzraidi.proconnect.domain.repository.MessageRepository

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    opportunitiesViewModel: OpportunitiesViewModel,
    networkViewModel: NetworkViewModel,
    conversationsViewModel: ConversationsViewModel,
    messageRepository: MessageRepository,
    tokenManager: TokenManager
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    // Attendre la vérification du token DataStore au lancement
    if (isLoggedIn == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val startDestination = if (isLoggedIn == true) Screen.Home.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Protected Routes
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToOpportunities = {
                    navController.navigate(Screen.Opportunities.route)
                },
                onNavigateToNetwork = {
                    navController.navigate(Screen.Network.route)
                },
                onNavigateToConversations = {
                    navController.navigate(Screen.Conversations.route)
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = profileViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Opportunities.route) {
            val profileState by profileViewModel.uiState.collectAsState()
            val isRecruiter = (profileState as? ProfileUiState.Success)?.profile?.role == Role.RECRUITER
            OpportunitiesScreen(
                viewModel = opportunitiesViewModel,
                isRecruiter = isRecruiter,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Network.route) {
            NetworkScreen(
                viewModel = networkViewModel,
                onNavigateToChat = { participantId, participantName ->
                    navController.navigate(Screen.Chat.createRoute(participantId, participantName, ""))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Conversations.route) {
            ConversationsScreen(
                viewModel = conversationsViewModel,
                onNavigateToChat = { recipientId, participantName, conversationId ->
                    navController.navigate(Screen.Chat.createRoute(recipientId, participantName, conversationId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Chat.route) { backStackEntry ->
            val recipientId = backStackEntry.arguments?.getString("recipientId") ?: ""
            val participantName = backStackEntry.arguments?.getString("participantName") ?: ""
            val rawConvId = backStackEntry.arguments?.getString("conversationId") ?: ""
            val conversationId = if (rawConvId == "none") "" else rawConvId

            val chatViewModel = remember(recipientId, conversationId) {
                ChatViewModel(messageRepository, recipientId, conversationId, tokenManager)
            }

            ChatScreen(
                viewModel = chatViewModel,
                participantName = participantName,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
