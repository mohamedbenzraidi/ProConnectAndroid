package com.mohamedbenzraidi.proconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.mohamedbenzraidi.proconnect.core.navigation.AppNavGraph
import com.mohamedbenzraidi.proconnect.core.network.NetworkModule
import com.mohamedbenzraidi.proconnect.data.local.TokenManager
import com.mohamedbenzraidi.proconnect.data.repository.AuthRepositoryImpl
import com.mohamedbenzraidi.proconnect.data.repository.ConnectionRepositoryImpl
import com.mohamedbenzraidi.proconnect.data.repository.MessageRepositoryImpl
import com.mohamedbenzraidi.proconnect.data.repository.OpportunityRepositoryImpl
import com.mohamedbenzraidi.proconnect.data.repository.ProfileRepositoryImpl
import com.mohamedbenzraidi.proconnect.presentation.auth.AuthViewModel
import com.mohamedbenzraidi.proconnect.presentation.connections.NetworkViewModel
import com.mohamedbenzraidi.proconnect.presentation.messages.ConversationsViewModel
import com.mohamedbenzraidi.proconnect.presentation.opportunities.OpportunitiesViewModel
import com.mohamedbenzraidi.proconnect.presentation.profile.ProfileViewModel
import com.mohamedbenzraidi.proconnect.ui.theme.ProConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Injection manuelle des dépendances
        val tokenManager = TokenManager(applicationContext)

        val authApi = NetworkModule.provideAuthApi(tokenManager)
        val authRepository = AuthRepositoryImpl(authApi, tokenManager)
        val authViewModel = AuthViewModel(authRepository)

        val profileApi = NetworkModule.provideProfileApi(tokenManager)
        val profileRepository = ProfileRepositoryImpl(profileApi)
        val profileViewModel = ProfileViewModel(profileRepository)

        val opportunityApi = NetworkModule.provideOpportunityApi(tokenManager)
        val opportunityRepository = OpportunityRepositoryImpl(opportunityApi)
        val opportunitiesViewModel = OpportunitiesViewModel(opportunityRepository)

        val connectionApi = NetworkModule.provideConnectionApi(tokenManager)
        val connectionRepository = ConnectionRepositoryImpl(connectionApi)
        val networkViewModel = NetworkViewModel(connectionRepository, tokenManager)

        val messageApi = NetworkModule.provideMessageApi(tokenManager)
        val messageRepository = MessageRepositoryImpl(messageApi)
        val conversationsViewModel = ConversationsViewModel(messageRepository, connectionRepository, tokenManager)

        setContent {
            ProConnectTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    profileViewModel = profileViewModel,
                    opportunitiesViewModel = opportunitiesViewModel,
                    networkViewModel = networkViewModel,
                    conversationsViewModel = conversationsViewModel,
                    messageRepository = messageRepository,
                    tokenManager = tokenManager
                )
            }
        }
    }
}
