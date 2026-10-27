package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ui.MainViewModel

/**
 * AdminScreen delegating to the comprehensive AdminDashboardScreen.
 * Provides full parcel listing from Firestore, quick status updates (Reçu, En transit, Livré),
 * and the daily receipts summary ("recettes du jour").
 */
@Composable
fun AdminScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  AdminDashboardScreen(
    viewModel = viewModel,
    modifier = modifier
  )
}
