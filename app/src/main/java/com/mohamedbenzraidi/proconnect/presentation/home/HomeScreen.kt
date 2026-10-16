package com.mohamedbenzraidi.proconnect.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToOpportunities: () -> Unit,
    onNavigateToNetwork: () -> Unit,
    onNavigateToConversations: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenue sur ProConnect ! 🚀",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToProfile,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp)
        ) {
            Text("Mon Profil Professionnel 👤")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onNavigateToOpportunities,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp)
        ) {
            Text("💼 Offres & Opportunités")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onNavigateToNetwork,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp)
        ) {
            Text("🤝 Mon Réseau & Connexions")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onNavigateToConversations,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp)
        ) {
            Text("💬 Messages & Conversations")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp)
        ) {
            Text("Se déconnecter")
        }
    }
}
