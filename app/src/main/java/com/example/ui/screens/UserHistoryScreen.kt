package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Parcel
import com.example.data.model.ParcelStatus
import com.example.data.model.PaymentStatus
import com.example.data.repository.ParcelRepository
import com.example.ui.MainViewModel
import com.example.ui.components.GoogleSignInCard
import com.example.ui.components.ParcelStatusBadge
import com.example.ui.components.PaymentStatusBadge
import com.example.ui.navigation.AppDestination
import com.example.ui.theme.IvoBlueDark
import com.example.ui.theme.IvoBluePrimary
import com.example.ui.theme.IvoGreenLight
import com.example.ui.theme.IvoGreenPrimary
import com.example.ui.util.WhatsAppHelper

/**
 * Écran d'historique personnel pour les utilisateurs connectés.
 * Affiche tous leurs colis envoyés avec leur progression étape par étape et statut actuel en temps réel.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserHistoryScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val allParcels by viewModel.allParcels.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()

  var searchQuery by remember { mutableStateOf("") }
  var filterCategory by remember { mutableStateOf("TOUS") } // "TOUS", "EN_COURS", "LIVRES"

  // Filter parcels for the user: if logged in, filter or display user's shipments
  val userParcels = allParcels.filter { parcel ->
    val matchesFilter = when (filterCategory) {
      "EN_COURS" -> parcel.status != ParcelStatus.LIVRE && parcel.status != ParcelStatus.ANNULE
      "LIVRES" -> parcel.status == ParcelStatus.LIVRE
      else -> true
    }
    val matchesSearch = searchQuery.isBlank() ||
      parcel.reference.contains(searchQuery, ignoreCase = true) ||
      parcel.recipientName.contains(searchQuery, ignoreCase = true) ||
      parcel.destinationCity.contains(searchQuery, ignoreCase = true)

    matchesFilter && matchesSearch
  }

  val totalCount = allParcels.size
  val inTransitCount = allParcels.count { it.status != ParcelStatus.LIVRE && it.status != ParcelStatus.ANNULE }
  val deliveredCount = allParcels.count { it.status == ParcelStatus.LIVRE }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
      .testTag("user_history_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.History,
              contentDescription = null,
              tint = IvoBluePrimary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Historique de mes Colis",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 20.sp,
              color = IvoBlueDark
            )
          }
          Text(
            text = "Suivi de progression et reçus de vos expéditions",
            fontSize = 11.sp,
            color = Color.Gray
          )
        }

        IconButton(
          onClick = { viewModel.refreshFirestoreData() },
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Actualiser",
            tint = IvoBluePrimary
          )
        }
      }
    }

    // Google Sign-In Card for cloud backup
    item {
      GoogleSignInCard(
        currentUser = currentUser,
        onAuthChanged = { viewModel.refreshFirestoreData() }
      )
    }

    // Search and Filter Bar
    item {
      Column {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Rechercher un colis par réf ou ville...") },
          leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Effacer")
              }
            }
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("history_search_input"),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Pills
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          HistoryFilterPill(
            label = "Tous ($totalCount)",
            isSelected = filterCategory == "TOUS",
            onClick = { filterCategory = "TOUS" },
            modifier = Modifier.weight(1f)
          )
          HistoryFilterPill(
            label = "En cours ($inTransitCount)",
            isSelected = filterCategory == "EN_COURS",
            onClick = { filterCategory = "EN_COURS" },
            modifier = Modifier.weight(1f)
          )
          HistoryFilterPill(
            label = "Livrés ($deliveredCount)",
            isSelected = filterCategory == "LIVRES",
            onClick = { filterCategory = "LIVRES" },
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // Empty State
    if (userParcels.isEmpty()) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("history_empty_state_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(60.dp)
                .background(IvoBluePrimary.copy(alpha = 0.1f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.LocalShipping,
                contentDescription = null,
                tint = IvoBluePrimary,
                modifier = Modifier.size(32.dp)
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = if (searchQuery.isNotEmpty()) "Aucun colis ne correspond à la recherche." else "Aucun colis enregistré pour l'instant.",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = IvoBlueDark,
              textAlign = TextAlign.Center
            )

            Text(
              text = "Expédiez votre premier colis vers l'intérieur de la Côte d'Ivoire à partir de 1 500 FCFA !",
              fontSize = 12.sp,
              color = Color.Gray,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
            )

            Button(
              onClick = { viewModel.navigateTo(AppDestination.BOOKING) },
              colors = ButtonDefaults.buttonColors(containerColor = IvoGreenPrimary, contentColor = Color.Black),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("history_book_parcel_button")
            ) {
              Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "Expédier un colis maintenant", fontWeight = FontWeight.ExtraBold)
            }
          }
        }
      }
    }

    // Parcels List with Progress Timeline
    items(userParcels.size) { index ->
      val parcel = userParcels[index]
      UserParcelHistoryCard(
        parcel = parcel,
        onTrackClick = {
          viewModel.selectParcelForTracking(parcel)
        },
        onPayClick = {
          viewModel.startPaymentForParcel(parcel)
        },
        onShareClick = {
          val shareText = """
            📦 SUIVI DE COLIS IVOCOLIS
            Référence : ${parcel.reference}
            Destination : ${parcel.destinationCity}
            Destinataire : ${parcel.recipientName}
            Statut actuel : ${parcel.status.label}
            Montant : ${parcel.priceFcfa} FCFA
            Agence : Abidjan Cocody Faya Nouveau Goudron
            Tél WhatsApp : 07 67 18 25 45
          """.trimIndent()
          WhatsAppHelper.shareTrackingViaWhatsApp(context, parcel.recipientPhone.ifBlank { parcel.senderPhone }, shareText)
        }
      )
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun HistoryFilterPill(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    color = if (isSelected) IvoBluePrimary else Color(0xFFF1F5F9),
    shape = RoundedCornerShape(10.dp),
    modifier = modifier
      .clickable(onClick = onClick)
      .height(36.dp)
  ) {
    Box(contentAlignment = Alignment.Center) {
      Text(
        text = label,
        color = if (isSelected) Color.White else Color(0xFF475569),
        fontSize = 11.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
      )
    }
  }
}

@Composable
private fun UserParcelHistoryCard(
  parcel: Parcel,
  onTrackClick: () -> Unit,
  onPayClick: () -> Unit,
  onShareClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("user_history_item_${parcel.reference}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Reference and Status Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = parcel.reference,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = IvoBlueDark
          )
          Text(
            text = ParcelRepository.formatDate(parcel.createdAt),
            fontSize = 11.sp,
            color = Color.Gray
          )
        }

        ParcelStatusBadge(status = parcel.status)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Route: Origin to Destination
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.LocationOn,
          contentDescription = null,
          tint = IvoGreenPrimary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Abidjan (${parcel.senderCommune}) ➔ ${parcel.destinationCity}",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Pour : ${parcel.recipientName} (${parcel.recipientPhone.ifBlank { "Tél non renseigné" }})",
        fontSize = 11.sp,
        color = Color(0xFF64748B),
        modifier = Modifier.padding(start = 22.dp)
      )

      Spacer(modifier = Modifier.height(12.dp))

      // --- Visual Step Progress Timeline ---
      ParcelProgressTimeline(status = parcel.status)

      Spacer(modifier = Modifier.height(12.dp))
      Divider(color = Color(0xFFF1F5F9))
      Spacer(modifier = Modifier.height(10.dp))

      // Bottom Row: Price + Payment Status + Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${parcel.priceFcfa} FCFA",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
            color = IvoBluePrimary
          )
          Spacer(modifier = Modifier.width(8.dp))
          PaymentStatusBadge(paymentStatus = parcel.paymentStatus)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          IconButton(
            onClick = onShareClick,
            modifier = Modifier.size(34.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Partager",
              tint = Color(0xFF25D366),
              modifier = Modifier.size(18.dp)
            )
          }

          if (parcel.paymentStatus == PaymentStatus.NON_PAYE) {
            Button(
              onClick = onPayClick,
              colors = ButtonDefaults.buttonColors(containerColor = IvoGreenPrimary, contentColor = Color.Black),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.height(34.dp)
            ) {
              Icon(imageVector = Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = "Payer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          OutlinedButton(
            onClick = onTrackClick,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Text(text = "Suivre", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

/**
 * Composant de progression visuelle étape par étape (5 étapes logistiques clés)
 */
@Composable
private fun ParcelProgressTimeline(status: ParcelStatus) {
  val (stepIndex, progressRatio) = when (status) {
    ParcelStatus.DEMANDE_RECU -> 0 to 0.15f
    ParcelStatus.RAMASSE_ABIDJAN -> 1 to 0.35f
    ParcelStatus.RECU_AGENCE -> 2 to 0.50f
    ParcelStatus.EXPEDIE, ParcelStatus.EN_TRANSIT -> 3 to 0.75f
    ParcelStatus.ARRIVE_DESTINATION -> 4 to 0.90f
    ParcelStatus.LIVRE -> 5 to 1.0f
    ParcelStatus.ANNULE -> 0 to 0.0f
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Progression de la livraison :",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF64748B)
      )
      Text(
        text = "${(progressRatio * 100).toInt()}%",
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        color = if (status == ParcelStatus.LIVRE) Color(0xFF047857) else IvoBluePrimary
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    LinearProgressIndicator(
      progress = { progressRatio },
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp)),
      color = if (status == ParcelStatus.LIVRE) Color(0xFF047857) else IvoGreenPrimary,
      trackColor = Color(0xFFE2E8F0)
    )

    Spacer(modifier = Modifier.height(6.dp))

    // Step labels
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = "Enregistré",
        fontSize = 9.sp,
        color = if (stepIndex >= 0) IvoBlueDark else Color.Gray,
        fontWeight = if (stepIndex >= 0) FontWeight.Bold else FontWeight.Normal
      )
      Text(
        text = "Hub Cocody",
        fontSize = 9.sp,
        color = if (stepIndex >= 2) IvoBlueDark else Color.Gray,
        fontWeight = if (stepIndex >= 2) FontWeight.Bold else FontWeight.Normal
      )
      Text(
        text = "En Transit",
        fontSize = 9.sp,
        color = if (stepIndex >= 3) IvoBlueDark else Color.Gray,
        fontWeight = if (stepIndex >= 3) FontWeight.Bold else FontWeight.Normal
      )
      Text(
        text = "Livré",
        fontSize = 9.sp,
        color = if (stepIndex >= 5) Color(0xFF047857) else Color.Gray,
        fontWeight = if (stepIndex >= 5) FontWeight.ExtraBold else FontWeight.Normal
      )
    }
  }
}
