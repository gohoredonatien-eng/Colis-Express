package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Parcel
import com.example.data.model.PaymentGatewayType
import com.example.data.model.PaymentTransaction
import com.example.ui.theme.IvoBlueDark
import com.example.ui.theme.IvoBluePrimary
import com.example.ui.theme.IvoGreenLight
import com.example.ui.theme.IvoGreenPrimary
import com.example.ui.theme.MoovBlueColor
import com.example.ui.theme.MtnYellowColor
import com.example.ui.theme.OrangeMoneyColor
import com.example.ui.theme.VisaBlueColor
import com.example.ui.theme.WaveBlueColor
import com.example.ui.util.WhatsAppHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OnlinePaymentDialog(
  parcel: Parcel,
  isProcessing: Boolean,
  onDismiss: () -> Unit,
  onConfirmPayment: (gateway: PaymentGatewayType, payerPhoneOrCard: String) -> Unit
) {
  var selectedGateway by remember { mutableStateOf(PaymentGatewayType.ORANGE_MONEY) }
  var payerPhone by remember { mutableStateOf(parcel.senderPhone) }
  var cardNumber by remember { mutableStateOf("") }
  var cardExpiry by remember { mutableStateOf("") }
  var cardCvv by remember { mutableStateOf("") }
  var otpCode by remember { mutableStateOf("") }
  var step by remember { mutableStateOf(1) } // 1: Input details, 2: USSD / OTP Validation confirmation

  Dialog(onDismissRequest = { if (!isProcessing) onDismiss() }) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("online_payment_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .background(IvoBluePrimary.copy(alpha = 0.1f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = IvoBluePrimary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Paiement Sécurisé CI",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
              )
              Text(
                text = "Colis ${parcel.reference}",
                fontSize = 12.sp,
                color = Color.Gray
              )
            }
          }

          IconButton(onClick = onDismiss, enabled = !isProcessing) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Amount Summary Banner
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F5F9))
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = "Montant à régler", fontSize = 11.sp, color = Color(0xFF64748B))
              Text(
                text = "${parcel.priceFcfa} FCFA",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = IvoBlueDark
              )
            }
            Surface(
              color = IvoGreenPrimary.copy(alpha = 0.15f),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "0% de frais",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF047857),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (step == 1) {
          Text(
            text = "Choisissez votre passerelle de paiement en Côte d'Ivoire :",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Gateway Selector List
          val gateways = listOf(
            PaymentGatewayType.ORANGE_MONEY,
            PaymentGatewayType.WAVE,
            PaymentGatewayType.MTN_MOMO,
            PaymentGatewayType.MOOV_MONEY,
            PaymentGatewayType.CARTE_BANCAIRE
          )

          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            gateways.forEach { gateway ->
              val isSelected = selectedGateway == gateway
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSelected) gateway.brandColor.copy(alpha = 0.1f) else Color(0xFFF8FAFC))
                  .border(
                    1.2.dp,
                    if (isSelected) gateway.brandColor else Color(0xFFE2E8F0),
                    RoundedCornerShape(10.dp)
                  )
                  .clickable { selectedGateway = gateway }
                  .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(24.dp)
                    .background(gateway.brandColor, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (gateway == PaymentGatewayType.CARTE_BANCAIRE) Icons.Default.CreditCard else Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                  )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = gateway.displayName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = gateway.ussdCode,
                    fontSize = 11.sp,
                    color = Color.Gray
                  )
                }

                if (isSelected) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = gateway.brandColor,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Specific inputs depending on gateway
          if (selectedGateway == PaymentGatewayType.CARTE_BANCAIRE) {
            OutlinedTextField(
              value = cardNumber,
              onValueChange = { if (it.length <= 16) cardNumber = it },
              label = { Text("Numéro de carte Visa / Mastercard") },
              placeholder = { Text("4000 1234 5678 9010") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedTextField(
                value = cardExpiry,
                onValueChange = { if (it.length <= 5) cardExpiry = it },
                label = { Text("MM/AA") },
                placeholder = { Text("12/28") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
              )
              OutlinedTextField(
                value = cardCvv,
                onValueChange = { if (it.length <= 3) cardCvv = it },
                label = { Text("CVV") },
                placeholder = { Text("123") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
              )
            }
          } else {
            // Mobile Money Phone
            OutlinedTextField(
              value = payerPhone,
              onValueChange = { payerPhone = it },
              label = { Text("Numéro de téléphone ${selectedGateway.displayName.take(12)}") },
              placeholder = { Text("Ex: 07 67 18 25 45") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              singleLine = true,
              leadingIcon = {
                Text(
                  text = "+225",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = IvoBluePrimary,
                  modifier = Modifier.padding(start = 12.dp)
                )
              },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = { step = 2 },
            enabled = if (selectedGateway == PaymentGatewayType.CARTE_BANCAIRE) cardNumber.length >= 12 else payerPhone.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
              containerColor = selectedGateway.brandColor,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("submit_gateway_step1_button")
          ) {
            Text(
              text = "Poursuivre vers la validation sécurisée",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        } else {
          // Step 2: Confirmation / OTP / USSD
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = selectedGateway.brandColor,
              modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Autorisation de Débit ${selectedGateway.displayName}",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = selectedGateway.instructions,
              fontSize = 12.sp,
              color = Color(0xFF475569),
              lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Simulated USSD prompt / OTP box
            Surface(
              color = Color(0xFFF8FAFC),
              shape = RoundedCornerShape(12.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "Code d'autorisation temporaire (OTP)",
                  fontSize = 11.sp,
                  color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                  value = otpCode,
                  onValueChange = { otpCode = it },
                  placeholder = { Text("Ex: 482910") },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth()
                )
                Text(
                  text = "Pour le test en ligne, tout code ou simple validation validera le débit avec succès.",
                  fontSize = 10.sp,
                  color = Color(0xFF64748B)
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isProcessing) {
              CircularProgressIndicator(
                color = selectedGateway.brandColor,
                modifier = Modifier.size(36.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Communication avec la passerelle ${selectedGateway.displayName}...",
                fontSize = 12.sp,
                color = Color.Gray
              )
            } else {
              Button(
                onClick = {
                  val identifier = if (selectedGateway == PaymentGatewayType.CARTE_BANCAIRE) "CARTE-****${cardNumber.takeLast(4)}" else payerPhone
                  onConfirmPayment(selectedGateway, identifier)
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = selectedGateway.brandColor,
                  contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("confirm_payment_final_button")
              ) {
                Text(
                  text = "Confirmer le paiement de ${parcel.priceFcfa} FCFA",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              OutlinedButton(
                onClick = { step = 1 },
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(text = "Changer de moyen de paiement")
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun PaymentReceiptDialog(
  receipt: PaymentTransaction,
  parcel: Parcel?,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val dateFormatted = SimpleDateFormat("dd MMMM yyyy à HH:mm", Locale.FRENCH).format(Date(receipt.timestamp))

  val receiptText = """
    🧾 REÇU OFFICIEL DE PAIEMENT - IVOCOLIS EXPRESS
    ======================================
    N° Reçu : ${receipt.receiptNumber}
    Réf. Colis : ${receipt.parcelReference}
    Montant Payé : ${receipt.amountFcfa} FCFA
    Moyen de Paiement : ${receipt.gateway.displayName}
    Date : $dateFormatted
    Téléphone Payeur : ${receipt.payerPhone}
    Statut : PAYÉ ET SÉCURISÉ (SUCCESS)
    
    Expéditeur : ${parcel?.senderName ?: "Client"} (${parcel?.senderPhone ?: ""})
    Destinataire : ${parcel?.recipientName ?: ""} (${parcel?.destinationCity ?: ""})
    Agence émettrice : Abidjan - Cocody Faya Nouveau Goudron
    Contact WhatsApp : +225 07 67 18 25 45
    ======================================
    Merci pour votre confiance avec IVOCOLIS !
  """.trimIndent()

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("payment_receipt_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Success Stamp Header
        Box(
          modifier = Modifier
            .size(54.dp)
            .background(Color(0xFFDCFCE7), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF16A34A),
            modifier = Modifier.size(32.dp)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Paiement Confirmé !",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 19.sp,
          color = Color(0xFF15803D)
        )
        Text(
          text = "Reçu officiel IVOCOLIS Express",
          fontSize = 12.sp,
          color = Color.Gray
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Receipt Card layout
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = Color(0xFFF8FAFC),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "Réf. Transaction :", fontSize = 11.sp, color = Color.Gray)
              Text(
                text = receipt.transactionId,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "Colis associé :", fontSize = 11.sp, color = Color.Gray)
              Text(
                text = receipt.parcelReference,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = IvoBluePrimary
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "Moyen de règlement :", fontSize = 11.sp, color = Color.Gray)
              Text(
                text = receipt.gateway.displayName,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "Date :", fontSize = 11.sp, color = Color.Gray)
              Text(text = dateFormatted, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color(0xFFCBD5E1))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Total Encaissé",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Text(
                text = "${receipt.amountFcfa} FCFA",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = IvoBlueDark
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Action Buttons
        Button(
          onClick = {
            WhatsAppHelper.shareGeneralText(
              context,
              "Partager mon reçu IVOCOLIS",
              receiptText
            )
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF25D366),
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Partager le reçu par WhatsApp / SMS", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
          onClick = onDismiss,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(text = "Fermer")
        }
      }
    }
  }
}
