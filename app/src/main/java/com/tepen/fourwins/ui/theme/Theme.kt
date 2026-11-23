package com.tepen.fourwins.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tepen.fourwins.R

object TacticalColors {
    // Deep Midnight-Ocean Gradient Palette (from Gradient Dark Mode Template)
    val OceanTopGlow = Color(0xFF0064A8)
    val OceanMid = Color(0xFF033B6B)
    val OceanDeep = Color(0xFF02192E)
    val AmoledBlack = Color(0xFF02192E)
    val SurfaceDark = Color(0xFF042644)
    val CardDark = Color(0xFF062E52)
    val CardElevated = Color(0xFF083862)
    val BorderHairline = Color(0xFF1F6396)

    // Luminous Hero Cyan Gradient (Balance Card style)
    val HeroCyanLight = Color(0xFF6EEBFF)
    val HeroCyanMid = Color(0xFF23C5F2)
    val HeroCyanDeep = Color(0xFF0C8CCF)
    val HeroOnDark = Color(0xFF03203A)

    // Frosted Glass Surfaces & Borders
    val GlassFill = Color(0x12FFFFFF)
    val GlassFillStrong = Color(0x1CFFFFFF)
    val GlassBorderTop = Color(0x8862E2FF)
    val GlassBorderBottom = Color(0x440B5285)

    // Typography
    val TextPrimary = Color(0xFFF3F8FF)
    val TextSecondary = Color(0xFFA7CBE4)
    val TextMuted = Color(0xFF6C97B8)

    // 4-Pillar Luminous Accents
    val PhysicalCrimson = Color(0xFFFF5A6E)
    val MentalCyan = Color(0xFF4BE4FF)
    val FinancialEmerald = Color(0xFF2EE69C)
    val SpiritualAmber = Color(0xFFFFC857)

    val ArcadeGold = Color(0xFFFFD66B)
    val KneeSafeGreen = Color(0xFF2EE69C)
    val CautionAmber = Color(0xFFFFC857)
    val AlertRed = Color(0xFFFF5A6E)

    val BackgroundGradient = Brush.verticalGradient(
        colors = listOf(OceanTopGlow, OceanMid, OceanDeep)
    )

    val HeroCardGradient = Brush.linearGradient(
        colors = listOf(HeroCyanLight, HeroCyanMid, HeroCyanDeep),
        start = Offset(0f, 0f),
        end = Offset(900f, 500f)
    )

    val GlassBorderGradient = Brush.linearGradient(
        colors = listOf(GlassBorderTop, GlassBorderBottom),
        start = Offset(0f, 0f),
        end = Offset(600f, 600f)
    )
}

@OptIn(ExperimentalTextApi::class)
val NunitoSansFamily = FontFamily(
    Font(
        resId = R.font.nunito_sans,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))
    ),
    Font(
        resId = R.font.nunito_sans,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500))
    ),
    Font(
        resId = R.font.nunito_sans,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600))
    ),
    Font(
        resId = R.font.nunito_sans,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700))
    ),
    Font(
        resId = R.font.nunito_sans,
        weight = FontWeight.ExtraBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(800))
    )
)

private val TacticalDarkScheme = darkColorScheme(
    primary = TacticalColors.MentalCyan,
    onPrimary = TacticalColors.HeroOnDark,
    secondary = TacticalColors.PhysicalCrimson,
    onSecondary = Color.White,
    tertiary = TacticalColors.FinancialEmerald,
    onTertiary = TacticalColors.HeroOnDark,
    background = TacticalColors.OceanDeep,
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
        fontFamily = NunitoSansFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        letterSpacing = (-0.4).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = NunitoSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 23.sp,
        letterSpacing = (-0.2).sp
    ),
    titleLarge = TextStyle(
        fontFamily = NunitoSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = NunitoSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = NunitoSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = NunitoSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp
    )
)

@Composable
fun OceanGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF0076BA), TacticalColors.OceanMid, TacticalColors.OceanDeep),
                    center = Offset(300f, 120f),
                    radius = 1500f
                )
            ),
        content = content
    )
}

@Composable
fun FourWinsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TacticalDarkScheme,
        typography = TacticalTypography,
        content = content
    )
}
