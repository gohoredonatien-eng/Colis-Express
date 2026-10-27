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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
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

/**
 * Calculateur dynamique de tarifs d'expédition IVOCOLIS.
 * Calcule automatiquement le coût en fonction du poids saisi et de la ville de destination
 * (Tarifs à partir de 1 500 FCFA avec départ d'Abidjan - Cocody Faya).
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShippingCalculator(
  destinations: List<Destination>,
  selectedDestination: Destination,
  onDestinationChange: (Destination) -> Unit,
  weightKg: Double,
  onWeightChange: (Double) -> Unit,
  isExpressService: Boolean = false,
  onExpressServiceChange: ((Boolean) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val availableDestinations = if (destinations.isNotEmpty()) destinations else DefaultDestinations.initialList
  var isDropdownExpanded by remember { mutableStateOf(false) }

  // Calculation details: base fare min 1500 FCFA
  val baseFare = selectedDestination.basePriceFcfa.coerceAtLeast(1500)
  val weightSurcharge = when {
    weightKg <= 1.0 -> 0
    weightKg <= 3.0 -> 500
    weightKg <= 6.0 -> 1000
    weightKg <= 10.0 -> 1500
    else -> ((weightKg - 10) * 200).roundToInt() + 2000
  }
  val expressSurcharge = if (isExpressService) 500 else 0
  val calculatedTotal = baseFare + weightSurcharge + expressSurcharge

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("shipping_calculator"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .background(IvoBluePrimary.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Calculate,
              contentDescription = null,
              tint = IvoBluePrimary,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "Calculateur de Tarifs d'Expédition",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Départ Abidjan ➔ Intérieur de la Côte d'Ivoire",
              fontSize = 11.sp,
              color = Color(0xFF64748B)
            )
          }
        }

        Surface(
          color = IvoGreenPrimary.copy(alpha = 0.15f),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = "Dès 1 500 FCFA",
            color = Color(0xFF047857),
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 1. Destination City
      Text(
        text = "Ville de destination :",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(4.dp))

      ExposedDropdownMenuBox(
        expanded = isDropdownExpanded,
        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = "${selectedDestination.cityName} (${selectedDestination.region}) — Base ${selectedDestination.basePriceFcfa} FCFA",
          onValueChange = {},
          readOnly = true,
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = IvoBluePrimary,
            unfocusedBorderColor = Color(0xFFCBD5E1)
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .menuAnchor()
            .fillMaxWidth()
            .testTag("calc_destination_selector")
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
                      text = "${dest.cityName} (${dest.region})",
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp
                    )
                    Text(
                      text = "Délai : ${dest.deliveryDelayHours}",
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
                onDestinationChange(dest)
                isDropdownExpanded = false
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Weight Slider & Presets
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Poids du colis :",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Surface(
          color = IvoBluePrimary,
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = String.format("%.1f kg", weightKg),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
          )
        }
      }

      Slider(
        value = weightKg.toFloat(),
        onValueChange = { onWeightChange((it * 2).roundToInt() / 2.0) },
        valueRange = 0.5f..20f,
        steps = 38,
        colors = SliderDefaults.colors(
          thumbColor = IvoBluePrimary,
          activeTrackColor = IvoBluePrimary,
          inactiveTrackColor = Color(0xFFE2E8F0)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("calc_weight_slider")
      )

      // Quick Weight presets
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        listOf(1.0 to "Pli (1 kg)", 2.5 to "Colis (2.5 kg)", 5.0 to "Carton (5 kg)", 10.0 to "Lourd (10 kg)").forEach { (w, label) ->
          val isSelected = (weightKg - w).let { it >= -0.3 && it <= 0.3 }
          Surface(
            color = if (isSelected) IvoBlueLight.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .clickable { onWeightChange(w) }
              .border(
                1.dp,
                if (isSelected) IvoBluePrimary else Color.Transparent,
                RoundedCornerShape(10.dp)
              )
          ) {
            Text(
              text = label,
              fontSize = 11.sp,
              color = if (isSelected) IvoBluePrimary else Color(0xFF475569),
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      // 3. Option Service Rapide (Optionnel)
      if (onExpressServiceChange != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isExpressService) IvoGreenLight.copy(alpha = 0.15f) else Color(0xFFF8FAFC))
            .border(1.dp, if (isExpressService) IvoGreenPrimary else Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
            .clickable { onExpressServiceChange(!isExpressService) }
            .padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.ElectricBolt,
              contentDescription = null,
              tint = if (isExpressService) Color(0xFFD97706) else Color.Gray,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Option Express Chrono (+500 FCFA)",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Départ prioritaire premier car direct",
                fontSize = 10.sp,
                color = Color.Gray
              )
            }
          }

          if (isExpressService) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = IvoGreenPrimary,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 4. Dynamic Summary Box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(
            Brush.horizontalGradient(
              listOf(IvoBlueDark, Color(0xFF0F3E68))
            )
          )
          .padding(14.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Tarif calculé automatiquement",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
              Row(verticalAlignment = Alignment.Bottom) {
                Text(
                  text = "$calculatedTotal",
                  color = Color.White,
                  fontSize = 24.sp,
                  fontWeight = FontWeight.ExtraBold,
                  modifier = Modifier.testTag("calc_total_price")
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "FCFA",
                  color = IvoGreenLight,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(bottom = 3.dp)
                )
              }
            }

            Surface(
              color = Color(0x33FFFFFF),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "${selectedDestination.cityName} • ${selectedDestination.deliveryDelayHours}",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))
          Divider(color = Color(0x22FFFFFF))
          Spacer(modifier = Modifier.height(6.dp))

          // Detail breakdown
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Base ville ($baseFare F) + Poids ($weightSurcharge F)${if (expressSurcharge > 0) " + Express ($expressSurcharge F)" else ""}",
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
        }
      }
    }
  }
}
