package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DefaultDestinations
import com.example.data.model.Destination
import com.example.data.model.Parcel
import com.example.data.model.PaymentGatewayType
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.GoogleSignInCard
import com.example.ui.components.PaymentReceiptDialog
import com.example.ui.components.ShippingCalculator
import com.example.ui.theme.IvoBlueDark
import com.example.ui.theme.IvoBluePrimary
import com.example.ui.theme.IvoGreenContainer
import com.example.ui.theme.IvoGreenLight
import com.example.ui.theme.IvoGreenPrimary
import com.example.ui.util.WhatsAppHelper
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PickupRequestScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val destinations by viewModel.activeDestinations.collectAsState()
  val availableDestinations = if (destinations.isNotEmpty()) destinations else DefaultDestinations.initialList

  val initialCity by viewModel.calcCity.collectAsState()
  val initialWeight by viewModel.calcWeight.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()

  // Form Fields
  var senderName by remember { mutableStateOf("") }
  var senderPhone by remember { mutableStateOf("") }
  var senderCommune by remember { mutableStateOf("Cocody") }
  var senderAddress by remember { mutableStateOf("Faya Nouveau Goudron") }

  var recipientName by remember { mutableStateOf("") }
  var recipientPhone by remember { mutableStateOf("") }
  var selectedDestination by remember {
    mutableStateOf(availableDestinations.firstOrNull { it.cityName == initialCity } ?: availableDestinations.first())
  }
  var destinationAddress by remember { mutableStateOf("Gare Centrale / Centre-ville") }

  var parcelCategory by remember { mutableStateOf("Colis standard") }
  var weightKg by remember { mutableDoubleStateOf(initialWeight) }
  var isExpressService by remember { mutableStateOf(false) }
  var description by remember { mutableStateOf("") }

  var chosenPaymentMethod by remember { mutableStateOf(PaymentGatewayType.ORANGE_MONEY) }
  var payerPhone by remember { mutableStateOf("") }

  // User interaction & submission state for real-time validation
  var hasAttemptedSubmit by remember { mutableStateOf(false) }
  var senderNameTouched by remember { mutableStateOf(false) }
  var senderPhoneTouched by remember { mutableStateOf(false) }
  var recipientNameTouched by remember { mutableStateOf(false) }
  var recipientPhoneTouched by remember { mutableStateOf(false) }
  var destinationAddressTouched by remember { mutableStateOf(false) }

  // --- Real-time Validation Rules ---
  val isSenderNameValid by remember {
    derivedStateOf { senderName.trim().length >= 2 }
  }
  val isSenderPhoneValid by remember {
    derivedStateOf {
      val digits = senderPhone.filter { it.isDigit() }
      digits.length in 8..14
    }
  }
  val isSenderCommuneValid by remember {
    derivedStateOf { senderCommune.isNotBlank() }
  }
  val isRecipientNameValid by remember {
    derivedStateOf { recipientName.trim().length >= 2 }
  }
  val isRecipientPhoneValid by remember {
    derivedStateOf {
      val digits = recipientPhone.filter { it.isDigit() }
      digits.length in 8..14
    }
  }
  val isDestinationCityValid by remember {
    derivedStateOf { selectedDestination.cityName.isNotBlank() }
  }
  val isDestinationAddressValid by remember {
    derivedStateOf { destinationAddress.trim().length >= 2 }
  }
  val isWeightValid by remember {
    derivedStateOf { weightKg > 0.0 }
  }

  val isFormValid by remember {
    derivedStateOf {
      isSenderNameValid &&
      isSenderPhoneValid &&
      isSenderCommuneValid &&
      isRecipientNameValid &&
      isRecipientPhoneValid &&
      isDestinationCityValid &&
      isDestinationAddressValid &&
      isWeightValid
    }
  }

  val latestReceipt by viewModel.latestReceipt.collectAsState()

  if (latestReceipt != null) {
    PaymentReceiptDialog(
      receipt = latestReceipt!!,
      parcel = viewModel.selectedParcel.collectAsState().value,
      onDismiss = {
        viewModel.dismissReceipt()
        viewModel.navigateTo(AppScreen.TRACKING)
      }
    )
  }

  // Live estimated price calculation: Base city (min 1500 FCFA) + weight + express
  val basePrice = selectedDestination.basePriceFcfa.coerceAtLeast(1500)
  val weightExtra = when {
    weightKg <= 1.0 -> 0
    weightKg <= 3.0 -> 500
    weightKg <= 6.0 -> 1000
    weightKg <= 10.0 -> 1500
    else -> ((weightKg - 10) * 200).roundToInt() + 2000
  }
  val expressExtra = if (isExpressService) 500 else 0
  val estimatedTotal = basePrice + weightExtra + expressExtra

  val abidjanCommunes = listOf("Cocody", "Yopougon", "Marcory", "Plateau", "Abobo", "Koumassi", "Port-Bouët", "Treichville", "Adjamé", "Bingerville", "Songon")
  val categories = listOf("Documents & Plis", "Colis standard", "Textile & Pagnes", "Cosmétiques & Vivriers", "Électronique & High-Tech", "Marchandise diverse")

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
      .testTag("pickup_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Title Banner
    item {
      Column {
        Text(
          text = "Demande d'Enlèvement de Colis",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 20.sp,
          color = IvoBlueDark
        )
        Text(
          text = "Collecte à domicile à Abidjan et acheminement vers toutes les villes de Côte d'Ivoire.",
          fontSize = 12.sp,
          color = Color(0xFF64748B)
        )
      }
    }

    // Google Sign-In & Cloud Firestore Sync
    item {
      GoogleSignInCard(
        currentUser = currentUser,
        onAuthChanged = {}
      )
    }

    // --- Real-time Validation Status Checklist Card ---
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("form_validation_status_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isFormValid) Color(0xFFECFDF5) else Color(0xFFFFFBEB)
        ),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (isFormValid) IvoGreenPrimary else Color(0xFFFCD34D)
        )
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (isFormValid) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isFormValid) Color(0xFF047857) else Color(0xFFD97706),
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isFormValid) "Formulaire complet & prêt pour Firestore" else "Validation en temps réel des champs obligatoires",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (isFormValid) Color(0xFF047857) else Color(0xFF92400E)
              )
            }

            Surface(
              color = if (isFormValid) IvoGreenPrimary else Color(0xFFF59E0B),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = if (isFormValid) "VALIDE" else "À COMPLÉTER",
                color = if (isFormValid) Color.Black else Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Mini Checklist indicators
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            ValidationCheckItem(
              label = "Expéditeur",
              isValid = isSenderNameValid && isSenderPhoneValid
            )
            ValidationCheckItem(
              label = "Destinataire",
              isValid = isRecipientNameValid && isRecipientPhoneValid
            )
            ValidationCheckItem(
              label = "Destination",
              isValid = isDestinationCityValid && isDestinationAddressValid
            )
          }
        }
      }
    }

    // Step 1: Expéditeur (Abidjan)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          SectionHeader(icon = Icons.Default.Person, title = "1. Expéditeur (Abidjan) *")

          Spacer(modifier = Modifier.height(12.dp))

          val showSenderNameError = (senderNameTouched || hasAttemptedSubmit) && !isSenderNameValid
          OutlinedTextField(
            value = senderName,
            onValueChange = {
              senderName = it
              senderNameTouched = true
            },
            label = { Text("Nom et Prénoms *") },
            placeholder = { Text("Ex: Koffi Serge") },
            isError = showSenderNameError,
            supportingText = {
              if (showSenderNameError) {
                Text("Le nom de l'expéditeur est obligatoire (min 2 caractères)", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
              }
            },
            trailingIcon = {
              if (isSenderNameValid) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Valide", tint = IvoGreenPrimary)
              } else if (showSenderNameError) {
                Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = "Erreur", tint = MaterialTheme.colorScheme.error)
              }
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_sender_name"),
            shape = RoundedCornerShape(10.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          val showSenderPhoneError = (senderPhoneTouched || hasAttemptedSubmit) && !isSenderPhoneValid
          OutlinedTextField(
            value = senderPhone,
            onValueChange = {
              senderPhone = it
              senderPhoneTouched = true
            },
            label = { Text("Numéro de Téléphone (WhatsApp) *") },
            placeholder = { Text("07 67 18 25 45") },
            leadingIcon = { Text("+225", fontWeight = FontWeight.Bold, color = IvoBluePrimary, modifier = Modifier.padding(start = 12.dp)) },
            isError = showSenderPhoneError,
            supportingText = {
              if (showSenderPhoneError) {
                Text("Numéro de téléphone ivoirien requis (min 8 chiffres)", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
              }
            },
            trailingIcon = {
              if (isSenderPhoneValid) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Valide", tint = IvoGreenPrimary)
              } else if (showSenderPhoneError) {
                Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = "Erreur", tint = MaterialTheme.colorScheme.error)
              }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_sender_phone"),
            shape = RoundedCornerShape(10.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(text = "Commune de ramassage à Abidjan * :", fontSize = 11.sp, color = Color.Gray)
          Spacer(modifier = Modifier.height(4.dp))

          FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            abidjanCommunes.forEach { commune ->
              val isSelected = senderCommune == commune
              Surface(
                color = if (isSelected) IvoBluePrimary else Color(0xFFF1F5F9),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .clickable { senderCommune = commune }
                  .padding(vertical = 2.dp)
              ) {
                Text(
                  text = commune,
                  color = if (isSelected) Color.White else Color(0xFF475569),
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = senderAddress,
            onValueChange = { senderAddress = it },
            label = { Text("Adresse précise de collecte (Abidjan)") },
            placeholder = { Text("Ex: Cocody Faya Nouveau Goudron, face pharmacie") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )
        }
      }
    }

    // Step 2: Destinataire (Intérieur CI)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          SectionHeader(icon = Icons.Default.LocationOn, title = "2. Destinataire (Côte d'Ivoire) *")

          Spacer(modifier = Modifier.height(12.dp))

          val showRecipientNameError = (recipientNameTouched || hasAttemptedSubmit) && !isRecipientNameValid
          OutlinedTextField(
            value = recipientName,
            onValueChange = {
              recipientName = it
              recipientNameTouched = true
            },
            label = { Text("Nom du Destinataire *") },
            placeholder = { Text("Ex: Awa Bamba") },
            isError = showRecipientNameError,
            supportingText = {
              if (showRecipientNameError) {
                Text("Le nom du destinataire est obligatoire", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
              }
            },
            trailingIcon = {
              if (isRecipientNameValid) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Valide", tint = IvoGreenPrimary)
              } else if (showRecipientNameError) {
                Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = "Erreur", tint = MaterialTheme.colorScheme.error)
              }
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_recipient_name"),
            shape = RoundedCornerShape(10.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          val showRecipientPhoneError = (recipientPhoneTouched || hasAttemptedSubmit) && !isRecipientPhoneValid
          OutlinedTextField(
            value = recipientPhone,
            onValueChange = {
              recipientPhone = it
              recipientPhoneTouched = true
            },
            label = { Text("Téléphone du Destinataire *") },
            placeholder = { Text("05 XX XX XX XX") },
            leadingIcon = { Text("+225", fontWeight = FontWeight.Bold, color = IvoBluePrimary, modifier = Modifier.padding(start = 12.dp)) },
            isError = showRecipientPhoneError,
            supportingText = {
              if (showRecipientPhoneError) {
                Text("Numéro du destinataire requis (min 8 chiffres)", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
              }
            },
            trailingIcon = {
              if (isRecipientPhoneValid) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Valide", tint = IvoGreenPrimary)
              } else if (showRecipientPhoneError) {
                Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = "Erreur", tint = MaterialTheme.colorScheme.error)
              }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_recipient_phone"),
            shape = RoundedCornerShape(10.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          val showDestAddressError = (destinationAddressTouched || hasAttemptedSubmit) && !isDestinationAddressValid
          OutlinedTextField(
            value = destinationAddress,
            onValueChange = {
              destinationAddress = it
              destinationAddressTouched = true
            },
            label = { Text("Gare de livraison ou adresse à destination *") },
            placeholder = { Text("Ex: Agence Gare Centrale UTB / Domicile quartier Commerce") },
            isError = showDestAddressError,
            supportingText = {
              if (showDestAddressError) {
                Text("Veuillez préciser la gare ou point relais de destination", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
              }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )
        }
      }
    }

    // Step 3: Composant 'ShippingCalculator' dynamique (base 1500 FCFA)
    item {
      ShippingCalculator(
        destinations = availableDestinations,
        selectedDestination = selectedDestination,
        onDestinationChange = { newDest ->
          selectedDestination = newDest
        },
        weightKg = weightKg,
        onWeightChange = { newWeight ->
          weightKg = newWeight
        },
        isExpressService = isExpressService,
        onExpressServiceChange = { isExpressService = it }
      )
    }

    // Step 4: Détails additionnels & Catégorie
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          SectionHeader(icon = Icons.Default.Inventory2, title = "4. Nature de la Marchandise")

          Spacer(modifier = Modifier.height(10.dp))

          Text(text = "Catégorie :", fontSize = 11.sp, color = Color.Gray)
          Spacer(modifier = Modifier.height(4.dp))

          FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            categories.forEach { cat ->
              val isSelected = parcelCategory == cat
              Surface(
                color = if (isSelected) IvoGreenContainer else Color(0xFFF1F5F9),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) IvoGreenPrimary else Color.Transparent),
                modifier = Modifier
                  .clickable { parcelCategory = cat }
                  .padding(vertical = 2.dp)
              ) {
                Text(
                  text = cat,
                  color = if (isSelected) Color(0xFF047857) else Color(0xFF475569),
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description du colis") },
            placeholder = { Text("Ex: 3 complets pagnes + boîte de chaussures") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )
        }
      }
    }

    // Step 5: Mode de Paiement
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          SectionHeader(icon = Icons.Default.CreditCard, title = "5. Règlement Sécurisé")

          Spacer(modifier = Modifier.height(10.dp))

          val paymentOptions = listOf(
            PaymentGatewayType.ORANGE_MONEY,
            PaymentGatewayType.WAVE,
            PaymentGatewayType.MTN_MOMO,
            PaymentGatewayType.MOOV_MONEY,
            PaymentGatewayType.CARTE_BANCAIRE,
            PaymentGatewayType.ESPECES_COLLECTE
          )

          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            paymentOptions.forEach { method ->
              val isSelected = chosenPaymentMethod == method
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSelected) method.brandColor.copy(alpha = 0.12f) else Color(0xFFF8FAFC))
                  .border(1.dp, if (isSelected) method.brandColor else Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                  .clickable { chosenPaymentMethod = method }
                  .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(22.dp)
                    .background(method.brandColor, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (method == PaymentGatewayType.CARTE_BANCAIRE) Icons.Default.CreditCard else Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                  )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = method.displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = method.description,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    maxLines = 1
                  )
                }

                if (isSelected) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = method.brandColor,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }

          if (chosenPaymentMethod != PaymentGatewayType.ESPECES_COLLECTE && chosenPaymentMethod != PaymentGatewayType.CARTE_BANCAIRE) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = payerPhone,
              onValueChange = { payerPhone = it },
              label = { Text("Numéro mobile de débit ${chosenPaymentMethod.name.take(6)}") },
              placeholder = { Text("Ex: 07 67 18 25 45") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp)
            )
          }
        }
      }
    }

    // Step 6: Confirmation & Envoi vers Firestore
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(
            Brush.horizontalGradient(
              listOf(IvoBlueDark, Color(0xFF0F3E68))
            )
          )
          .padding(18.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = "Total à régler", color = Color(0xFF94A3B8), fontSize = 12.sp)
              Row(verticalAlignment = Alignment.Bottom) {
                Text(
                  text = "$estimatedTotal",
                  color = Color.White,
                  fontSize = 26.sp,
                  fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "FCFA",
                  color = IvoGreenLight,
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp,
                  modifier = Modifier.padding(bottom = 3.dp)
                )
              }
            }

            Surface(
              color = Color(0x33FFFFFF),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "${selectedDestination.cityName} • ${selectedDestination.deliveryDelayHours}",
                color = Color.White,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          // Error warning above button if attempted submit with missing fields
          if (hasAttemptedSubmit && !isFormValid) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              color = Color(0x33EF4444),
              shape = RoundedCornerShape(8.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Veuillez renseigner tous les champs obligatoires (expéditeur, destinataire, ville).",
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = {
              hasAttemptedSubmit = true
              if (!isFormValid) {
                return@Button
              }

              // Valid submission to Firestore and local Room DB
              viewModel.submitPickupOrder(
                senderName = senderName.trim(),
                senderPhone = senderPhone.trim(),
                senderCommune = senderCommune.trim(),
                senderAddress = senderAddress.trim(),
                recipientName = recipientName.trim(),
                recipientPhone = recipientPhone.trim(),
                destinationCity = selectedDestination.cityName,
                destinationAddress = destinationAddress.trim(),
                parcelCategory = parcelCategory,
                weightKg = weightKg,
                description = if (description.isNotBlank()) description.trim() else "Colis standard $parcelCategory",
                chosenPaymentMethod = chosenPaymentMethod,
                payerPhone = if (payerPhone.isNotBlank()) payerPhone.trim() else senderPhone.trim()
              ) { newParcel ->
                WhatsAppHelper.openWhatsApp(
                  context,
                  """
                    Bonjour IVOCOLIS !
                    Je viens d'enregistrer une demande d'enlèvement de colis :
                    - Référence : ${newParcel.reference}
                    - Expéditeur : ${newParcel.senderName} (${newParcel.senderPhone})
                    - Collecte : Abidjan - ${newParcel.senderCommune} (${newParcel.senderAddress})
                    - Destinataire : ${newParcel.recipientName} à ${newParcel.destinationCity} (${newParcel.destinationAddress})
                    - Tarif calculé : ${newParcel.priceFcfa} FCFA
                    - Mode de règlement : ${chosenPaymentMethod.displayName}
                  """.trimIndent()
                )
              }
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isFormValid) IvoGreenPrimary else Color(0xFF64748B),
              contentColor = if (isFormValid) Color.Black else Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("submit_pickup_order_button")
          ) {
            Text(
              text = if (isFormValid) "Confirmer & Enregistrer (Firestore)" else "Compléter les champs requis",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
              imageVector = if (isFormValid) Icons.Default.CloudDone else Icons.Default.ArrowForward,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
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
private fun ValidationCheckItem(label: String, isValid: Boolean) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = if (isValid) Icons.Default.Check else Icons.Default.Warning,
      contentDescription = null,
      tint = if (isValid) Color(0xFF047857) else Color(0xFFD97706),
      modifier = Modifier.size(14.dp)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = if (isValid) FontWeight.Bold else FontWeight.Normal,
      color = if (isValid) Color(0xFF047857) else Color(0xFF92400E)
    )
  }
}

@Composable
private fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(30.dp)
        .background(IvoBluePrimary.copy(alpha = 0.1f), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(imageVector = icon, contentDescription = null, tint = IvoBluePrimary, modifier = Modifier.size(16.dp))
    }
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = title,
      fontWeight = FontWeight.Bold,
      fontSize = 14.sp,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}
