package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
  primary = IvoBluePrimary,
  onPrimary = Color.White,
  primaryContainer = IvoBlueContainer,
  onPrimaryContainer = IvoBlueDark,
  secondary = IvoGreenPrimary,
  onSecondary = Color.White,
  secondaryContainer = IvoGreenContainer,
  onSecondaryContainer = IvoGreenDark,
  tertiary = IvoBlueLight,
  onTertiary = Color.White,
  background = SurfaceBackground,
  onBackground = TextPrimary,
  surface = CardSurface,
  onSurface = TextPrimary,
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = TextSecondary,
  outline = BorderLight
)

private val DarkColorScheme = darkColorScheme(
  primary = IvoBlueLight,
  onPrimary = Color.White,
  primaryContainer = IvoBlueDark,
  onPrimaryContainer = Color.White,
  secondary = IvoGreenLight,
  onSecondary = Color.Black,
  secondaryContainer = IvoGreenDark,
  onSecondaryContainer = Color.White,
  tertiary = WaveBlueColor,
  onTertiary = Color.White,
  background = Color(0xFF0F172A),
  onBackground = Color(0xFFF8FAFC),
  surface = Color(0xFF1E293B),
  onSurface = Color(0xFFF8FAFC),
  surfaceVariant = Color(0xFF334155),
  onSurfaceVariant = Color(0xFFCBD5E1),
  outline = Color(0xFF475569)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // For brand consistency, we default to the custom IVOCOLIS blue/green scheme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
