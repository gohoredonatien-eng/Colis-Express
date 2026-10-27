package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.IvoBlueDark
import com.example.ui.theme.IvoBluePrimary
import com.example.ui.theme.IvoGreenLight
import com.example.ui.theme.IvoGreenPrimary
import com.example.ui.util.WhatsAppHelper

@Composable
fun SeoGuideDialog(onDismiss: () -> Unit) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  val fullSeoStrategyReport = """
=== GUIDE & STRATÉGIE SEO GOOGLE POUR IVOCOLIS ===

1. MOTS-CLÉS STRATÉGIQUES POUR LA CÔTE D'IVOIRE
------------------------------------------------
- "envoi de colis abidjan" (Intention transactionnelle forte)
- "livraison colis intérieur côte d'ivoire"
- "transport colis abidjan bouaké"
- "envoi rapide abidjan yamoussoukro"
- "coursier colis cocody faya"
- "tarif expédition colis côte d'ivoire à partir de 1500 fcfa"
- "service ramassage colis abidjan domicile"
- "fret colis express san pedro korhogo daloa"

2. BALISES META OPTIMISÉES POUR LE SITE WEB
-------------------------------------------
<title>IVOCOLIS Express | Envoi de Colis Abidjan & Intérieur Côte d'Ivoire dès 1500 FCFA</title>
<meta name="description" content="Expédition express et sécurisée de colis au départ d'Abidjan vers Bouaké, Yamoussoukro, Korhogo, San-Pedro. Collecte à domicile à Cocody Faya et paiement Mobile Money sécurisé." />
<meta name="keywords" content="envoi colis abidjan, livraison intérieur cote d ivoire, coursier cocody faya, transport colis bouaké, ivocolis express" />
<link rel="canonical" href="https://ivocolis.ci" />

3. DONNÉES STRUCTURÉES JSON-LD (SCHEMA.ORG)
-------------------------------------------
{
  "@context": "https://schema.org",
  "@type": "DeliveryService",
  "name": "IVOCOLIS Express",
  "image": "https://ivocolis.ci/logo.png",
  "telephone": "+2250767182545",
  "email": "ivocolisexpress@gmail.com",
  "address": {
    "@type": "PostalAddress",
    "streetAddress": "Nouveau Goudron",
    "addressLocality": "Cocody Faya, Abidjan",
    "addressCountry": "CI"
  },
  "priceRange": "1500 FCFA - 15000 FCFA",
  "openingHours": "Mo-Sa 07:30-19:00"
}

4. STRATÉGIE DE CONTENU & BLOG
------------------------------
- Articles de blog ciblés géographiquement : "Comment envoyer un colis d'Abidjan à Bouaké en moins de 24h", "Les 5 conseils pour emballer vos marchandises fragiles à Abidjan", "Tarifs et délais d'expédition vers San-Pédro et Korhogo".
- FAQ interactive optimisée pour les Featured Snippets Google (Questions fréquentes sur les tarifs à 1500 FCFA, le suivi et les paiements).

5. OBTENTION DE LIENS RETOUR (BACKLINKS QUALITATIFS)
-----------------------------------------------------
- Inscription sur Google My Business (Google Maps) pour la fiche locale "IVOCOLIS - Cocody Faya Nouveau Goudron".
- Référencement sur les annuaires d'entreprises ivoiriennes (Pages Jaunes CI, GoAfricaOnline, Abidjan.net).
- Partenariats avec des boutiques e-commerce d'Abidjan (vendeurs de pagnes, vêtements, cosmétiques sur Facebook et Instagram).
  """.trimIndent()

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.9f)
        .testTag("seo_guide_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Dialog Top Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(IvoBlueDark)
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .background(IvoGreenPrimary, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Visibilité Google & SEO",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
              Text(
                text = "Stratégie de Référencement IVOCOLIS",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Fermer",
              tint = Color.White
            )
          }
        }

        // Scrollable Content
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // Section 1: Mots clés
          item {
            SeoSectionCard(
              title = "1. Mots-clés Clés (Côte d'Ivoire)",
              subtitle = "Termes recherchés par les clients à Abidjan et à l'intérieur"
            ) {
              listOf(
                "envoi de colis abidjan" to "Volume très élevé • Intention d'achat directe",
                "livraison colis intérieur côte d'ivoire" to "Recherche ciblée transport province",
                "fret colis abidjan bouaké" to "Recherche commerçants & familles",
                "coursier express cocody faya" to "Référencement local quartier",
                "tarif colis côte d'ivoire dès 1500 fcfa" to "Attractivité tarifaire prix d'appel"
              ).forEach { (kw, desc) ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = IvoBluePrimary,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(text = kw, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = desc, fontSize = 11.sp, color = Color.Gray)
                  }
                }
              }
            }
          }

          // Section 2: Balises Meta
          item {
            SeoSectionCard(
              title = "2. Balises Meta Prêtes à Déployer",
              subtitle = "À intégrer dans l'en-tête HTML <head> du site web"
            ) {
              Surface(
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = """
<title>IVOCOLIS Express | Envoi de Colis Abidjan & CI dès 1500 FCFA</title>
<meta name="description" content="Expédition express et sécurisée de colis d'Abidjan vers l'intérieur (Bouaké, Yamoussoukro, Korhogo, San-Pédro). Collecte à Cocody Faya, paiement Mobile Money." />
<meta name="keywords" content="envoi colis abidjan, livraison cote d'ivoire, coursier cocody faya, transport colis" />
<link rel="canonical" href="https://ivocolis.ci" />
                  """.trimIndent(),
                  color = IvoGreenLight,
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace,
                  modifier = Modifier.padding(10.dp)
                )
              }
            }
          }

          // Section 3: Données Structurées JSON-LD
          item {
            SeoSectionCard(
              title = "3. Rich Snippets Schema.org (Google Local)",
              subtitle = "Permet d'afficher étoiles, horaires et tarifs dès les résultats Google"
            ) {
              Surface(
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = """
{
  "@context": "https://schema.org",
  "@type": "DeliveryService",
  "name": "IVOCOLIS Express",
  "telephone": "+2250767182545",
  "address": {
    "streetAddress": "Nouveau Goudron",
    "addressLocality": "Cocody Faya, Abidjan",
    "addressCountry": "CI"
  },
  "priceRange": "1500 FCFA - 15000 FCFA"
}
                  """.trimIndent(),
                  color = Color(0xFF7DD3FC),
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace,
                  modifier = Modifier.padding(10.dp)
                )
              }
            }
          }

          // Section 4: Contenu & Backlinks
          item {
            SeoSectionCard(
              title = "4. Stratégie de Contenu & Backlinks",
              subtitle = "Faire grandir l'autorité du domaine en Côte d'Ivoire"
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BulletPointItem(
                  title = "Fiche Google My Business",
                  desc = "Créer la fiche vérifiée 'IVOCOLIS - Cocody Faya Nouveau Goudron' pour apparaître sur Google Maps et dans le Pack Local d'Abidjan."
                )
                BulletPointItem(
                  title = "Partenariats e-commerçants d'Abidjan",
                  desc = "Proposer une réduction aux vendeurs Facebook/Instagram en échange d'un lien 'Expédié en Côte d'Ivoire par IVOCOLIS'."
                )
                BulletPointItem(
                  title = "Articles de Blog Utiles",
                  desc = "Publier des guides pratiques : 'Guide d'envoi de colis Abidjan-Bouaké', 'Comment envoyer des produits vivriers ou pagnes sans abîmer'."
                )
              }
            }
          }
        }

        // Bottom Actions
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = {
              clipboardManager.setText(AnnotatedString(fullSeoStrategyReport))
            },
            colors = ButtonDefaults.buttonColors(containerColor = IvoBluePrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Copier le rapport", fontSize = 12.sp)
          }

          Button(
            onClick = {
              WhatsAppHelper.shareGeneralText(
                context,
                "Stratégie SEO IVOCOLIS",
                fullSeoStrategyReport
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = IvoGreenPrimary, contentColor = Color.Black),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Partager", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun SeoSectionCard(
  title: String,
  subtitle: String,
  content: @Composable () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = IvoBlueDark)
      Text(text = subtitle, fontSize = 11.sp, color = Color(0xFF64748B))
      Spacer(modifier = Modifier.height(10.dp))
      content()
    }
  }
}

@Composable
private fun BulletPointItem(title: String, desc: String) {
  Row(verticalAlignment = Alignment.Top) {
    Icon(
      imageVector = Icons.Default.Star,
      contentDescription = null,
      tint = IvoGreenPrimary,
      modifier = Modifier
        .size(16.dp)
        .padding(top = 2.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Column {
      Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
      Text(text = desc, fontSize = 11.sp, color = Color(0xFF475569))
    }
  }
}
