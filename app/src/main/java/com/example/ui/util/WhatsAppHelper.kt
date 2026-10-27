package com.example.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder

object WhatsAppHelper {
  const val PHONE_DISPLAY = "07 67 18 25 45"
  const val PHONE_INTERNATIONAL = "+2250767182545"
  const val PHONE_CALL_URI = "tel:+2250767182545"
  const val EMAIL_ADDRESS = "ivocolisexpress@gmail.com"
  const val ADDRESS_OFFICE = "Abidjan - Cocody Faya Nouveau Goudron, Côte d'Ivoire"

  fun openWhatsApp(context: Context, message: String) {
    try {
      val encodedMessage = URLEncoder.encode(message, "UTF-8")
      val url = "https://wa.me/2250767182545?text=$encodedMessage"
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Impossible d'ouvrir WhatsApp : ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
  }

  fun shareTrackingViaWhatsApp(context: Context, recipientPhone: String?, trackingText: String) {
    try {
      val encoded = URLEncoder.encode(trackingText, "UTF-8")
      val target = if (!recipientPhone.isNullOrBlank()) {
        val cleanPhone = recipientPhone.replace(" ", "").replace("-", "")
        val formatted = if (cleanPhone.startsWith("+225")) cleanPhone.removePrefix("+") else if (cleanPhone.startsWith("0")) "225$cleanPhone" else cleanPhone
        "https://wa.me/$formatted?text=$encoded"
      } else {
        "https://api.whatsapp.com/send?text=$encoded"
      }
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(target))
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Erreur de partage WhatsApp", Toast.LENGTH_SHORT).show()
    }
  }

  fun openDialer(context: Context) {
    try {
      val intent = Intent(Intent.ACTION_DIAL, Uri.parse(PHONE_CALL_URI))
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Erreur appel téléphonique", Toast.LENGTH_SHORT).show()
    }
  }

  fun sendEmail(context: Context, subject: String, body: String) {
    try {
      val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$EMAIL_ADDRESS")
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Aucune application de messagerie trouvée", Toast.LENGTH_SHORT).show()
    }
  }

  fun shareGeneralText(context: Context, title: String, text: String) {
    try {
      val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
      }
      val shareIntent = Intent.createChooser(sendIntent, title)
      context.startActivity(shareIntent)
    } catch (e: Exception) {
      Toast.makeText(context, "Impossible de partager", Toast.LENGTH_SHORT).show()
    }
  }
}
