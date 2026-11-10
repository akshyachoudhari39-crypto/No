package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = FireOrange,
    onPrimary = Color.White,
    primaryContainer = FireOrangeDark,
    onPrimaryContainer = Color.White,
    secondary = GlooCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF004D5C),
    onSecondaryContainer = GlooCyan,
    tertiary = BooyahGold,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF5A4400),
    onTertiaryContainer = BooyahGold,
    background = BattleDark,
    onBackground = Color(0xFFF0F2F8),
    surface = BattleCard,
    onSurface = Color(0xFFF0F2F8),
    surfaceVariant = BattleCardBorder,
    onSurfaceVariant = Color(0xFFB0B7C8),
    error = DangerRed,
    onError = Color.White
  )

private val LightColorScheme = DarkColorScheme // Gaming apps maintain the immersive high-contrast dark theme

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false, // Keep consistent esports aesthetic
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}
