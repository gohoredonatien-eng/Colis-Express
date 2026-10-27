package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.PaymentGatewayType
import com.example.data.model.PaymentStatus
import com.example.ui.MainViewModel
import com.example.ui.components.OnlinePaymentDialog
import com.example.ui.components.PaymentReceiptDialog
import com.example.ui.components.PaymentStatusBadge
import com.example.ui.theme.IvoBlueDark
import com.example.ui.theme.IvoBluePrimary
import com.example.ui.theme.IvoGreenLight
import com.example.ui.theme.IvoGreenPrimary
import com.example.ui.util.WhatsAppHelper

@Composable
fun PaymentScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val allParcels by viewModel.allParcels.collectAsState()
  val unpaidParcels = allParcels.filter { it.paymentStatus == PaymentStatus.NON_PAYE }

  var selectedParcelToPay by remember { mutableStateOf<Parcel?>(null) }
  var customRefInput by remember { mutableStateOf("") }

  var showPaymentDialog by remember { mutableStateOf(false) }
  val isProcessingPayment by viewModel.isProcessingPayment.collectAsState()
  val latestReceipt by viewModel.latestReceipt.collectAsState()

  if (showPaymentDialog && selectedParcelToPay != null) {
    OnlinePaymentDialog(
      parcel = selectedParcelToPay!!,
      isProcessing = isProcessingPayment,
      onDismiss = { showPaymentDialog = false },
      onConfirmPayment = { gateway, payerIdentifier ->
        viewModel.processDirectPayment(
          parcel = selectedParcelToPay!!,
          gateway = gateway,
          payerPhone = payerIdentifier
        ) {
          showPaymentDialog = false
        }
      }
    )
  }

  if (latestReceipt != null) {
    PaymentReceiptDialog(
      receipt = latestReceipt!!,
      parcel = selectedParcelToPay,
      onDismiss = { viewModel.dismissReceipt() }
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
      .testTag("payment_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    item {
      Column {
        Text(
          text = "Passerelle de Paiement Sécurisée",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 20.sp,
          color = IvoBlueDark
        )
        Text(
          text = "Réglez vos expéditions en ligne directement via les passerelles populaires de Côte d'Ivoire.",
          fontSize = 12.sp,
          color = Color(0xFF64748B)
        )
      }
    }

    // Security Guarantee Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .background(Color(0xFFDCFCE7), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = null,
              tint = Color(0xFF16A34A),
              modifier = Modifier.size(26.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Paiements 100% Cryptés & Sécurisés",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = Color(0xFF15803D)
            )
            Text(
              text = "Agrégation certifiée via API CinetPay / Bizao avec validation USSD et code OTP. Reçu officiel immédiat.",
              fontSize = 11.sp,
              color = Color(0xFF475569)
            )
          }
        }
      }
    }

    // Interactive Pay a Parcel box
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "Payer une expédition par référence",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
          Text(
            text = "Saisissez la référence du colis à régler :",
            fontSize = 12.sp,
            color = Color.Gray
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = customRefInput,
              onValueChange = { customRefInput = it },
              placeholder = { Text("Ex: IVO-CI-8421") },
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = IvoBluePrimary,
                unfocusedBorderColor = Color(0xFFCBD5E1)
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
              onClick = {
                val clean = customRefInput.trim().uppercase()
                val found = allParcels.firstOrNull { it.reference.equals(clean, ignoreCase = true) }
                if (found != null) {
                  selectedParcelToPay = found
                  showPaymentDialog = true
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = IvoBluePrimary),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.height(54.dp)
            ) {
              Text(text = "Régler", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Unpaid Parcels quick selection list
    if (unpaidParcels.isNotEmpty()) {
      item {
        Column {
          Text(
            text = "Colis en attente de règlement (${unpaidParcels.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
          Spacer(modifier = Modifier.height(8.dp))

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            unpaidParcels.forEach { parcel ->
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    selectedParcelToPay = parcel
                    showPaymentDialog = true
                  },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = parcel.reference,
                      fontWeight = FontWeight.Bold,
                      fontSize = 14.sp,
                      fontFamily = FontFamily.Monospace,
                      color = IvoBlueDark
                    )
                    Text(
                      text = "${parcel.senderCommune} ➔ ${parcel.destinationCity} (${parcel.recipientName})",
                      fontSize = 11.sp,
                      color = Color.Gray
                    )
                  }

                  Column(horizontalAlignment = Alignment.End) {
                    Text(
                      text = "${parcel.priceFcfa} FCFA",
                      fontWeight = FontWeight.ExtraBold,
                      fontSize = 15.sp,
                      color = Color(0xFFFF6600)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                      color = Color(0xFFFF6600),
                      shape = RoundedCornerShape(6.dp)
                    ) {
                      Text(
                        text = "Payer en ligne",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // Passerelles Populaires en Côte d'Ivoire (Detailed Directory)
    item {
      Column {
        Text(
          text = "Passerelles Populaires en Côte d'Ivoire",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        )
        Text(
          text = "Toutes les solutions de paiement mobile & bancaire supportées",
          fontSize = 12.sp,
          color = Color.Gray
        )

        Spacer(modifier = Modifier.height(10.dp))

        val gatewayDetails = listOf(
          PaymentGatewayType.ORANGE_MONEY,
          PaymentGatewayType.WAVE,
          PaymentGatewayType.MTN_MOMO,
          PaymentGatewayType.MOOV_MONEY,
          PaymentGatewayType.CARTE_BANCAIRE
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          gatewayDetails.forEach { g ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .background(g.brandColor, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (g == PaymentGatewayType.CARTE_BANCAIRE) Icons.Default.CreditCard else Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = g.displayName,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp
                    )
                    Surface(
                      color = g.brandColor.copy(alpha = 0.15f),
                      shape = RoundedCornerShape(6.dp)
                    ) {
                      Text(
                        text = g.ussdCode,
                        color = g.brandColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = g.description,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 15.sp
                  )
                }
              }
            }
          }
        }
      }
    }

    // Need Help / WhatsApp
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "Une difficulté lors du paiement ?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text(
            text = "Notre service comptabilité et support client à Cocody Faya vous assiste par WhatsApp ou appel direct au 07 67 18 25 45.",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Button(
            onClick = {
              WhatsAppHelper.openWhatsApp(
                context,
                "Bonjour IVOCOLIS, j'ai besoin d'assistance pour le paiement de mon expédition."
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366), contentColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(text = "Contacter le support paiement WhatsApp", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
