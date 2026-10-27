package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.History
import com.example.ui.navigation.AppDestination
import com.example.ui.navigation.AppNavigationState
import com.example.ui.navigation.rememberAppNavigationState
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PaymentScreen
import com.example.ui.screens.PickupRequestScreen
import com.example.ui.screens.TrackingScreen
import com.example.ui.screens.UserHistoryScreen
import com.example.ui.theme.IvoBlueDark
import com.example.ui.theme.IvoGreenLight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.util.NotificationHelper
import com.example.ui.util.WhatsAppHelper

class MainActivity : ComponentActivity() {
  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize notification channel for parcel tracking alerts
    NotificationHelper.initChannel(this)

    // Check if opened from notification with tracking reference
    val trackingRef = intent?.getStringExtra("EXTRA_TRACKING_REF")
    if (!trackingRef.isNullOrBlank()) {
      viewModel.searchParcelByRef(trackingRef)
    }

    setContent {
      MyApplicationTheme {
        val navState = rememberAppNavigationState(initialDestination = AppDestination.HOME)
        MainAppContent(viewModel = viewModel, navState = navState)
      }
    }
  }
}

@Composable
fun MainAppContent(
  viewModel: MainViewModel,
  navState: AppNavigationState = rememberAppNavigationState()
) {
  val context = LocalContext.current
  val vmScreen by viewModel.currentScreen.collectAsState()
  val uiMessage by viewModel.uiMessage.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }

  // Sync ViewModel requested navigation with the top-level navState
  LaunchedEffect(vmScreen) {
    if (navState.currentDestination != vmScreen) {
      navState.navigateTo(vmScreen)
    }
  }

  LaunchedEffect(uiMessage) {
    uiMessage?.let {
      snackbarHostState.showSnackbar(it.message)
      viewModel.clearUiMessage()
    }
  }

  // Top-level back handler using the state holder
  BackHandler(enabled = navState.canNavigateBack) {
    navState.navigateBack()
    viewModel.navigateTo(navState.currentDestination)
  }

  val navItems = listOf(
    NavigationItemData(
      destination = AppDestination.HOME,
      label = "Accueil",
      selectedIcon = Icons.Filled.Home,
      unselectedIcon = Icons.Outlined.Home,
      testTag = "nav_home"
    ),
    NavigationItemData(
      destination = AppDestination.BOOKING,
      label = "Expédier",
      selectedIcon = Icons.Filled.LocalShipping,
      unselectedIcon = Icons.Outlined.LocalShipping,
      testTag = "nav_booking"
    ),
    NavigationItemData(
      destination = AppDestination.TRACKING,
      label = "Suivi",
      selectedIcon = Icons.Filled.LocationOn,
      unselectedIcon = Icons.Outlined.LocationOn,
      testTag = "nav_tracking"
    ),
    NavigationItemData(
      destination = AppDestination.HISTORY,
      label = "Mes Colis",
      selectedIcon = Icons.Filled.History,
      unselectedIcon = Icons.Outlined.History,
      testTag = "nav_history"
    ),
    NavigationItemData(
      destination = AppDestination.ADMIN,
      label = "Admin",
      selectedIcon = Icons.Filled.AdminPanelSettings,
      unselectedIcon = Icons.Outlined.AdminPanelSettings,
      testTag = "nav_admin"
    )
  )

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    floatingActionButton = {
      // Floating WhatsApp button for instant assistance
      FloatingActionButton(
        onClick = {
          WhatsAppHelper.openWhatsApp(
            context,
            "Bonjour IVOCOLIS, je vous contacte depuis l'application pour une expédition de colis."
          )
        },
        containerColor = Color(0xFF25D366),
        contentColor = Color.White,
        modifier = Modifier.testTag("floating_whatsapp_fab")
      ) {
        Icon(
          imageVector = Icons.Default.Call,
          contentDescription = "WhatsApp 07 67 18 25 45",
          modifier = Modifier.size(24.dp)
        )
      }
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
      ) {
        navItems.forEach { item ->
          val isSelected = navState.currentDestination == item.destination
          NavigationBarItem(
            selected = isSelected,
            onClick = {
              navState.navigateTo(item.destination)
              viewModel.navigateTo(item.destination)
            },
            icon = {
              Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.label,
                modifier = Modifier.size(22.dp)
              )
            },
            label = {
              Text(
                text = item.label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = IvoBlueDark,
              selectedTextColor = IvoBlueDark,
              indicatorColor = IvoGreenLight.copy(alpha = 0.35f),
              unselectedIconColor = Color(0xFF64748B),
              unselectedTextColor = Color(0xFF64748B)
            ),
            modifier = Modifier.testTag(item.testTag)
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      AnimatedContent(
        targetState = navState.currentDestination,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_transition"
      ) { target ->
        when (target) {
          AppDestination.HOME -> HomeScreen(viewModel = viewModel)
          AppDestination.BOOKING -> PickupRequestScreen(viewModel = viewModel)
          AppDestination.TRACKING -> TrackingScreen(viewModel = viewModel)
          AppDestination.HISTORY -> UserHistoryScreen(viewModel = viewModel)
          AppDestination.PAYMENT -> PaymentScreen(viewModel = viewModel)
          AppDestination.ADMIN -> AdminDashboardScreen(viewModel = viewModel)
        }
      }
    }
  }
}

private data class NavigationItemData(
  val destination: AppDestination,
  val label: String,
  val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
  val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
  val testTag: String
)
