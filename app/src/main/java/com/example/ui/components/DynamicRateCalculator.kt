package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DefaultDestinations
import com.example.data.model.Destination
import com.example.ui.theme.IvoBlueDark
import com.example.ui.theme.IvoBlueLight
import com.example.ui.theme.IvoBluePrimary
import com.example.ui.theme.IvoGreenLight
import com.example.ui.theme.IvoGreenPrimary
import kotlin.math.roundToInt

enum class DeliverySpeedService(
  val label: String,
  val delayText: String,
  val priceExtraFcfa: Int,
  val description: String
) {
  STANDARD(
    label = "Service Standard",
    delayText = "24h à 48h",
    priceExtraFcfa = 0,
    description = "Acheminement régulier sécurisé"
  ),
  EXPRESS(
    label = "Service Express Chrono",
    delayText = "24h garanti",
    priceExtraFcfa = 500,
    description = "Départ prioritaire par premier car"
  )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DynamicRateCalculator(
  destinations: List<Destination>,
  modifier: Modifier = Modifier,
  onBookWithQuote: (city: String, weight: Double, isExpress: Boolean, price: Int) -> Unit = { _, _, _, _ -> }
) {
  val availableDestinations = if (destinations.isNotEmpty()) destinations else DefaultDestinations.initialList

  var selectedDestination by remember {
    mutableStateOf(availableDestinations.firstOrNull { it.cityName == "Bouaké" } ?: availableDestinations.first())
  }
  var isDropdownExpanded by remember { mutableStateOf(false) }

  var weightKg by remember { mutableDoubleStateOf(1.5) }
  var selectedService by remember { mutableStateOf(DeliverySpeedService.STANDARD) }
  var isHomePickupSelected by remember { mutableStateOf(true) }

  // Calculation logic:
  // Base city price (min 1500 FCFA)
  val basePrice = selectedDestination.basePriceFcfa
  val weightExtra = when {
    weightKg <= 1.0 -> 0
    weightKg <= 3.0 -> 500
    weightKg <= 6.0 -> 1000
    weightKg <= 10.0 -> 1500
    else -> ((weightKg - 10) * 200).roundToInt() + 2000
  }
  val serviceExtra = selectedService.priceExtraFcfa
  val pickupExtra = if (isHomePickupSelected) 0 else 0 // Ramassage à Abidjan inclus dans le forfait de bienvenue!
  val totalEstimatedPrice = basePrice + weightExtra + serviceExtra + pickupExtra

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("dynamic_rate_calculator"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      // Header tag
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
              imageVector = Icons.Default.LocalShipping,
              contentDescription = null,
              tint = IvoBluePrimary,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Calculateur de Tarifs Dynamique",
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Départ Abidjan ➔ Intérieur de la Côte d'Ivoire",
              fontSize = 12.sp,
              color = Color(0xFF64748B)
            )
          }
        }

        Surface(
          color = IvoGreenPrimary.copy(alpha = 0.15f),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(
            text = "Dès 1 500 FCFA",
            color = Color(0xFF047857),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 1. Destination Dropdown
      Text(
        text = "1. Ville de destination en Côte d'Ivoire",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(6.dp))

      ExposedDropdownMenuBox(
        expanded = isDropdownExpanded,
        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = "${selectedDestination.cityName} (${selectedDestination.region}) - Base ${selectedDestination.basePriceFcfa} FCFA",
          onValueChange = {},
          readOnly = true,
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = IvoBluePrimary,
            unfocusedBorderColor = Color(0xFFCBD5E1)
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .menuAnchor()
            .fillMaxWidth()
            .testTag("destination_selector_input")
        )

        ExposedDropdownMenu(
          expanded = isDropdownExpanded,
          onDismissRequest = { isDropdownExpanded = false }
        ) {
          availableDestinations.forEach { dest ->
            DropdownMenuItem(
              text = {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(
                      text = dest.cityName,
                      fontWeight = FontWeight.Bold,
                      fontSize = 14.sp
                    )
                    Text(
                      text = "${dest.region} • ${dest.deliveryDelayHours}",
                      fontSize = 11.sp,
                      color = Color.Gray
                    )
                  }
                  Text(
                    text = "${dest.basePriceFcfa} FCFA",
                    fontWeight = FontWeight.ExtraBold,
                    color = IvoBluePrimary,
                    fontSize = 13.sp
                  )
                }
              },
              onClick = {
                selectedDestination = dest
                isDropdownExpanded = false
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 2. Weight Slider & Input
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "2. Poids estimé du colis",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Surface(
          color = IvoBluePrimary,
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = String.format("%.1f kg", weightKg),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
          )
        }
      }

      Slider(
        value = weightKg.toFloat(),
        onValueChange = { weightKg = (it * 2).roundToInt() / 2.0 },
        valueRange = 0.5f..20f,
        steps = 38,
        colors = SliderDefaults.colors(
          thumbColor = IvoBluePrimary,
          activeTrackColor = IvoBluePrimary,
          inactiveTrackColor = Color(0xFFE2E8F0)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("weight_slider")
      )

      // Quick Weight Pills
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(0.5 to "Pli (<1kg)", 2.0 to "Colis 2kg", 5.0 to "Carton 5kg", 10.0 to "Lourd 10kg").forEach { (w, label) ->
          val isSelected = (weightKg - w).let { it >= -0.3 && it <= 0.3 }
          Surface(
            color = if (isSelected) IvoBlueLight.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .clickable { weightKg = w }
              .border(
                1.dp,
                if (isSelected) IvoBlueLight else Color.Transparent,
                RoundedCornerShape(14.dp)
              )
          ) {
            Text(
              text = label,
              fontSize = 11.sp,
              color = if (isSelected) IvoBluePrimary else Color(0xFF475569),
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 3. Service Choice (Standard vs Express)
      Text(
        text = "3. Formule de rapidité",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        DeliverySpeedService.values().forEach { service ->
          val isSelected = selectedService == service
          Card(
            modifier = Modifier
              .weight(1f)
              .clickable { selectedService = service }
              .border(
                1.5.dp,
                if (isSelected) IvoGreenPrimary else Color(0xFFE2E8F0),
                RoundedCornerShape(12.dp)
              ),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) IvoGreenLight.copy(alpha = 0.08f) else Color(0xFFF8FAFC)
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = if (service == DeliverySpeedService.EXPRESS) Icons.Default.ElectricBolt else Icons.Default.Schedule,
                  contentDescription = null,
                  tint = if (service == DeliverySpeedService.EXPRESS) Color(0xFFD97706) else IvoBluePrimary,
                  modifier = Modifier.size(18.dp)
                )
                if (isSelected) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = IvoGreenPrimary,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = if (service == DeliverySpeedService.EXPRESS) "Express 24h" else "Standard",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = service.delayText,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = if (service.priceExtraFcfa == 0) "Inclus" else "+${service.priceExtraFcfa} FCFA",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (service.priceExtraFcfa == 0) IvoGreenPrimary else Color(0xFFD97706)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 4. Breakdown & Total Box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(
            Brush.horizontalGradient(
              listOf(IvoBlueDark, Color(0xFF0F3E68))
            )
          )
          .padding(16.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Tarif Total Estimé",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
              Row(verticalAlignment = Alignment.Bottom) {
                Text(
                  text = "$totalEstimatedPrice",
                  color = Color.White,
                  fontSize = 28.sp,
                  fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "FCFA",
                  color = IvoGreenLight,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(bottom = 4.dp)
                )
              }
            }

            Surface(
              color = Color(0x33FFFFFF),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "Délai : ${selectedService.delayText}",
                color = Color(0xFFE2E8F0),
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Divider(color = Color(0x22FFFFFF))
          Spacer(modifier = Modifier.height(8.dp))

          // Mini price breakdown
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Base ${selectedDestination.cityName} ($basePrice F) + Poids ($weightExtra F) + ${selectedService.label} ($serviceExtra F)",
              color = Color(0xFFCBD5E1),
              fontSize = 10.sp
            )
            Text(
              text = "Ramassage Cocody Inclus",
              color = IvoGreenLight,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = {
              onBookWithQuote(
                selectedDestination.cityName,
                weightKg,
                selectedService == DeliverySpeedService.EXPRESS,
                totalEstimatedPrice
              )
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = IvoGreenPrimary,
              contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("book_with_quote_button")
          ) {
            Text(
              text = "Expédier ce colis avec ce tarif",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
              imageVector = Icons.Default.ArrowForward,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}
