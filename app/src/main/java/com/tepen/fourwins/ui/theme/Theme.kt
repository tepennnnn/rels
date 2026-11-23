package com.tepen.fourwins.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object TacticalColors {
    val AmoledBlack = Color(0xFF050608)
    val SurfaceDark = Color(0xFF0D1016)
    val CardDark = Color(0xFF131720)
    val CardElevated = Color(0xFF191F2C)
    val BorderHairline = Color(0xFF252D3D)

    val TextPrimary = Color(0xFFF3F6FC)
    val TextSecondary = Color(0xFF98A3B8)
    val TextMuted = Color(0xFF626D82)

    val PhysicalCrimson = Color(0xFFFF3B4E)
    val MentalCyan = Color(0xFF00E5FF)
    val FinancialEmerald = Color(0xFF00E676)
    val SpiritualAmber = Color(0xFFFFB300)

    val ArcadeGold = Color(0xFFFFD54F)
    val KneeSafeGreen = Color(0xFF22C55E)
    val CautionAmber = Color(0xFFF59E0B)
    val AlertRed = Color(0xFFEF4444)
}

private val TacticalDarkScheme = darkColorScheme(
    primary = TacticalColors.PhysicalCrimson,
    onPrimary = Color.White,
    secondary = TacticalColors.MentalCyan,
    onSecondary = TacticalColors.AmoledBlack,
    tertiary = TacticalColors.FinancialEmerald,
    onTertiary = TacticalColors.AmoledBlack,
    background = TacticalColors.AmoledBlack,
    onBackground = TacticalColors.TextPrimary,
    surface = TacticalColors.SurfaceDark,
    onSurface = TacticalColors.TextPrimary,
    surfaceVariant = TacticalColors.CardDark,
    onSurfaceVariant = TacticalColors.TextSecondary,
    outline = TacticalColors.BorderHairline,
    error = TacticalColors.AlertRed
)

private val TacticalTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = (-0.3).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 0.8.sp
    )
)

@Composable
fun FourWinsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TacticalDarkScheme,
        typography = TacticalTypography,
        content = content
    )
}
