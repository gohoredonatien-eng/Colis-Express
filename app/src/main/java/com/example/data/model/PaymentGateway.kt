package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.MoovBlueColor
import com.example.ui.theme.MtnYellowColor
import com.example.ui.theme.OrangeMoneyColor
import com.example.ui.theme.VisaBlueColor
import com.example.ui.theme.WaveBlueColor

enum class PaymentGatewayType(
  val displayName: String,
  val brandColor: Color,
  val ussdCode: String,
  val phonePrefixes: List<String>,
  val description: String,
  val instructions: String,
  val feesPercent: Double = 0.0
) {
  ORANGE_MONEY(
    displayName = "Orange Money Côte d'Ivoire",
    brandColor = OrangeMoneyColor,
    ussdCode = "#144#",
    phonePrefixes = listOf("07", "08", "09"),
    description = "Paiement via compte Orange Money CI. Génération code de validation #144*82#.",
    instructions = "Composez le #144*82# pour obtenir votre code d'autorisation Orange Money, puis validez votre paiement.",
    feesPercent = 0.0
  ),
  WAVE(
    displayName = "Wave Côte d'Ivoire",
    brandColor = WaveBlueColor,
    ussdCode = "App Wave",
    phonePrefixes = listOf("01", "05", "07"),
    description = "Paiement instantané sans frais via l'application Wave CI.",
    instructions = "Vous recevrez une invite de confirmation sur l'application Wave ou scannerez le QR code pour valider le montant exact.",
    feesPercent = 0.0
  ),
  MTN_MOMO(
    displayName = "MTN Mobile Money",
    brandColor = MtnYellowColor,
    ussdCode = "*133#",
    phonePrefixes = listOf("05", "04", "06"),
    description = "Paiement via compte MTN MoMo CI. Notification push USSD instantanée.",
    instructions = "Une invite USSD apparaîtra sur votre téléphone MTN. Entrez votre code PIN secret pour confirmer le paiement.",
    feesPercent = 0.0
  ),
  MOOV_MONEY(
    displayName = "Moov Money Flooz",
    brandColor = MoovBlueColor,
    ussdCode = "*155#",
    phonePrefixes = listOf("01", "02", "03"),
    description = "Paiement sécurisé via Moov Money CI (Flooz).",
    instructions = "Composez le *155# ou confirmez la notification de prélèvement avec votre code secret Moov.",
    feesPercent = 0.0
  ),
  CARTE_BANCAIRE(
    displayName = "Carte Visa / Mastercard",
    brandColor = VisaBlueColor,
    ussdCode = "3D-Secure",
    phonePrefixes = emptyList(),
    description = "Paiement sécurisé par carte bancaire avec authentification 3D Secure (CinetPay / Paystack).",
    instructions = "Saisissez les 16 chiffres de votre carte bancaire, la date d'expiration et le cryptogramme CVV (3 chiffres au dos).",
    feesPercent = 1.5
  ),
  ESPECES_COLLECTE(
    displayName = "Paiement à la Collecte (Espèces)",
    brandColor = Color(0xFF10B981),
    ussdCode = "Espèces",
    phonePrefixes = emptyList(),
    description = "Réglez directement en espèces au coursier Ivocolis lors du ramassage à Abidjan.",
    instructions = "Préparez l'appoint en francs CFA (FCFA) lors du passage de notre coursier à votre adresse.",
    feesPercent = 0.0
  )
}

data class PaymentTransaction(
  val transactionId: String,
  val parcelReference: String,
  val amountFcfa: Int,
  val gateway: PaymentGatewayType,
  val payerPhone: String,
  val status: String = "SUCCESS", // PENDING, SUCCESS, FAILED
  val timestamp: Long = System.currentTimeMillis(),
  val receiptNumber: String = "REC-${System.currentTimeMillis() % 1000000}"
)
