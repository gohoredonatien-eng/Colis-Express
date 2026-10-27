package com.example

import com.example.data.model.DefaultDestinations
import com.example.data.model.PaymentGatewayType
import com.example.data.repository.ParcelRepository
import com.example.ui.navigation.AppDestination
import com.example.ui.navigation.AppNavigationState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testReferenceFormat() {
    val ref = ParcelRepository.generateReference()
    assertTrue(ref.startsWith("IVO-CI-"))
    assertEquals(11, ref.length)
  }

  @Test
  fun testDestinationsBasePrice() {
    val destinations = DefaultDestinations.initialList
    assertTrue(destinations.isNotEmpty())
    val bouake = destinations.find { it.cityName == "Bouaké" }
    assertNotNull(bouake)
    assertEquals(1500, bouake?.basePriceFcfa)

    val yamoussoukro = destinations.find { it.cityName == "Yamoussoukro" }
    assertNotNull(yamoussoukro)
    assertEquals(1500, yamoussoukro?.basePriceFcfa)

    // All destinations must have base price >= 1500 FCFA
    destinations.forEach { dest ->
      assertTrue(dest.basePriceFcfa >= 1500)
    }
  }

  @Test
  fun testPaymentGateways() {
    val om = PaymentGatewayType.ORANGE_MONEY
    assertEquals("#144#", om.ussdCode)

    val wave = PaymentGatewayType.WAVE
    assertNotNull(wave)

    val mtn = PaymentGatewayType.MTN_MOMO
    assertEquals("*133#", mtn.ussdCode)

    val moov = PaymentGatewayType.MOOV_MONEY
    assertEquals("*155#", moov.ussdCode)
  }

  @Test
  fun testNavigationStateSwitching() {
    val navState = AppNavigationState(initialDestination = AppDestination.HOME)
    assertEquals(AppDestination.HOME, navState.currentDestination)
    assertFalse(navState.canNavigateBack)

    // Switch to parcel booking view
    navState.navigateToBooking()
    assertEquals(AppDestination.BOOKING, navState.currentDestination)
    assertTrue(navState.canNavigateBack)

    // Switch to tracking view
    navState.navigateToTracking()
    assertEquals(AppDestination.TRACKING, navState.currentDestination)

    // Navigate back to booking view
    val popped1 = navState.navigateBack()
    assertTrue(popped1)
    assertEquals(AppDestination.BOOKING, navState.currentDestination)

    // Navigate back to home view
    val popped2 = navState.navigateBack()
    assertTrue(popped2)
    assertEquals(AppDestination.HOME, navState.currentDestination)
    assertFalse(navState.canNavigateBack)
  }
}
