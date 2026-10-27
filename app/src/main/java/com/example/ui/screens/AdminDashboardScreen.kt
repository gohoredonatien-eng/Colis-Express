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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WhereToVote
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Parcel
import com.example.data.model.ParcelStatus
import com.example.data.model.PaymentGatewayType
import com.example.data.model.PaymentStatus
import com.example.data.repository.ParcelRepository
import com.example.ui.MainViewModel
import com.example.ui.components.GoogleSignInCard
import com.example.ui.components.ParcelStatusBadge
import com.example.ui.components.PaymentStatusBadge
import com.example.ui.components.QrScannerDialog
import com.example.ui.theme.IvoBlueDark
import com.example.ui.theme.IvoBluePrimary
import com.example.ui.theme.IvoGreenLight
import com.example.ui.theme.IvoGreenPrimary
import com.example.ui.util.WhatsAppHelper
import java.util.Calendar

/**
 * Vue 'AdminDashboard' permettant de :
 * 1. Lister tous les colis enregistrés dans Firestore et synchronisés en local.
 * 2. Mettre à jour rapidement leur statut avec des boutons dédiés (Reçu, En transit, Livré).
 * 3. Consulter le récapitulatif détaillé des recettes du jour (Total du jour, ventilation Mobile Money / Espèces).
 */
@Composable
fun AdminDashboard(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  AdminDashboardScreen(viewModel = viewModel, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdminDashboardScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()
  val allParcels by viewModel.allParcels.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()

  var pinInput by remember { mutableStateOf("") }
  var searchQuery by remember { mutableStateOf("") }
  var selectedStatusFilter by remember { mutableStateOf<ParcelStatus?>(null) }
  var showOnlyToday by remember { mutableStateOf(false) }

  var showQrScanner by remember { mutableStateOf(false) }
  var parcelToEdit by remember { mutableStateOf<Parcel?>(null) }
  var editTargetStatus by remember { mutableStateOf(ParcelStatus.EN_TRANSIT) }
  var editLocationInput by remember { mutableStateOf("") }
  var editCommentInput by remember { mutableStateOf("") }

  // PIN gate if not yet authenticated
  if (!isAdminLoggedIn) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .padding(20.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("admin_login_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(56.dp)
              .background(IvoBluePrimary.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = IvoBluePrimary,
              modifier = Modifier.size(30.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "Espace Admin IVOCOLIS",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 19.sp,
            color = IvoBlueDark
          )

          Text(
            text = "Gestion des colis Firestore, changement de statut et recettes du jour.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )

          Spacer(modifier = Modifier.height(18.dp))

          OutlinedTextField(
            value = pinInput,
            onValueChange = { if (it.length <= 6) pinInput = it },
            label = { Text("Code PIN d'accès") },
            placeholder = { Text("Défaut : 2026") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = { viewModel.loginAdmin(pinInput) },
            colors = ButtonDefaults.buttonColors(containerColor = IvoBluePrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("admin_login_button")
          ) {
            Text(text = "Accéder au Tableau de Bord", fontWeight = FontWeight.Bold)
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Code PIN d'accès par défaut : 2026",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
        }
      }
    }
    return
  }

  // QR Scanner modal dialog for logistic agents
  if (showQrScanner) {
    QrScannerDialog(
      viewModel = viewModel,
      onDismiss = { showQrScanner = false }
    )
  }

  // Edit status modal with custom notes & WhatsApp dispatch
  if (parcelToEdit != null) {
    val parcel = parcelToEdit!!
    var isExpanded by remember { mutableStateOf(false) }

    AlertDialog(
      onDismissRequest = { parcelToEdit = null },
      title = {
        Text(
          text = "Mettre à jour le statut du colis",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp
        )
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "Réf : ${parcel.reference} (${parcel.destinationCity})",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = IvoBluePrimary
          )

          Spacer(modifier = Modifier.height(12.dp))

          Text(text = "Nouveau statut :", fontSize = 11.sp, color = Color.Gray)

          ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded },
            modifier = Modifier.fillMaxWidth()
          ) {
            OutlinedTextField(
              value = editTargetStatus.label,
              onValueChange = {},
              readOnly = true,
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
              modifier = Modifier.menuAnchor().fillMaxWidth(),
              shape = RoundedCornerShape(8.dp)
            )

            ExposedDropdownMenu(
              expanded = isExpanded,
              onDismissRequest = { isExpanded = false }
            ) {
              ParcelStatus.values().forEach { st ->
                DropdownMenuItem(
                  text = { Text(st.label, fontWeight = FontWeight.Medium) },
                  onClick = {
                    editTargetStatus = st
                    isExpanded = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = editLocationInput,
            onValueChange = { editLocationInput = it },
            label = { Text("Localisation") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = editCommentInput,
            onValueChange = { editCommentInput = it },
            label = { Text("Note d'acheminement") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.adminUpdateStatus(
              ref = parcel.reference,
              newStatus = editTargetStatus,
              location = editLocationInput.ifBlank { "Agence Abidjan Cocody Faya" },
              comment = editCommentInput.ifBlank { "Statut actualisé" }
            )

            val updateMsg = """
              🔔 MISE À JOUR EXPÉDITION IVOCOLIS
              Votre colis ${parcel.reference} est désormais : ${editTargetStatus.label} !
              📍 Localisation : $editLocationInput
              📝 Note : $editCommentInput
              
              Merci de votre confiance.
              Agence Cocody Faya Nouveau Goudron (+225 07 67 18 25 45)
            """.trimIndent()

            WhatsAppHelper.shareTrackingViaWhatsApp(context, parcel.senderPhone, updateMsg)
            parcelToEdit = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = IvoGreenPrimary, contentColor = Color.Black)
        ) {
          Text(text = "Valider & Notifier WhatsApp", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { parcelToEdit = null }) {
          Text(text = "Annuler")
        }
      }
    )
  }

  // --- Financial Computations: Recettes du jour & Globales ---
  fun isToday(ts: Long): Boolean {
    val now = Calendar.getInstance()
    val itemCal = Calendar.getInstance().apply { timeInMillis = ts }
    return now.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR) &&
           now.get(Calendar.DAY_OF_YEAR) == itemCal.get(Calendar.DAY_OF_YEAR)
  }

  val todayParcels = allParcels.filter { isToday(it.createdAt) }
  val todayRevenue = todayParcels
    .filter { it.paymentStatus != PaymentStatus.NON_PAYE }
    .sumOf { it.priceFcfa }

  val totalAllTimeRevenue = allParcels
    .filter { it.paymentStatus != PaymentStatus.NON_PAYE }
    .sumOf { it.priceFcfa }

  val todayDeliveredCount = todayParcels.count { it.status == ParcelStatus.LIVRE }
  val todayInTransitCount = todayParcels.count { it.status == ParcelStatus.EN_TRANSIT || it.status == ParcelStatus.EXPEDIE }
  val todayReceivedCount = todayParcels.count { it.status == ParcelStatus.RECU_AGENCE || it.status == ParcelStatus.RAMASSE_ABIDJAN }

  // Revenue breakdown by payment method for today
  val todayOrangeRevenue = todayParcels.filter { it.paymentMethod?.contains("Orange", ignoreCase = true) == true }.sumOf { it.priceFcfa }
  val todayWaveRevenue = todayParcels.filter { it.paymentMethod?.contains("Wave", ignoreCase = true) == true }.sumOf { it.priceFcfa }
  val todayMtnRevenue = todayParcels.filter { it.paymentMethod?.contains("MTN", ignoreCase = true) == true }.sumOf { it.priceFcfa }
  val todayCashRevenue = todayParcels.filter { it.paymentStatus == PaymentStatus.PAYE_A_LA_COLLECTE }.sumOf { it.priceFcfa }

  // Filtered parcels list
  val filteredParcels = allParcels.filter { parcel ->
    val matchesToday = !showOnlyToday || isToday(parcel.createdAt)
    val matchesStatus = selectedStatusFilter == null || parcel.status == selectedStatusFilter
    val matchesSearch = searchQuery.isBlank() ||
      parcel.reference.contains(searchQuery, ignoreCase = true) ||
      parcel.recipientName.contains(searchQuery, ignoreCase = true) ||
      parcel.senderName.contains(searchQuery, ignoreCase = true) ||
      parcel.destinationCity.contains(searchQuery, ignoreCase = true)
    matchesToday && matchesStatus && matchesSearch
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
      .testTag("admin_dashboard_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Bar Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "AdminDashboard",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 20.sp,
              color = IvoBlueDark
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              color = IvoGreenPrimary.copy(alpha = 0.2f),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "FIRESTORE SYNC",
                color = Color(0xFF047857),
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Text(
            text = "Gestion des colis & suivi des encaissements",
            fontSize = 11.sp,
            color = Color.Gray
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { viewModel.refreshFirestoreData() },
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Actualiser Firestore",
              tint = IvoBluePrimary
            )
          }

          OutlinedButton(
            onClick = { viewModel.logoutAdmin() },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Sortir", fontSize = 11.sp)
          }
        }
      }
    }

    // Google Sign-In Card for Cloud Firestore syncing
    item {
      GoogleSignInCard(
        currentUser = currentUser,
        onAuthChanged = { viewModel.refreshFirestoreData() }
      )
    }

    // --- ESPACE AGENT LOGISTIQUE : SCAN ÉTIQUETTE QR & NOTIFICATIONS ---
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("logistic_agent_actions_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, IvoBluePrimary.copy(alpha = 0.3f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                  imageVector = Icons.Default.QrCodeScanner,
                  contentDescription = null,
                  tint = IvoBluePrimary,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Scan Rapide Agent Logistique",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = IvoBlueDark
                )
                Text(
                  text = "Mise à jour statut Firestore via QR / Code-barres",
                  fontSize = 11.sp,
                  color = Color.Gray
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { showQrScanner = true },
              colors = ButtonDefaults.buttonColors(containerColor = IvoGreenPrimary, contentColor = Color.Black),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1.3f)
                .height(44.dp)
                .testTag("open_qr_scanner_button")
            ) {
              Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "Scanner QR Colis", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            OutlinedButton(
              onClick = { viewModel.sendTestNotification() },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("test_notification_button")
            ) {
              Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp), tint = IvoBluePrimary)
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = "Alerte Test", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
          }
        }
      }
    }

    // --- RECAPITULATIF DES RECETTES DU JOUR ---
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("today_revenue_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, IvoGreenPrimary.copy(alpha = 0.5f))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.verticalGradient(
                listOf(IvoBlueDark, Color(0xFF0A2E50))
              )
            )
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = IvoGreenLight,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Recettes du Jour",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }

            Surface(
              color = IvoGreenPrimary,
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "${todayParcels.size} colis aujourd'hui",
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Big Today Revenue Display
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "$todayRevenue",
              color = Color.White,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 32.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "FCFA",
              color = IvoGreenLight,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              modifier = Modifier.padding(bottom = 4.dp)
            )
          }

          Text(
            text = "Total cumulé toutes dates : $totalAllTimeRevenue FCFA (${allParcels.size} colis)",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp
          )

          Spacer(modifier = Modifier.height(12.dp))
          Divider(color = Color(0x33FFFFFF))
          Spacer(modifier = Modifier.height(12.dp))

          // Today status stats
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            DashboardMetricItem(label = "Reçus au hub", value = "$todayReceivedCount")
            DashboardMetricItem(label = "En transit CI", value = "$todayInTransitCount")
            DashboardMetricItem(label = "Colis livrés", value = "$todayDeliveredCount")
          }

          // Payment method breakdown chips
          if (todayRevenue > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Ventilation des encaissements du jour :", color = Color(0xFFCBD5E1), fontSize = 10.sp)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              if (todayOrangeRevenue > 0) {
                PaymentMethodPill(name = "Orange Money", amount = todayOrangeRevenue, color = Color(0xFFFF7900))
              }
              if (todayWaveRevenue > 0) {
                PaymentMethodPill(name = "Wave", amount = todayWaveRevenue, color = Color(0xFF1EA8E7))
              }
              if (todayMtnRevenue > 0) {
                PaymentMethodPill(name = "MTN MoMo", amount = todayMtnRevenue, color = Color(0xFFFFCC00))
              }
              if (todayCashRevenue > 0) {
                PaymentMethodPill(name = "Espèces", amount = todayCashRevenue, color = IvoGreenPrimary)
              }
            }
          }
        }
      }
    }

    // Share report button
    item {
      val reportText = """
        📊 RAPPORT DES RECETTES DU JOUR - IVOCOLIS
        Hub : Cocody Faya Nouveau Goudron
        ------------------------------------------
        Recettes du jour : $todayRevenue FCFA
        Colis du jour : ${todayParcels.size}
        - Reçus à l'agence : $todayReceivedCount
        - En transit : $todayInTransitCount
        - Livrés : $todayDeliveredCount
        ------------------------------------------
        Recettes cumulées : $totalAllTimeRevenue FCFA
        Date : ${ParcelRepository.formatDate(System.currentTimeMillis())}
        Contact : 07 67 18 25 45
      """.trimIndent()

      Button(
        onClick = {
          WhatsAppHelper.shareGeneralText(context, "Rapport Recettes IVOCOLIS", reportText)
        },
        colors = ButtonDefaults.buttonColors(containerColor = IvoGreenPrimary, contentColor = Color.Black),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Partager les recettes du jour sur WhatsApp", fontWeight = FontWeight.Bold)
      }
    }

    // --- Search & Filters Section ---
    item {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Colis enregistrés (${filteredParcels.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = IvoBlueDark
          )

          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              color = if (showOnlyToday) IvoBluePrimary else Color(0xFFF1F5F9),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.clickable { showOnlyToday = !showOnlyToday }
            ) {
              Text(
                text = "Aujourd'hui",
                color = if (showOnlyToday) Color.White else Color(0xFF475569),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search textfield
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Rechercher par référence, nom ou ville...") },
          leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Effacer")
              }
            }
          },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filter Pills
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(
            null to "Tous",
            ParcelStatus.RECU_AGENCE to "Reçu",
            ParcelStatus.EN_TRANSIT to "En transit",
            ParcelStatus.LIVRE to "Livré"
          ).forEach { (st, label) ->
            val isSelected = selectedStatusFilter == st
            Surface(
              color = if (isSelected) IvoBluePrimary else Color(0xFFF1F5F9),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .clickable { selectedStatusFilter = st }
                .padding(vertical = 2.dp)
            ) {
              Text(
                text = label,
                color = if (isSelected) Color.White else Color(0xFF475569),
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }

    // Empty state
    if (filteredParcels.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.LocalShipping,
              contentDescription = null,
              tint = Color.Gray,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Aucun colis trouvé pour ces filtres.",
              fontWeight = FontWeight.Medium,
              color = Color.Gray,
              fontSize = 13.sp
            )
          }
        }
      }
    }

    // --- LIST OF PARCELS WITH QUICK STATUS BUTTONS ---
    items(filteredParcels.size) { index ->
      val parcel = filteredParcels[index]
      ParcelAdminCard(
        parcel = parcel,
        onQuickStatusUpdate = { newStatus ->
          viewModel.updateParcelStatusQuick(parcel.reference, newStatus)
        },
        onOpenEditDialog = {
          parcelToEdit = parcel
          editTargetStatus = parcel.status
          editLocationInput = "Hub Cocody Faya"
          editCommentInput = "Statut actualisé par l'administrateur"
        },
        onWhatsAppClient = {
          val msg = """
            Bonjour ${parcel.recipientName}, votre colis ${parcel.reference} expédié depuis Abidjan vers ${parcel.destinationCity} est actuellement : ${parcel.status.label}.
            Agence IVOCOLIS Cocody Faya Nouveau Goudron. Tél : 07 67 18 25 45.
          """.trimIndent()
          WhatsAppHelper.shareTrackingViaWhatsApp(context, parcel.recipientPhone.ifBlank { parcel.senderPhone }, msg)
        }
      )
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun ParcelAdminCard(
  parcel: Parcel,
  onQuickStatusUpdate: (ParcelStatus) -> Unit,
  onOpenEditDialog: () -> Unit,
  onWhatsAppClient: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("admin_parcel_item_${parcel.reference}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: Ref & Destination
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
            fontSize = 14.sp,
            color = IvoBlueDark
          )
          Text(
            text = "${parcel.senderName} ➔ ${parcel.recipientName}",
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        ParcelStatusBadge(status = parcel.status)
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "De: ${parcel.senderCommune} ➔ Vers: ${parcel.destinationCity} (${parcel.destinationAddress})",
        fontSize = 11.sp,
        color = Color(0xFF64748B)
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${parcel.priceFcfa} FCFA • ${parcel.weightKg} kg",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 13.sp,
          color = IvoBluePrimary
        )
        PaymentStatusBadge(paymentStatus = parcel.paymentStatus)
      }

      Spacer(modifier = Modifier.height(10.dp))
      Divider(color = Color(0xFFF1F5F9))
      Spacer(modifier = Modifier.height(8.dp))

      // Action label
      Text(
        text = "Mettre à jour le statut en 1 clic :",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Direct Quick-action buttons (Reçu, En transit, Livré)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Button: Reçu
        val isRecu = parcel.status == ParcelStatus.RECU_AGENCE || parcel.status == ParcelStatus.RAMASSE_ABIDJAN
        OutlinedButton(
          onClick = { onQuickStatusUpdate(ParcelStatus.RECU_AGENCE) },
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isRecu) IvoBluePrimary.copy(alpha = 0.15f) else Color.Transparent
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isRecu) IvoBluePrimary else Color(0xFFCBD5E1)
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .height(36.dp)
            .testTag("status_btn_recu_${parcel.reference}")
        ) {
          Text(
            text = "📦 Reçu",
            fontSize = 10.sp,
            fontWeight = if (isRecu) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isRecu) IvoBluePrimary else Color(0xFF334155)
          )
        }

        // Button: En transit
        val isTransit = parcel.status == ParcelStatus.EN_TRANSIT || parcel.status == ParcelStatus.EXPEDIE
        OutlinedButton(
          onClick = { onQuickStatusUpdate(ParcelStatus.EN_TRANSIT) },
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isTransit) Color(0xFFFEF3C7) else Color.Transparent
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isTransit) Color(0xFFD97706) else Color(0xFFCBD5E1)
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .height(36.dp)
            .testTag("status_btn_transit_${parcel.reference}")
        ) {
          Text(
            text = "🚚 Transit",
            fontSize = 10.sp,
            fontWeight = if (isTransit) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isTransit) Color(0xFFB45309) else Color(0xFF334155)
          )
        }

        // Button: Livré
        val isLivre = parcel.status == ParcelStatus.LIVRE
        OutlinedButton(
          onClick = { onQuickStatusUpdate(ParcelStatus.LIVRE) },
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isLivre) IvoGreenLight.copy(alpha = 0.2f) else Color.Transparent
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isLivre) IvoGreenPrimary else Color(0xFFCBD5E1)
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .height(36.dp)
            .testTag("status_btn_livre_${parcel.reference}")
        ) {
          Text(
            text = "✅ Livré",
            fontSize = 10.sp,
            fontWeight = if (isLivre) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isLivre) Color(0xFF047857) else Color(0xFF334155)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Secondary Actions: Edit note or WhatsApp notification
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onOpenEditDialog,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .height(34.dp)
        ) {
          Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Détails & Note", fontSize = 10.sp)
        }

        Button(
          onClick = onWhatsAppClient,
          colors = ButtonDefaults.buttonColors(containerColor = IvoGreenPrimary, contentColor = Color.Black),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .height(34.dp)
        ) {
          Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "WhatsApp", fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun DashboardMetricItem(label: String, value: String) {
  Column {
    Text(text = label, color = Color(0xFF94A3B8), fontSize = 10.sp)
    Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
  }
}

@Composable
private fun PaymentMethodPill(name: String, amount: Int, color: Color) {
  Surface(
    color = color.copy(alpha = 0.15f),
    shape = RoundedCornerShape(8.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
  ) {
    Text(
      text = "$name: $amount F",
      color = Color.White,
      fontSize = 10.sp,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
    )
  }
}
