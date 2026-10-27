package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ParcelStatus
import com.example.data.model.PaymentStatus
import com.example.ui.theme.StatusCancelled
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusProcessing
import com.example.ui.theme.StatusTransit

@Composable
fun ParcelStatusBadge(
  status: ParcelStatus,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor, dotColor) = when (status) {
    ParcelStatus.DEMANDE_RECU -> Triple(Color(0xFFFEF3C7), Color(0xFF92400E), StatusPending)
    ParcelStatus.RAMASSE_ABIDJAN -> Triple(Color(0xFFE0E7FF), Color(0xFF3730A3), Color(0xFF6366F1))
    ParcelStatus.RECU_AGENCE -> Triple(Color(0xFFE0F2FE), Color(0xFF0369A1), StatusProcessing)
    ParcelStatus.EXPEDIE -> Triple(Color(0xFFEDE9FE), Color(0xFF6B21A8), StatusTransit)
    ParcelStatus.EN_TRANSIT -> Triple(Color(0xFFF3E8FF), Color(0xFF7E22CE), Color(0xFFA855F7))
    ParcelStatus.ARRIVE_DESTINATION -> Triple(Color(0xFFDCFCE7), Color(0xFF166534), Color(0xFF22C55E))
    ParcelStatus.LIVRE -> Triple(Color(0xFFD1FAE5), Color(0xFF065F46), StatusDelivered)
    ParcelStatus.ANNULE -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), StatusCancelled)
  }

  Row(
    modifier = modifier
      .background(bgColor, RoundedCornerShape(16.dp))
      .padding(horizontal = 10.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .background(dotColor, CircleShape)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = status.label,
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold
    )
  }
}

@Composable
fun PaymentStatusBadge(
  paymentStatus: PaymentStatus,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor) = when (paymentStatus) {
    PaymentStatus.NON_PAYE -> Pair(Color(0xFFFEE2E2), Color(0xFFB91C1C))
    PaymentStatus.PAYE_MOBILE_MONEY -> Pair(Color(0xFFDCFCE7), Color(0xFF15803D))
    PaymentStatus.PAYE_CARTE -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1))
    PaymentStatus.PAYE_A_LA_COLLECTE -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
  }

  Text(
    text = paymentStatus.label,
    color = textColor,
    fontSize = 11.sp,
    fontWeight = FontWeight.Bold,
    modifier = modifier
      .background(bgColor, RoundedCornerShape(6.dp))
      .padding(horizontal = 8.dp, vertical = 3.dp)
  )
}
