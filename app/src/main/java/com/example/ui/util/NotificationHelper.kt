package com.example.ui.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.ParcelStatus

/**
 * Gestionnaire des notifications locales pour IVOCOLIS.
 * Alerte l'utilisateur dès que son colis change de statut (ex: 'Votre colis est arrivé à Bouaké').
 */
object NotificationHelper {

  const val CHANNEL_ID = "ivocolis_tracking_notifications"
  private const val CHANNEL_NAME = "Suivi des Colis IVOCOLIS"
  private const val CHANNEL_DESCRIPTION = "Alertes en temps réel sur l'acheminement et la livraison de vos colis en Côte d'Ivoire."

  /**
   * Crée le canal de notification pour Android 8.0+ (API 26+)
   */
  fun initChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
        description = CHANNEL_DESCRIPTION
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 250, 150, 250)
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
      notificationManager?.createNotificationChannel(channel)
    }
  }

  /**
   * Vérifie si la permission de notification est accordée (Android 13+ / API 33+)
   */
  fun hasNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      true
    }
  }

  /**
   * Affiche une notification locale lors d'un changement de statut de colis.
   */
  fun notifyParcelStatusChanged(
    context: Context,
    parcelReference: String,
    destinationCity: String,
    newStatus: ParcelStatus,
    location: String = "",
    comment: String = ""
  ) {
    initChannel(context)

    if (!hasNotificationPermission(context)) {
      return
    }

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra("EXTRA_TRACKING_REF", parcelReference)
    }

    val pendingIntent = PendingIntent.getActivity(
      context,
      parcelReference.hashCode(),
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
    )

    val title = "IVOCOLIS • Colis $parcelReference"
    val statusDescription = when (newStatus) {
      ParcelStatus.DEMANDE_RECU -> "Votre demande de collecte a été prise en compte à Abidjan."
      ParcelStatus.RAMASSE_ABIDJAN -> "Votre colis a été ramassé par notre coursier à Abidjan."
      ParcelStatus.RECU_AGENCE -> "Votre colis est réceptionné au Hub Cocody Faya."
      ParcelStatus.EXPEDIE -> "Votre colis est expédié en direction de $destinationCity."
      ParcelStatus.EN_TRANSIT -> "Votre colis pour $destinationCity est actuellement en cours d'acheminement."
      ParcelStatus.ARRIVE_DESTINATION -> "Votre colis est bien arrivé à la gare de $destinationCity !"
      ParcelStatus.LIVRE -> "Félicitations ! Votre colis a été livré avec succès à $destinationCity."
      ParcelStatus.ANNULE -> "L'expédition de votre colis a été annulée."
    }

    val locationNote = if (location.isNotBlank()) "\n📍 $location" else ""
    val commentNote = if (comment.isNotBlank()) "\n📝 $comment" else ""
    val bigText = "$statusDescription$locationNote$commentNote\nMerci de faire confiance à IVOCOLIS Côte d'Ivoire !"

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_dialog_info)
      .setContentTitle(title)
      .setContentText(statusDescription)
      .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)
      .setVibrate(longArrayOf(0, 250, 150, 250))
      .setColor(0xFF0F172A.toInt())
      .build()

    try {
      NotificationManagerCompat.from(context).notify(
        parcelReference.hashCode(),
        notification
      )
    } catch (e: SecurityException) {
      // Permission not granted at runtime
    }
  }
}
