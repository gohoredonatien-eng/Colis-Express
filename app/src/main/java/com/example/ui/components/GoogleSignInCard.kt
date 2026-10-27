package com.example.ui.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.ui.auth.AuthManager
import com.example.ui.theme.IvoBlueDark
import com.example.ui.theme.IvoBluePrimary
import com.example.ui.theme.IvoGreenLight
import com.example.ui.theme.IvoGreenPrimary
import com.google.firebase.auth.FirebaseUser

@Composable
fun GoogleSignInCard(
  currentUser: FirebaseUser?,
  onAuthChanged: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val credentialManager = remember { CredentialManager.create(context) }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("google_auth_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      if (currentUser != null) {
        // Authenticated with Google state
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .background(Color(0xFFDCFCE7), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CloudDone,
                contentDescription = null,
                tint = Color(0xFF16A34A),
                modifier = Modifier.size(24.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = currentUser.displayName ?: "Client IVOCOLIS",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = IvoBlueDark
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  color = IvoGreenPrimary.copy(alpha = 0.2f),
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text(
                    text = "Cloud Sync",
                    color = Color(0xFF047857),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }

              Text(
                text = currentUser.email ?: "Synchronisation Firestore temps réel active",
                fontSize = 11.sp,
                color = Color.Gray
              )
            }
          }

          IconButton(
            onClick = {
              AuthManager.signOut(context, credentialManager, onSignOutComplete = onAuthChanged, scope = scope)
            }
          ) {
            Icon(
              imageVector = Icons.Default.Logout,
              contentDescription = "Déconnexion",
              tint = Color(0xFF94A3B8)
            )
          }
        }
      } else {
        // Unauthenticated state
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .background(IvoBluePrimary.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CloudSync,
              contentDescription = null,
              tint = IvoBluePrimary,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Sauvegarde Cloud & Synchronisation Temps Réel",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = IvoBlueDark
            )
            Text(
              text = "Connectez-vous avec Google pour retrouver vos expéditions sur tous vos appareils via Firebase Firestore.",
              fontSize = 11.sp,
              color = Color(0xFF64748B)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (errorMessage != null) {
          Text(
            text = errorMessage!!,
            color = Color(0xFFDC2626),
            fontSize = 11.sp,
            modifier = Modifier.padding(bottom = 8.dp)
          )
        }

        Button(
          onClick = {
            isLoading = true
            errorMessage = null
            AuthManager.onGoogleSignInClicked(
              context = context,
              credentialManager = credentialManager,
              onAuthSuccess = {
                isLoading = false
                onAuthChanged()
              },
              onAuthError = { err ->
                isLoading = false
                errorMessage = err
              },
              scope = scope,
              onAuthCancelled = { isLoading = false }
            )
          },
          enabled = !isLoading,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF1F2937)
          ),
          border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFCBD5E1)),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .testTag("google_signin_button")
        ) {
          if (isLoading) {
            CircularProgressIndicator(
              modifier = Modifier.size(20.dp),
              strokeWidth = 2.dp,
              color = IvoBluePrimary
            )
          } else {
            Icon(
              imageVector = Icons.Default.AccountCircle,
              contentDescription = null,
              tint = IvoBluePrimary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Se connecter avec Google (Synchro Firestore)",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }
      }
    }
  }
}
