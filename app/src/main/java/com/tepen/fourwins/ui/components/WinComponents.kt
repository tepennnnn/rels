package com.tepen.fourwins.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tepen.fourwins.model.WinPillar
import com.tepen.fourwins.ui.theme.TacticalColors

fun pillarColor(pillar: WinPillar): Color = Color(pillar.colorHex)

fun pillarIcon(pillar: WinPillar): ImageVector = when (pillar) {
    WinPillar.PHYSICAL -> Icons.Rounded.FitnessCenter
    WinPillar.MENTAL -> Icons.Rounded.Psychology
    WinPillar.FINANCIAL -> Icons.AutoMirrored.Rounded.TrendingUp
    WinPillar.SPIRITUAL -> Icons.Rounded.SelfImprovement
}

@Composable
fun TacticalCard(
    modifier: Modifier = Modifier,
    borderColor: Color = TacticalColors.BorderHairline,
    backgroundColor: Color = TacticalColors.CardDark,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = shape,
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp), content = content)
    }
}

@Composable
fun KneeSafetyPill(
    isSafe: Boolean,
    label: String = if (isSafe) "KNEE-SAFE" else "MODIFY FOR KNEE"
) {
    val bg = if (isSafe) TacticalColors.KneeSafeGreen.copy(alpha = 0.14f)
    else TacticalColors.CautionAmber.copy(alpha = 0.16f)
    val fg = if (isSafe) TacticalColors.KneeSafeGreen else TacticalColors.CautionAmber
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(1.dp, fg.copy(alpha = 0.4f), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = if (isSafe) Icons.Rounded.Shield else Icons.Rounded.WarningAmber,
            contentDescription = null,
            tint = fg,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = label,
            color = fg,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

/**
 * Hero 4-Quadrant Arcade Wheel modeled after the "4 DAILY WINS" whiteboard diagram:
 * central athlete core surrounded by Physical, Financial, Spiritual, Mental quadrants.
 */
@Composable
fun FourWinsArcadeWheel(
    pillarStatus: Map<WinPillar, Boolean>,
    pillarsWon: Int,
    habitsDone: Int,
    habitsTotal: Int,
    streakDays: Int,
    totalWinsAllTime: Int,
    rankTitle: String,
    level: Int,
    xpIntoLevel: Int,
    xpPerLevel: Int,
    modifier: Modifier = Modifier
) {
    val ordered = listOf(
        WinPillar.PHYSICAL to 185f,   // Top-left
        WinPillar.FINANCIAL to 275f,  // Top-right
        WinPillar.SPIRITUAL to 5f,    // Bottom-right
        WinPillar.MENTAL to 95f       // Bottom-left
    )

    val sweeps = ordered.map { (pillar, _) ->
        val won = pillarStatus[pillar] == true
        val anim by animateFloatAsState(
            targetValue = if (won) 1f else 0.18f,
            animationSpec = tween(420),
            label = pillar.name
        )
        anim
    }

    val centerGlow by animateColorAsState(
        targetValue = if (pillarsWon == 4) TacticalColors.ArcadeGold else TacticalColors.MentalCyan,
        animationSpec = tween(450),
        label = "centerGlow"
    )

    TacticalCard(
        modifier = modifier,
        borderColor = if (pillarsWon == 4) TacticalColors.ArcadeGold.copy(alpha = 0.7f) else TacticalColors.BorderHairline,
        backgroundColor = TacticalColors.CardDark
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: 4-Quadrant Wheel
            Box(
                modifier = Modifier.size(134.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(126.dp)) {
                    val stroke = 14.dp.toPx()
                    val inset = stroke / 2f
                    val arcSize = Size(size.width - stroke, size.height - stroke)
                    val topLeft = Offset(inset, inset)

                    ordered.forEachIndexed { idx, (pillar, startAngle) ->
                        val baseColor = pillarColor(pillar)
                        val activeFraction = sweeps[idx]
                        // Background track
                        drawArc(
                            color = baseColor.copy(alpha = 0.14f),
                            startAngle = startAngle,
                            sweepAngle = 80f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                        // Active arc
                        drawArc(
                            color = if (pillarStatus[pillar] == true) baseColor else baseColor.copy(alpha = 0.35f),
                            startAngle = startAngle,
                            sweepAngle = 80f * activeFraction,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                    }
                }

                // Central Avatar / Score Core
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(centerGlow.copy(alpha = 0.25f), TacticalColors.AmoledBlack)
                                )
                            )
                            .border(1.5.dp, centerGlow.copy(alpha = 0.65f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = "Athlete Core",
                            tint = centerGlow,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "$pillarsWon/4",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (pillarsWon == 4) TacticalColors.ArcadeGold else TacticalColors.TextPrimary
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            // Right: Arcade Stats & 4-Pillar Mini Status
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LVL $level • $rankTitle",
                        color = TacticalColors.ArcadeGold,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "$habitsDone/$habitsTotal TASKS",
                        color = TacticalColors.TextSecondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(Modifier.height(6.dp))
                // XP Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(TacticalColors.AmoledBlack)
                ) {
                    val frac = (xpIntoLevel.toFloat() / xpPerLevel.coerceAtLeast(1)).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(frac)
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(TacticalColors.PhysicalCrimson, TacticalColors.ArcadeGold)
                                )
                            )
                    )
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricChip(
                        label = "ALL-TIME WINS",
                        value = "$totalWinsAllTime",
                        accent = TacticalColors.MentalCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        label = "WIN STREAK",
                        value = "${streakDays}d",
                        accent = TacticalColors.PhysicalCrimson,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(8.dp))

                // 4-Pillar tags row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    WinPillar.entries.forEach { pillar ->
                        val won = pillarStatus[pillar] == true
                        val c = pillarColor(pillar)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (won) c.copy(alpha = 0.22f) else TacticalColors.AmoledBlack)
                                .border(
                                    1.dp,
                                    if (won) c else TacticalColors.BorderHairline,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = pillar.tag,
                                color = if (won) c else TacticalColors.TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricChip(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(TacticalColors.AmoledBlack)
            .border(1.dp, TacticalColors.BorderHairline, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            color = TacticalColors.TextMuted
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace,
            color = accent
        )
    }
}
