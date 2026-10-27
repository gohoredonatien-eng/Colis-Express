package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Parcel
import com.example.data.model.ParcelStatus
import com.example.data.model.PaymentStatus
import com.example.data.model.StatusHistoryItem
import com.example.data.repository.ParcelRepository
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.OnlinePaymentDialog
import com.example.ui.components.ParcelStatusBadge
import com.example.ui.components.PaymentReceiptDialog
import com.example.ui.components.PaymentStatusBadge
import com.example.ui.theme.IvoBlueDark
import com.example.ui.theme.IvoBlueLight
import com.example.ui.theme.IvoBluePrimary
import com.example.ui.theme.IvoGreenLight
import com.example.ui.theme.IvoGreenPrimary
import com.example.ui.util.WhatsAppHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TrackingScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val allParcels by viewModel.allParcels.collectAsState()
  val selectedParcel by viewModel.selectedParcel.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val isSearchingTracking by viewModel.isSearchingTracking.collectAsState()
  var inputRef by remember { mutableStateOf(searchQuery) }

  var showPaymentDialog by remember { mutableStateOf(false) }
  val isProcessingPayment by viewModel.isProcessingPayment.collectAsState()
  val latestReceipt by viewModel.latestReceipt.collectAsState()

  if (showPaymentDialog && selectedParcel != null) {
    OnlinePaymentDialog(
      parcel = selectedParcel!!,
      isProcessing = isProcessingPayment,
      onDismiss = { showPaymentDialog = false },
      onConfirmPayment = { gateway, payerPhone ->
        viewModel.processDirectPayment(
          parcel = selectedParcel!!,
          gateway = gateway,
          payerPhone = payerPhone
        ) {
          showPaymentDialog = false
        }
      }
    )
  }

  if (latestReceipt != null) {
    PaymentReceiptDialog(
      receipt = latestReceipt!!,
      parcel = selectedParcel,
      onDismiss = { viewModel.dismissReceipt() }
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
      .testTag("tracking_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header & Search
    item {
      Column {
        Text(
          text = "Suivi de Colis en Direct",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 20.sp,
          color = IvoBlueDark
        )
        Text(
          text = "Consultez l'acheminement de votre colis avec son numéro de référence (synchronisation Cloud Firestore).",
          fontSize = 12.sp,
          color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = inputRef,
            onValueChange = { inputRef = it },
            label = { Text("Numéro de référence (ex: IVO-CI-8421)") },
            placeholder = { Text("IVO-CI-...") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
              onSearch = {
                if (inputRef.isNotBlank()) {
                  viewModel.searchParcelByRef(inputRef)
                }
              }
            ),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = IvoBluePrimary,
              unfocusedBorderColor = Color(0xFFCBD5E1)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("tracking_input_field")
          )

          Spacer(modifier = Modifier.width(8.dp))

          Button(
            onClick = {
              if (inputRef.isNotBlank()) {
                viewModel.searchParcelByRef(inputRef)
              }
            },
            enabled = !isSearchingTracking,
            colors = ButtonDefaults.buttonColors(containerColor = IvoBluePrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .height(54.dp)
              .testTag("search_ref_button")
          ) {
            if (isSearchingTracking) {
              CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = Color.White,
                strokeWidth = 2.dp
              )
            } else {
              Icon(imageVector = Icons.Default.Search, contentDescription = "Rechercher")
            }
          }
        }
      }
    }

    // Active Firestore Query Loading Banner
    if (isSearchingTracking) {
      item {
        Surface(
          color = IvoBlueLight.copy(alpha = 0.35f),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, IvoBluePrimary.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(20.dp),
              color = IvoBluePrimary,
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Interrogation de Cloud Firestore...",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = IvoBlueDark
              )
              Text(
                text = "Vérification en direct du statut du colis $inputRef",
                fontSize = 10.sp,
                color = Color(0xFF334155)
              )
            }
          }
        }
      }
    }

    // Quick selection of registered parcels (Pills)
    if (allParcels.isNotEmpty()) {
      item {
        Column {
          Text(text = "Derniers colis enregistrés :", fontSize = 11.sp, color = Color.Gray)
          Spacer(modifier = Modifier.height(6.dp))

          FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            allParcels.take(6).forEach { parcel ->
              val isSelected = selectedParcel?.reference == parcel.reference
              Surface(
                color = if (isSelected) IvoBlueDark else Color(0xFFF1F5F9),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) IvoBlueLight else Color(0xFFE2E8F0)),
                modifier = Modifier.clickable {
                  inputRef = parcel.reference
                  viewModel.selectParcelForTracking(parcel)
                }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "${parcel.reference} (${parcel.destinationCity})",
                    fontSize = 11.sp,
                    color = if (isSelected) Color.White else Color(0xFF334155),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                  )
                }
              }
            }
          }
        }
      }
    }

    // If a parcel is selected, display tracking details
    if (selectedParcel != null) {
      val parcel = selectedParcel!!

      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            // Parcel Head
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
                  fontSize = 19.sp,
                  color = IvoBlueDark
                )
                Text(
                  text = "Enregistré le ${ParcelRepository.formatDate(parcel.createdAt)}",
                  fontSize = 11.sp,
                  color = Color.Gray
                )
              }

              ParcelStatusBadge(status = parcel.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cloud Firestore real-time indicator pill
            Surface(
              color = IvoGreenPrimary.copy(alpha = 0.12f),
              shape = RoundedCornerShape(6.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, IvoGreenPrimary.copy(alpha = 0.35f))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = Color(0xFF047857),
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Statut Cloud Firestore vérifié en temps réel",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF047857)
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Route Banner: Abidjan -> Destination
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF8FAFC))
                .padding(12.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(text = "Départ", fontSize = 10.sp, color = Color(0xFF64748B))
                  Text(text = "Abidjan (${parcel.senderCommune})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                  Text(text = parcel.senderName, fontSize = 11.sp, color = Color.Gray)
                }

                Icon(
                  imageVector = Icons.Default.NearMe,
                  contentDescription = null,
                  tint = IvoBluePrimary,
                  modifier = Modifier
                    .size(20.dp)
                    .padding(horizontal = 4.dp)
                )

                Column(
                  modifier = Modifier.weight(1f),
                  horizontalAlignment = Alignment.End
                ) {
                  Text(text = "Destination", fontSize = 10.sp, color = Color(0xFF64748B))
                  Text(text = parcel.destinationCity, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = IvoBlueDark)
                  Text(text = parcel.recipientName, fontSize = 11.sp, color = Color.Gray)
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Payment status & Amount
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(text = "Tarif d'expédition", fontSize = 11.sp, color = Color.Gray)
                Text(
                  text = "${parcel.priceFcfa} FCFA",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 16.sp,
                  color = IvoBluePrimary
                )
              }

              PaymentStatusBadge(paymentStatus = parcel.paymentStatus)
            }

            if (parcel.paymentStatus == PaymentStatus.NON_PAYE) {
              Spacer(modifier = Modifier.height(8.dp))
              Button(
                onClick = { showPaymentDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6600)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Icon(imageVector = Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Payer en ligne (Orange / Wave / MTN / Moov / Carte)")
              }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Divider(color = Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(14.dp))

            // Timeline Steps
            Text(
              text = "Historique et étapes d'acheminement",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (parcel.history.isEmpty()) {
              Text(
                text = "Colis en attente de prise en charge par l'agence.",
                fontSize = 12.sp,
                color = Color.Gray
              )
            } else {
              Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                parcel.history.asReversed().forEachIndexed { index, stepItem ->
                  TimelineStepRow(
                    item = stepItem,
                    isLatest = index == 0
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Share & WhatsApp buttons
            val shareText = """
              📦 SUIVI DE COLIS IVOCOLIS EXPRESS
              Référence : ${parcel.reference}
              Statut : ${parcel.status.label}
              Départ : Abidjan (${parcel.senderCommune})
              Destination : ${parcel.destinationCity}
              Destinataire : ${parcel.recipientName} (${parcel.recipientPhone})
              Paiement : ${parcel.paymentStatus.label} (${parcel.priceFcfa} FCFA)
              
              Agence Abidjan : Cocody Faya Nouveau Goudron
              Assistance WhatsApp : +225 07 67 18 25 45
            """.trimIndent()

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = {
                  WhatsAppHelper.shareTrackingViaWhatsApp(
                    context,
                    parcel.recipientPhone,
                    shareText
                  )
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFF25D366),
                  contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
              ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Partager WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              OutlinedButton(
                onClick = {
                  WhatsAppHelper.openWhatsApp(
                    context,
                    "Bonjour IVOCOLIS, j'ai une question concernant mon colis référence ${parcel.reference} à destination de ${parcel.destinationCity}."
                  )
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
              ) {
                Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp), tint = IvoBluePrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Aide colis", fontSize = 11.sp, color = IvoBluePrimary)
              }
            }
          }
        }
      }
    } else {
      // Empty state / Guide
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(60.dp)
                .background(IvoBluePrimary.copy(alpha = 0.1f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = null,
                tint = IvoBluePrimary,
                modifier = Modifier.size(32.dp)
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "Aucun colis sélectionné",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )

            Text(
              text = "Entrez votre numéro de référence (ex: IVO-CI-8421) ci-dessus pour consulter le statut de livraison et l'historique de votre expédition.",
              fontSize = 12.sp,
              color = Color.Gray,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
              modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = {
                val first = allParcels.firstOrNull()
                if (first != null) {
                  inputRef = first.reference
                  viewModel.selectParcelForTracking(first)
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = IvoGreenPrimary, contentColor = Color.Black),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text(text = "Afficher un exemple de colis en cours", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun TimelineStepRow(
  item: StatusHistoryItem,
  isLatest: Boolean
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.Top
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Box(
        modifier = Modifier
          .size(24.dp)
          .background(
            if (isLatest) IvoGreenPrimary else IvoBluePrimary.copy(alpha = 0.2f),
            CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        if (isLatest) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(14.dp)
          )
        } else {
          Box(modifier = Modifier.size(6.dp).background(IvoBluePrimary, CircleShape))
        }
      }
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = item.status.label,
          fontWeight = if (isLatest) FontWeight.Bold else FontWeight.SemiBold,
          fontSize = 13.sp,
          color = if (isLatest) IvoBlueDark else Color(0xFF1E293B)
        )
        Text(
          text = ParcelRepository.formatDate(item.timestamp),
          fontSize = 10.sp,
          color = Color(0xFF94A3B8)
        )
      }

      Text(
        text = "📍 ${item.location}",
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = IvoBluePrimary
      )

      if (item.comment.isNotBlank()) {
        Text(
          text = item.comment,
          fontSize = 11.sp,
          color = Color(0xFF64748B),
          lineHeight = 15.sp
        )
      }
    }
  }
}
