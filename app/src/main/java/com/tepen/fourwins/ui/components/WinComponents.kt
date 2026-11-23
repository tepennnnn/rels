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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Notifications
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tepen.fourwins.model.WinPillar
import com.tepen.fourwins.ui.theme.TacticalColors

fun pillarColor(pillar: WinPillar): Color = when (pillar) {
    WinPillar.PHYSICAL -> TacticalColors.PhysicalCrimson
    WinPillar.MENTAL -> TacticalColors.MentalCyan
    WinPillar.FINANCIAL -> TacticalColors.FinancialEmerald
    WinPillar.SPIRITUAL -> TacticalColors.SpiritualAmber
}

fun pillarIcon(pillar: WinPillar): ImageVector = when (pillar) {
    WinPillar.PHYSICAL -> Icons.Rounded.FitnessCenter
    WinPillar.MENTAL -> Icons.Rounded.Psychology
    WinPillar.FINANCIAL -> Icons.AutoMirrored.Rounded.TrendingUp
    WinPillar.SPIRITUAL -> Icons.Rounded.SelfImprovement
}

/**
 * Frosted Glass Card with luminous cyan-sapphire gradient hairline border.
 */
@Composable
fun TacticalCard(
    modifier: Modifier = Modifier,
    borderColor: Color? = null,
    backgroundColor: Color = TacticalColors.GlassFill,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    val borderMod = if (borderColor != null) {
        Modifier.border(1.dp, borderColor.copy(alpha = 0.7f), shape)
    } else {
        Modifier.border(1.dp, TacticalColors.GlassBorderGradient, shape)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .then(borderMod)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(16.dp),
        content = content
    )
}

/**
 * Top Greeting Header mirroring the template's "Hello, Smith Roe | Bell | SR" row.
 */
@Composable
fun TemplateGreetingHeader(
    userName: String,
    subtitle: String,
    streakDays: Int,
    onAvatarClick: () -> Unit
) {
    val initials = userName.trim()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "4W" }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hello, $userName",
                style = MaterialTheme.typography.headlineMedium,
                color = TacticalColors.TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = TacticalColors.TextSecondary
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(TacticalColors.GlassFillStrong)
                    .border(1.dp, TacticalColors.GlassBorderGradient, RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.LocalFireDepartment,
                    contentDescription = "Streak",
                    tint = TacticalColors.ArcadeGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${streakDays}d",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TacticalColors.TextPrimary
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(TacticalColors.HeroCardGradient)
                    .clickable(onClick = onAvatarClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TacticalColors.HeroOnDark
                )
            }
        }
    }
}

/**
 * Signature Luminous Cyan Gradient Hero Card inspired by the template's Balance + Top Up card.
 */
@Composable
fun HeroWinsGradientCard(
    pillarsWon: Int,
    habitsDone: Int,
    habitsTotal: Int,
    totalWinsAllTime: Int,
    level: Int,
    rankTitle: String,
    xpIntoLevel: Int,
    xpPerLevel: Int,
    onActionClick: () -> Unit,
    actionLabel: String = "Train"
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(TacticalColors.HeroCardGradient)
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Today's 4 Daily Wins",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TacticalColors.HeroOnDark.copy(alpha = 0.8f)
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$pillarsWon / 4",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TacticalColors.HeroOnDark
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "$habitsDone/$habitsTotal tasks",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TacticalColors.HeroOnDark.copy(alpha = 0.78f),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
                Spacer(Modifier.height(6.dp))
                // XP progress strip
                val frac = (xpIntoLevel.toFloat() / xpPerLevel.coerceAtLeast(1)).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(TacticalColors.HeroOnDark.copy(alpha = 0.18f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(frac)
                            .height(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(TacticalColors.HeroOnDark)
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Lvl $level $rankTitle • $totalWinsAllTime lifetime wins",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TacticalColors.HeroOnDark.copy(alpha = 0.85f)
                )
            }

            // Vertical subtle hairline divider (matching template)
            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .width(1.dp)
                    .height(78.dp)
                    .background(TacticalColors.HeroOnDark.copy(alpha = 0.22f))
            )

            // Right Action Tile (like "+ Top up" in template)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onActionClick)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(TacticalColors.HeroOnDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FitnessCenter,
                        contentDescription = actionLabel,
                        tint = TacticalColors.HeroCyanLight,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = actionLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TacticalColors.HeroOnDark
                )
            }
        }
    }
}

/**
 * Outlined Glass Quick-Action Tile mirroring "Pay | Cards | Bills" in template.
 */
@Composable
fun QuickActionGlassTile(
    icon: ImageVector,
    label: String,
    accent: Color = TacticalColors.MentalCyan,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(TacticalColors.GlassFill)
            .border(1.dp, TacticalColors.GlassBorderGradient, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = accent,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TacticalColors.TextPrimary,
            maxLines = 1
        )
    }
}

/**
 * Clean Underline Tab Switcher mirroring "Credit Card | Debit Card" in template.
 */
@Composable
fun TemplateUnderlineTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        tabs.forEachIndexed { idx, label ->
            val active = idx == selectedIndex
            Column(
                modifier = Modifier
                    .clickable { onSelect(idx) }
                    .padding(vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    fontSize = 15.sp,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                    color = if (active) TacticalColors.TextPrimary else TacticalColors.TextMuted
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(if (active) 48.dp else 0.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (active) TacticalColors.MentalCyan else Color.Transparent)
                )
            }
        }
    }
}

@Composable
fun KneeSafetyPill(
    isSafe: Boolean,
    label: String = if (isSafe) "Knee-Safe" else "Modify for Knee"
) {
    val bg = if (isSafe) TacticalColors.KneeSafeGreen.copy(alpha = 0.16f)
    else TacticalColors.CautionAmber.copy(alpha = 0.18f)
    val fg = if (isSafe) TacticalColors.KneeSafeGreen else TacticalColors.CautionAmber
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(1.dp, fg.copy(alpha = 0.45f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = if (isSafe) Icons.Rounded.Shield else Icons.Rounded.WarningAmber,
            contentDescription = null,
            tint = fg,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = label,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

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
        WinPillar.PHYSICAL to 185f,
        WinPillar.FINANCIAL to 275f,
        WinPillar.SPIRITUAL to 5f,
        WinPillar.MENTAL to 95f
    )
    val sweeps = ordered.map { (pillar, _) ->
        val won = pillarStatus[pillar] == true
        val anim by animateFloatAsState(
            targetValue = if (won) 1f else 0.22f,
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

    TacticalCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(112.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(104.dp)) {
                    val stroke = 11.dp.toPx()
                    val inset = stroke / 2f
                    val arcSize = Size(size.width - stroke, size.height - stroke)
                    val topLeft = Offset(inset, inset)
                    ordered.forEachIndexed { idx, (pillar, startAngle) ->
                        val baseColor = pillarColor(pillar)
                        drawArc(
                            color = baseColor.copy(alpha = 0.16f),
                            startAngle = startAngle,
                            sweepAngle = 80f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = if (pillarStatus[pillar] == true) baseColor else baseColor.copy(alpha = 0.4f),
                            startAngle = startAngle,
                            sweepAngle = 80f * sweeps[idx],
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.Person, null, tint = centerGlow, modifier = Modifier.size(22.dp))
                    Text(
                        text = "$pillarsWon/4",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TacticalColors.TextPrimary
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Level $level • $rankTitle",
                    color = TacticalColors.ArcadeGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "$habitsDone of $habitsTotal tasks • $totalWinsAllTime total wins",
                    color = TacticalColors.TextSecondary,
                    fontSize = 13.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    WinPillar.entries.forEach { pillar ->
                        val won = pillarStatus[pillar] == true
                        val c = pillarColor(pillar)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (won) c.copy(alpha = 0.25f) else TacticalColors.GlassFill)
                                .border(1.dp, if (won) c else TacticalColors.BorderHairline.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = pillar.tag,
                                color = if (won) c else TacticalColors.TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
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
            .clip(RoundedCornerShape(14.dp))
            .background(TacticalColors.GlassFill)
            .border(1.dp, TacticalColors.GlassBorderGradient, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TacticalColors.TextSecondary
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = accent
        )
    }
}
