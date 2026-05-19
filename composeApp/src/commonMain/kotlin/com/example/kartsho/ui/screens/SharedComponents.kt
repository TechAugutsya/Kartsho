package com.example.kartsho.ui.screens

import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import coil3.compose.SubcomposeAsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle

@Composable
fun OverviewStrip(
    title: String,
    subtitle: String,
    stats: List<Pair<String, String>>
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            .padding(18.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            stats.forEach { (label, value) ->
                StatPill(label = label, value = value)
            }
        }
    }
}

@Composable
fun StatPill(label: String, value: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ApprovalChip(approved: Boolean) {
    AssistChip(
        onClick = {},
        label = { Text(if (approved) "Live" else "Pending") },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (approved) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            }
        )
    )
}

@Composable
fun remainingText(endAtMillis: Long, currentTimeMillis: Long): String {
    val millisLeft = (endAtMillis - currentTimeMillis).coerceAtLeast(0L)
    val totalSeconds = millisLeft / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    
    val hStr = hours.toString().padStart(2, '0')
    val mStr = minutes.toString().padStart(2, '0')
    val sStr = seconds.toString().padStart(2, '0')
    
    return "$hStr:$mStr:$sStr"
}

@Composable
fun DemoIllustrationBanner(
    seed: Int,
    height: Dp = 132.dp,
    badgeText: String = "✨ Premium",
    title: String = "",
    imageUrl: String? = null
) {
    val colors = when (seed % 4) {
        0 -> listOf(Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFFD946EF)) // Indigo-Violet-Pink
        1 -> listOf(Color(0xFF3B82F6), Color(0xFF06B6D4), Color(0xFF10B981)) // Blue-Cyan-Emerald
        2 -> listOf(Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFFEC4899)) // Amber-Red-Pink
        else -> listOf(Color(0xFF10B981), Color(0xFF059669), Color(0xFF3B82F6)) // Emerald-Teal-Blue
    }

    val lowerTitle = title.lowercase()
    val productIcon = when {
        lowerTitle.contains("speaker") || lowerTitle.contains("audio") -> "🔊"
        lowerTitle.contains("headphone") || lowerTitle.contains("earbud") || lowerTitle.contains("earphone") -> "🎧"
        lowerTitle.contains("watch") || lowerTitle.contains("band") -> "⌚"
        lowerTitle.contains("bottle") || lowerTitle.contains("flask") -> "🥤"
        lowerTitle.contains("lens") || lowerTitle.contains("camera") -> "📷"
        lowerTitle.contains("phone") || lowerTitle.contains("mobile") -> "📱"
        lowerTitle.contains("laptop") || lowerTitle.contains("book") -> "💻"
        lowerTitle.contains("shoe") || lowerTitle.contains("sneaker") -> "👟"
        lowerTitle.contains("bag") || lowerTitle.contains("pack") -> "🎒"
        else -> "📦"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(colors))
    ) {
        // Draw gorgeous geometric background accents
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                radius = h * 0.6f,
                center = Offset(w * 0.85f, h * 0.2f)
            )
            drawCircle(
                color = Color.Black.copy(alpha = 0.1f),
                radius = h * 0.4f,
                center = Offset(w * 0.15f, h * 0.8f)
            )
            val path = Path().apply {
                moveTo(w * 0.3f, 0f)
                lineTo(w * 0.5f, 0f)
                lineTo(w * 0.2f, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path = path, color = Color.White.copy(alpha = 0.08f))
        }

        if (!imageUrl.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = productIcon,
                            style = TextStyle(
                                fontSize = if (height > 150.dp) 72.sp else 48.sp
                            )
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = productIcon,
                            style = TextStyle(
                                fontSize = if (height > 150.dp) 72.sp else 48.sp
                            )
                        )
                    }
                }
            )
        } else {
            // Centerpiece Product Illustration Mockup
            Box(
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    text = productIcon,
                    style = TextStyle(
                        fontSize = if (height > 150.dp) 72.sp else 48.sp
                    )
                )
            }
        }

        // Glassmorphism Badge Overlay
        Row(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.TopStart)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.Black.copy(alpha = 0.35f))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = badgeText,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (title.isNotBlank()) {
            Text(
                text = title.take(15),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

object ProductTint {
    @Composable
    fun forSeed(seed: Int) = when (seed % 3) {
        0 -> MaterialTheme.colorScheme.primaryContainer
        1 -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.tertiaryContainer
    }
}
