package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.IvoBlueDark
import com.example.ui.theme.IvoBluePrimary
import com.example.ui.theme.IvoGreenLight
import com.example.ui.theme.IvoGreenPrimary
import com.example.ui.util.WhatsAppHelper

@Composable
fun BrandHeader(
  modifier: Modifier = Modifier,
  onOpenSeoGuide: () -> Unit = {}
) {
  val context = LocalContext.current

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 2.dp,
    shadowElevation = 3.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.verticalGradient(
            listOf(
              IvoBlueDark,
              Color(0xFF0F2B48)
            )
          )
        )
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Logo + Company Branding
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(12.dp))
              .border(1.5.dp, IvoGreenLight, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = R.drawable.ivocolis_logo),
              contentDescription = "Logo IVOCOLIS",
              modifier = Modifier.size(46.dp),
              contentScale = ContentScale.Crop
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "IVOCOLIS",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Color.White,
                letterSpacing = 0.5.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = IvoGreenPrimary,
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = "EXPRESS CI",
                  color = Color.Black,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
              }
            }

            Text(
              text = "Abidjan ➔ Intérieur de la Côte d'Ivoire",
              color = Color(0xFF94A3B8),
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        // WhatsApp Direct Quick Action
        Button(
          onClick = {
            WhatsAppHelper.openWhatsApp(
              context,
              "Bonjour IVOCOLIS Express, je souhaite me renseigner sur un envoi de colis depuis Abidjan."
            )
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF25D366),
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(20.dp),
          modifier = Modifier
            .height(38.dp)
            .testTag("header_whatsapp_button")
        ) {
          Icon(
            imageVector = Icons.Default.Call,
            contentDescription = "WhatsApp",
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "WhatsApp",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Location & Direct Contact Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0x22FFFFFF), RoundedCornerShape(8.dp))
          .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = IvoGreenLight,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Abidjan - Cocody Faya Nouveau Goudron",
            color = Color(0xFFE2E8F0),
            fontSize = 11.sp,
            maxLines = 1
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { WhatsAppHelper.openDialer(context) },
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Call,
              contentDescription = "Appeler",
              tint = Color.White,
              modifier = Modifier.size(15.dp)
            )
          }

          IconButton(
            onClick = {
              WhatsAppHelper.sendEmail(
                context,
                "Demande d'information - IVOCOLIS",
                "Bonjour l'équipe IVOCOLIS Express,"
              )
            },
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Email,
              contentDescription = "Email",
              tint = Color.White,
              modifier = Modifier.size(15.dp)
            )
          }
        }
      }
    }
  }
}
