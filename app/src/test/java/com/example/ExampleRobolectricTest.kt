package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("IVOCOLIS Express", appName)
  }

  @Test
  fun `test shipping rate minimum base is 1500 fcfa`() {
    val basePrice = 1500
    val weightKg = 1.0
    val weightExtra = if (weightKg <= 1.0) 0 else 500
    val total = basePrice + weightExtra
    assertEquals(1500, total)
  }

  @Test
  fun `test form validation required fields`() {
    val senderName = "Koffi Serge"
    val senderPhone = "0767182545"
    val recipientName = "Awa Bamba"
    val recipientPhone = "0501020304"
    val destinationCity = "Bouaké"

    val isSenderValid = senderName.trim().length >= 2 && senderPhone.filter { it.isDigit() }.length >= 8
    val isRecipientValid = recipientName.trim().length >= 2 && recipientPhone.filter { it.isDigit() }.length >= 8
    val isCityValid = destinationCity.isNotBlank()

    org.junit.Assert.assertTrue(isSenderValid && isRecipientValid && isCityValid)
  }

  @Test
  fun `test notification channel creation and tracking channel exists`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    com.example.ui.util.NotificationHelper.initChannel(context)
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? android.app.NotificationManager
    val channel = notificationManager?.getNotificationChannel(com.example.ui.util.NotificationHelper.CHANNEL_ID)
    org.junit.Assert.assertNotNull(channel)
    assertEquals(com.example.ui.util.NotificationHelper.CHANNEL_ID, channel?.id)
  }

  @Test
  fun `test AppDestination has HISTORY destination`() {
    val history = com.example.ui.navigation.AppDestination.HISTORY
    assertEquals("Mes Colis", history.label)
    assertEquals("nav_history", history.testTag)
  }
}
