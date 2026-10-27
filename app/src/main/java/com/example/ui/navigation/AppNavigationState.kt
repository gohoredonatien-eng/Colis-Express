package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Top-level navigation destinations in IVOCOLIS.
 * Specifically handles the three core user views:
 * - HOME: Accueil & présentation des tarifs
 * - BOOKING: Commande d'enlèvement / Réservation de colis (Parcel Booking)
 * - TRACKING: Suivi des colis en direct (Parcel Tracking)
 * plus les flux de paiement et d'administration.
 */
enum class AppDestination(val label: String, val testTag: String) {
  HOME("Accueil", "nav_home"),
  BOOKING("Expédier", "nav_booking"),
  TRACKING("Suivi", "nav_tracking"),
  HISTORY("Mes Colis", "nav_history"),
  PAYMENT("Paiement", "nav_payment"),
  ADMIN("Admin", "nav_admin");

  companion object {
    val PICKUP: AppDestination = BOOKING
    val coreDestinations: List<AppDestination> = listOf(HOME, BOOKING, TRACKING, HISTORY)
    val allDestinations: List<AppDestination> = values().toList()
  }
}

typealias AppScreen = AppDestination

/**
 * Top-level state holder that coordinates navigation state and transitions
 * across the Home, Parcel Booking, Tracking, and User History views.
 *
 * Encapsulates the current destination, back stack, and navigation methods.
 */
@Stable
class AppNavigationState(
  initialDestination: AppDestination = AppDestination.HOME
) {
  var currentDestination by mutableStateOf(initialDestination)
    private set

  private val backStack = mutableStateListOf<AppDestination>()

  val canNavigateBack: Boolean
    get() = backStack.isNotEmpty() || currentDestination != AppDestination.HOME

  fun navigateTo(destination: AppDestination) {
    if (destination != currentDestination) {
      backStack.add(currentDestination)
      currentDestination = destination
    }
  }

  fun navigateToHome() {
    backStack.clear()
    currentDestination = AppDestination.HOME
  }

  fun navigateToBooking() {
    navigateTo(AppDestination.BOOKING)
  }

  fun navigateToTracking() {
    navigateTo(AppDestination.TRACKING)
  }

  fun navigateToHistory() {
    navigateTo(AppDestination.HISTORY)
  }

  fun navigateToPayment() {
    navigateTo(AppDestination.PAYMENT)
  }

  fun navigateToAdmin() {
    navigateTo(AppDestination.ADMIN)
  }

  fun navigateBack(): Boolean {
    return if (backStack.isNotEmpty()) {
      currentDestination = backStack.removeAt(backStack.lastIndex)
      true
    } else if (currentDestination != AppDestination.HOME) {
      currentDestination = AppDestination.HOME
      true
    } else {
      false
    }
  }
}

/**
 * Creates and remembers an [AppNavigationState] instance across recompositions.
 */
@Composable
fun rememberAppNavigationState(
  initialDestination: AppDestination = AppDestination.HOME
): AppNavigationState {
  return remember { AppNavigationState(initialDestination) }
}
