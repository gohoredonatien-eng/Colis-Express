package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.data.model.Parcel
import com.example.data.model.ParcelStatus
import com.example.ui.MainViewModel
import com.example.ui.theme.IvoBlueDark
import com.example.ui.theme.IvoBluePrimary
import com.example.ui.theme.IvoGreenLight
import com.example.ui.theme.IvoGreenPrimary

/**
 * Boîte de dialogue et interface de scan de code QR pour agents logistiques IVOCOLIS.
 * Permet aux agents de scanner une étiquette de colis et de mettre à jour son statut instantanément dans Firestore.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QrScannerDialog(
  viewModel: MainViewModel,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val allParcels by viewModel.allParcels.collectAsState()

  var hasCameraPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
      ) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasCameraPermission = isGranted
  }

  LaunchedEffect(Unit) {
    if (!hasCameraPermission) {
      permissionLauncher.launch(Manifest.permission.CAMERA)
    }
  }

  var scannedRefInput by remember { mutableStateOf("") }
  var scannedParcel by remember { mutableStateOf<Parcel?>(null) }
  var selectedStatus by remember { mutableStateOf(ParcelStatus.EN_TRANSIT) }
  var locationInput by remember { mutableStateOf("Hub Abidjan Cocody Faya") }
  var commentInput by remember { mutableStateOf("Scanné par agent logistique") }
  var isUpdateSuccess by remember { mutableStateOf(false) }

  // Animated laser scan line
  val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
  val laserProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "laser_progress"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .testTag("qr_scanner_dialog"),
      color = Color(0xFF0F172A)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(20.dp)
      ) {
        // Top Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .background(IvoGreenPrimary.copy(alpha = 0.2f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                tint = IvoGreenPrimary,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Scanner Étiquette Colis",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
              Text(
                text = "Mise à jour instantanée Firestore",
                color = IvoGreenLight,
                fontSize = 11.sp
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .background(Color(0x33FFFFFF), CircleShape)
              .size(36.dp)
          ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scanner Viewfinder Area
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .testTag("qr_viewfinder_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
          border = androidx.compose.foundation.BorderStroke(2.dp, if (scannedParcel != null) IvoGreenPrimary else Color(0xFF334155))
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.radialGradient(
                  colors = listOf(Color(0xFF0F172A), Color(0xFF020617))
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            // Viewfinder reticle
            Box(
              modifier = Modifier
                .size(180.dp)
                .border(2.dp, IvoGreenPrimary.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .padding(12.dp),
              contentAlignment = Alignment.Center
            ) {
              // Corner decorative brackets
              Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.size(100.dp)
              )

              // Laser line animation
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(3.dp)
                  .offset { IntOffset(x = 0, y = ((laserProgress * 140) - 70).dp.roundToPx()) }
                  .background(
                    Brush.horizontalGradient(
                      listOf(
                        Color.Transparent,
                        IvoGreenLight,
                        IvoGreenPrimary,
                        IvoGreenLight,
                        Color.Transparent
                      )
                    )
                  )
              )
            }

            // Status indication on viewfinder
            Column(
              modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              if (scannedParcel != null) {
                Surface(
                  color = IvoGreenPrimary,
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text(
                    text = "COLIS DÉTECTÉ : ${scannedParcel!!.reference}",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                  )
                }
              } else {
                Text(
                  text = "Cadrez le QR Code ou Code-barres de l'étiquette",
                  color = Color(0xFF94A3B8),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Pick & Simulator for Instant Testing on Real Devices and Cloud Emulator
        Text(
          text = "Sélection rapide ou saisie de référence :",
          color = Color(0xFFCBD5E1),
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = scannedRefInput,
            onValueChange = { scannedRefInput = it },
            placeholder = { Text("Ex: IVO-XXXXXX", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("qr_ref_input"),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = IvoGreenPrimary,
              unfocusedBorderColor = Color(0xFF475569)
            ),
            shape = RoundedCornerShape(10.dp)
          )

          Spacer(modifier = Modifier.width(8.dp))

          Button(
            onClick = {
              val target = allParcels.firstOrNull { it.reference.equals(scannedRefInput.trim(), ignoreCase = true) }
              if (target != null) {
                scannedParcel = target
                selectedStatus = target.status
                isUpdateSuccess = false
              } else {
                // If not found, create a temporary matched parcel structure or alert
                val targetLoose = allParcels.firstOrNull { it.reference.contains(scannedRefInput.trim(), ignoreCase = true) }
                if (targetLoose != null) {
                  scannedParcel = targetLoose
                  selectedStatus = targetLoose.status
                  isUpdateSuccess = false
                }
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = IvoBluePrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.height(52.dp)
          ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Rechercher", tint = Color.White)
          }
        }

        // Available active parcels chips for 1-tap scan emulation
        if (allParcels.isNotEmpty()) {
          Spacer(modifier = Modifier.height(10.dp))
          Text(text = "Colis disponibles pour scan :", color = Color(0xFF94A3B8), fontSize = 11.sp)
          Spacer(modifier = Modifier.height(4.dp))
          FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            allParcels.take(5).forEach { p ->
              Surface(
                color = if (scannedParcel?.reference == p.reference) IvoGreenPrimary else Color(0xFF334155),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .clickable {
                    scannedParcel = p
                    scannedRefInput = p.reference
                    selectedStatus = p.status
                    isUpdateSuccess = false
                  }
                  .padding(vertical = 2.dp)
              ) {
                Text(
                  text = "${p.reference} (${p.destinationCity})",
                  color = if (scannedParcel?.reference == p.reference) Color.Black else Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scanned Parcel Information & Instant Status Actions
        AnimatedVisibility(
          visible = scannedParcel != null,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          val parcel = scannedParcel!!
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("scanned_parcel_details_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
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
                    fontSize = 16.sp,
                    color = IvoGreenLight
                  )
                  Text(
                    text = "${parcel.senderName} ➔ ${parcel.recipientName}",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                  )
                }

                Surface(
                  color = IvoBluePrimary.copy(alpha = 0.2f),
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text(
                    text = "${parcel.priceFcfa} FCFA",
                    color = IvoBluePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "Destination : ${parcel.destinationCity} • Statut actuel : ${parcel.status.label}",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
              )

              Spacer(modifier = Modifier.height(14.dp))
              Divider(color = Color(0xFF334155))
              Spacer(modifier = Modifier.height(14.dp))

              Text(
                text = "Sélectionnez le nouveau statut Firestore :",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )

              Spacer(modifier = Modifier.height(8.dp))

              // 1-Tap Status Selector Buttons
              val statusOptions = listOf(
                ParcelStatus.RECU_AGENCE to "📦 Reçu au Hub Cocody",
                ParcelStatus.EN_TRANSIT to "🚚 En transit vers l'intérieur",
                ParcelStatus.ARRIVE_DESTINATION to "🏁 Arrivé en Gare",
                ParcelStatus.LIVRE to "✅ Livré au destinataire"
              )

              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                statusOptions.forEach { (status, label) ->
                  val isSelected = selectedStatus == status
                  Surface(
                    color = if (isSelected) IvoGreenPrimary else Color(0xFF0F172A),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                      1.dp,
                      if (isSelected) IvoGreenLight else Color(0xFF334155)
                    ),
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable {
                        selectedStatus = status
                        locationInput = when (status) {
                          ParcelStatus.RECU_AGENCE -> "Hub Abidjan Cocody Faya"
                          ParcelStatus.EN_TRANSIT -> "En route vers ${parcel.destinationCity}"
                          ParcelStatus.ARRIVE_DESTINATION -> "Gare ${parcel.destinationCity}"
                          ParcelStatus.LIVRE -> "Remis au destinataire (${parcel.destinationCity})"
                          else -> "Agence IVOCOLIS"
                        }
                      }
                  ) {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(
                        text = label,
                        color = if (isSelected) Color.Black else Color.White,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp
                      )
                      if (isSelected) {
                        Icon(
                          imageVector = Icons.Default.CheckCircle,
                          contentDescription = null,
                          tint = Color.Black,
                          modifier = Modifier.size(16.dp)
                        )
                      }
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Location and note input
              OutlinedTextField(
                value = locationInput,
                onValueChange = { locationInput = it },
                label = { Text("Localisation de l'agent", color = Color.Gray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                  focusedTextColor = Color.White,
                  unfocusedTextColor = Color.White,
                  focusedBorderColor = IvoGreenPrimary,
                  unfocusedBorderColor = Color(0xFF475569)
                ),
                shape = RoundedCornerShape(8.dp)
              )

              Spacer(modifier = Modifier.height(8.dp))

              OutlinedTextField(
                value = commentInput,
                onValueChange = { commentInput = it },
                label = { Text("Note d'acheminement", color = Color.Gray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                  focusedTextColor = Color.White,
                  unfocusedTextColor = Color.White,
                  focusedBorderColor = IvoGreenPrimary,
                  unfocusedBorderColor = Color(0xFF475569)
                ),
                shape = RoundedCornerShape(8.dp)
              )

              Spacer(modifier = Modifier.height(16.dp))

              // Confirmation Button
              Button(
                onClick = {
                  viewModel.adminUpdateStatus(
                    ref = parcel.reference,
                    newStatus = selectedStatus,
                    location = locationInput.ifBlank { "Hub Cocody Faya" },
                    comment = commentInput.ifBlank { "Mise à jour par scan QR" }
                  )
                  isUpdateSuccess = true
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = IvoGreenPrimary,
                  contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("confirm_scan_update_button")
              ) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Enregistrer et synchroniser Firestore",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 13.sp
                )
              }

              if (isUpdateSuccess) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                  color = IvoGreenPrimary.copy(alpha = 0.2f),
                  shape = RoundedCornerShape(8.dp),
                  border = androidx.compose.foundation.BorderStroke(1.dp, IvoGreenPrimary)
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = IvoGreenPrimary,
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "Statut mis à jour avec succès dans Firestore ! Notification envoyée au client.",
                      color = IvoGreenLight,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}
