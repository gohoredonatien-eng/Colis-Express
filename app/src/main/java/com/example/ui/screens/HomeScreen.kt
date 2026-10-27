package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Destination
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.navigation.AppDestination
import com.example.ui.components.BrandHeader
import com.example.ui.components.DynamicRateCalculator
import com.example.ui.components.SeoGuideDialog
import com.example.ui.theme.IvoBlueDark
import com.example.ui.theme.IvoBlueLight
import com.example.ui.theme.IvoBluePrimary
import com.example.ui.theme.IvoGreenContainer
import com.example.ui.theme.IvoGreenLight
import com.example.ui.theme.IvoGreenPrimary
import com.example.ui.util.WhatsAppHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val destinations by viewModel.activeDestinations.collectAsState()
  var showSeoDialog by remember { mutableStateOf(false) }

  if (showSeoDialog) {
    SeoGuideDialog(onDismiss = { showSeoDialog = false })
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("home_screen"),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    // Header
    item {
      BrandHeader(onOpenSeoGuide = { showSeoDialog = true })
    }

    // Google Sign-In & Firebase Firestore Cloud Sync Card
    item {
      val currentUser by viewModel.currentUser.collectAsState()
      Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        com.example.ui.components.GoogleSignInCard(
          currentUser = currentUser,
          onAuthChanged = {}
        )
      }
    }

    // Hero Banner Section
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .clip(RoundedCornerShape(20.dp))
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_hero_banner),
          contentDescription = "Bannière IVOCOLIS Abidjan",
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
          contentScale = ContentScale.Crop
        )

        // Gradient overlay
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
              Brush.verticalGradient(
                listOf(
                  Color.Transparent,
                  IvoBlueDark.copy(alpha = 0.85f),
                  IvoBlueDark
                )
              )
            )
        )

        Column(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp)
        ) {
          Surface(
            color = IvoGreenPrimary,
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = "TARIF EXPRESS DÈS 1 500 FCFA",
              color = Color.Black,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 10.sp,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Vos colis expédiés en toute sécurité depuis Abidjan",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            lineHeight = 22.sp
          )

          Text(
            text = "Cocody Faya ➔ Yamoussoukro, Bouaké, San-Pedro, Korhogo & toute la CI",
            color = Color(0xFFCBD5E1),
            fontSize = 12.sp
          )
        }
      }
    }

    // Core Navigation Cards: Réservation & Suivi
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "Que souhaitez-vous faire ?",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = MaterialTheme.colorScheme.onSurface
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Card 1: Navigation vers la Réservation / Commande d'enlèvement
          Card(
            modifier = Modifier
              .weight(1f)
              .clickable { viewModel.navigateTo(AppScreen.PICKUP) }
              .testTag("home_nav_booking_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = IvoBluePrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.LocalShipping,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "Réserver un envoi",
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontSize = 14.sp
              )
              Text(
                text = "Collecte à Abidjan vers l'intérieur",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp,
                lineHeight = 14.sp
              )
              Spacer(modifier = Modifier.height(8.dp))
              Surface(
                color = IvoGreenPrimary,
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "Dès 1 500 FCFA",
                  color = Color.Black,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }

          // Card 2: Navigation vers le Suivi de colis
          Card(
            modifier = Modifier
              .weight(1f)
              .clickable { viewModel.navigateTo(AppScreen.TRACKING) }
              .testTag("home_nav_tracking_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, IvoGreenPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .background(IvoGreenPrimary.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = null,
                  tint = Color(0xFF047857),
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "Suivi de colis",
                fontWeight = FontWeight.ExtraBold,
                color = IvoBlueDark,
                fontSize = 14.sp
              )
              Text(
                text = "Direct Cloud Firestore",
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                lineHeight = 14.sp
              )
              Spacer(modifier = Modifier.height(8.dp))
              Surface(
                color = IvoBlueDark,
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "En temps réel",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }

        // WhatsApp direct contact quick banner
        Surface(
          color = Color(0xFF25D366).copy(alpha = 0.12f),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.4f)),
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              WhatsAppHelper.openWhatsApp(
                context,
                "Bonjour IVOCOLIS, je vous contacte depuis la page d'accueil pour une information sur l'envoi de colis."
              )
            }
            .testTag("home_whatsapp_banner")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Call,
              contentDescription = "WhatsApp",
              tint = Color(0xFF16A34A),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Besoin d'aide ou d'un devis express ?",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF14532D)
              )
              Text(
                text = "Contactez-nous sur WhatsApp au 07 67 18 25 45",
                fontSize = 11.sp,
                color = Color(0xFF166534)
              )
            }
            Surface(
              color = Color(0xFF25D366),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "Écrire",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        // User History quick shortcut banner
        Surface(
          color = IvoBluePrimary.copy(alpha = 0.08f),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, IvoBluePrimary.copy(alpha = 0.25f)),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.navigateTo(AppDestination.HISTORY) }
            .testTag("home_user_history_banner")
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .background(IvoBluePrimary.copy(alpha = 0.15f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = IvoBluePrimary,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Mes Expéditions & Historique",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = IvoBlueDark
              )
              Text(
                text = "Progression étape par étape de vos colis envoyés",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
              )
            }
            Surface(
              color = IvoBluePrimary,
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "Consulter",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }

    // Dynamic Shipping Rate Calculator
    item {
      Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        DynamicRateCalculator(
          destinations = destinations,
          onBookWithQuote = { city, weight, isExpress, price ->
            viewModel.setCalcParams(city = city, weight = weight)
            viewModel.navigateTo(AppScreen.PICKUP)
          }
        )
      }
    }

    // Destinations & Rates Grid (A partir de 1500 FCFA)
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Destinations & Tarifs en Côte d'Ivoire",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Expéditions quotidiennes depuis notre hub d'Abidjan",
              fontSize = 12.sp,
              color = Color.Gray
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          destinations.forEach { dest ->
            Card(
              modifier = Modifier
                .width(165.dp)
                .clickable {
                  viewModel.setCalcParams(city = dest.cityName)
                  viewModel.navigateTo(AppScreen.PICKUP)
                },
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              shape = RoundedCornerShape(14.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = dest.cityName,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = IvoBlueDark
                  )
                  if (dest.isPopular) {
                    Surface(
                      color = IvoGreenPrimary.copy(alpha = 0.2f),
                      shape = RoundedCornerShape(4.dp)
                    ) {
                      Text(
                        text = "Top",
                        color = Color(0xFF047857),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                      )
                    }
                  }
                }

                Text(
                  text = "Région : ${dest.region}",
                  fontSize = 11.sp,
                  color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "${dest.basePriceFcfa} F",
                    color = IvoBluePrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                  )
                  Text(
                    text = dest.deliveryDelayHours,
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }
          }
        }
      }
    }

    // Services Presentation
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      ) {
        Text(
          text = "Pourquoi choisir IVOCOLIS ?",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp
        )
        Text(
          text = "La solution de transport de référence pour vos marchandises",
          fontSize = 12.sp,
          color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          ServiceFeatureItem(
            icon = Icons.Default.LocationOn,
            iconTint = IvoBluePrimary,
            title = "Collecte à Domicile ou Bureau à Abidjan",
            description = "Notre coursier passe récupérer votre colis directement à votre adresse (Cocody, Yopougon, Marcory, Plateau, etc.)."
          )
          ServiceFeatureItem(
            icon = Icons.Default.Speed,
            iconTint = Color(0xFFD97706),
            title = "Acheminement Express 24h Garanti",
            description = "Départs chaque matin par cars et véhicules sécurisés vers Bouaké, Yamoussoukro, San-Pedro, Korhogo, etc."
          )
          ServiceFeatureItem(
            icon = Icons.Default.Payments,
            iconTint = Color(0xFF16A34A),
            title = "Paiement Mobile Money Sécurisé (CI)",
            description = "Réglez directement en ligne via Orange Money, Wave, MTN MoMo, Moov Money, ou en espèces au ramassage."
          )
          ServiceFeatureItem(
            icon = Icons.Default.Security,
            iconTint = IvoBluePrimary,
            title = "Suivi en Temps Réel & Alertes WhatsApp",
            description = "Recevez un numéro de référence unique et partagez le statut en direct avec votre destinataire à l'intérieur."
          )
        }
      }
    }

    // Google Visibility & SEO Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .clickable { showSeoDialog = true },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .background(IvoBluePrimary, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Language,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Visibilité Google & SEO",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = IvoGreenPrimary,
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = "OPTIMISÉ CI",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.Black,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = "Consulter les mots-clés, balises meta et stratégie de liens Google pour IVOCOLIS.",
              fontSize = 11.sp,
              color = Color(0xFF475569)
            )
          }
        }
      }
    }

    // FAQ Section
    item {
      FaqSection(modifier = Modifier.padding(horizontal = 16.dp))
    }

    // Contact & Physical Location Footer
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = IvoBlueDark)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "IVOCOLIS EXPRESS",
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            fontSize = 16.sp
          )
          Text(
            text = "Votre partenaire de confiance pour la logistique en Côte d'Ivoire",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp
          )

          Spacer(modifier = Modifier.height(14.dp))

          ContactRow(
            icon = Icons.Default.LocationOn,
            label = "Siège & Hub",
            value = "Abidjan - Cocody Faya Nouveau Goudron"
          )
          ContactRow(
            icon = Icons.Default.Call,
            label = "WhatsApp & Appel",
            value = "07 67 18 25 45 (+225)"
          )
          ContactRow(
            icon = Icons.Default.Email,
            label = "Email officiel",
            value = "ivocolisexpress@gmail.com"
          )

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = {
              WhatsAppHelper.openWhatsApp(
                context,
                "Bonjour IVOCOLIS, je souhaite faire expédier un colis vers l'intérieur."
              )
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF25D366),
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Discuter sur WhatsApp : 07 67 18 25 45", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}

@Composable
private fun ServiceFeatureItem(
  icon: ImageVector,
  iconTint: Color,
  title: String,
  description: String
) {
  Surface(
    color = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .background(iconTint.copy(alpha = 0.12f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(text = description, fontSize = 11.sp, color = Color(0xFF64748B), lineHeight = 15.sp)
      }
    }
  }
}

@Composable
private fun ContactRow(icon: ImageVector, label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(imageVector = icon, contentDescription = null, tint = IvoGreenLight, modifier = Modifier.size(16.dp))
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = "$label : ", fontSize = 11.sp, color = Color(0xFF94A3B8))
    Text(text = value, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
  }
}

@Composable
private fun FaqSection(modifier: Modifier = Modifier) {
  var expandedIndex by remember { mutableStateOf<Int?>(null) }

  val faqs = listOf(
    "Comment demander la collecte d'un colis à Abidjan ?" to
      "Rendez-vous dans l'onglet 'Expédier', saisissez vos coordonnées à Abidjan (Cocody, Yopougon, Marcory, etc.) et le numéro du destinataire. Un coursier Ivocolis passera collecter votre colis à votre porte.",
    "Quels sont les tarifs d'expédition ?" to
      "Nos tarifs démarrent à partir de 1 500 FCFA pour les plis et petits colis vers Yamoussoukro, Bouaké, et varient de 2 000 FCFA à 2 500 FCFA selon la destination et le poids.",
    "Quels sont les moyens de paiement acceptés ?" to
      "Vous pouvez régler directement en ligne en toute sécurité via Orange Money, Wave, MTN MoMo, Moov Money, Carte bancaire Visa/Mastercard, ou payer en espèces au coursier lors de la collecte.",
    "Comment suivre l'acheminement de mon colis ?" to
      "Dès l'enregistrement, un numéro de référence unique (ex: IVO-CI-8421) vous est attribué. Entrez ce numéro dans l'onglet 'Suivi' pour voir chaque étape en temps réel."
  )

  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = "Questions Fréquentes (FAQ)",
      fontWeight = FontWeight.Bold,
      fontSize = 16.sp
    )
    Text(
      text = "Tout savoir sur nos services d'expédition",
      fontSize = 12.sp,
      color = Color.Gray
    )

    Spacer(modifier = Modifier.height(10.dp))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      faqs.forEachIndexed { index, (question, answer) ->
        val isExpanded = expandedIndex == index
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { expandedIndex = if (isExpanded) null else index },
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = question,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
              )
              Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = IvoBluePrimary
              )
            }

            AnimatedVisibility(visible = isExpanded) {
              Column {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = answer,
                  fontSize = 12.sp,
                  color = Color(0xFF475569),
                  lineHeight = 16.sp
                )
              }
            }
          }
        }
      }
    }
  }
}
