package com.v2ray.ang.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.ui.main.MainActivity
import kotlinx.coroutines.delay

/**
 * Animated launch screen for Hossein Akbar VPN.
 * Shows the brand name with a small "connecting" flourish, then hands off
 * to MainActivity. Purely cosmetic -- no VPN state is touched here.
 */
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen(onFinished = {
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            })
        }
    }
}

private val BgTop = Color(0xFF0A2530)
private val BgBottom = Color(0xFF0F4C5C)
private val Accent = Color(0xFF22D3C5)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    // Entrance: logo scales/fades in with a slight overshoot ("bamze" bounce).
    val logoScale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    var ringProgress by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    LaunchedEffect(Unit) {
        logoAlpha.animateTo(1f, tween(420, easing = LinearOutSlowInEasing))
        logoScale.animateTo(1f, tween(650, easing = EaseOutBack))
        titleAlpha.animateTo(1f, tween(420))
        delay(120)
        subtitleAlpha.animateTo(1f, tween(420))

        // Little loading ring sweep, like a connection handshake.
        val start = System.currentTimeMillis()
        val duration = 900L
        while (System.currentTimeMillis() - start < duration) {
            val t = (System.currentTimeMillis() - start) / duration.toFloat()
            ringProgress = t.coerceIn(0f, 1f)
            delay(16)
        }
        ringProgress = 1f

        delay(500)
        onFinished()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BgTop
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BgTop, BgBottom))),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Handshake ring
                    Canvas(
                        modifier = Modifier
                            .size(148.dp)
                    ) {
                        drawArc(
                            color = Accent.copy(alpha = 0.9f),
                            startAngle = -90f,
                            sweepAngle = 360f * ringProgress,
                            useCenter = false,
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Monogram tile
                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .scale(logoScale.value * pulse)
                            .clip(RoundedCornerShape(26.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF0A3A4A), Color(0xFF116E82))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "H&A",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.alpha(logoAlpha.value)
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(top = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hossein Akbar VPN",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.alpha(titleAlpha.value)
                    )
                    Text(
                        text = "در حال آماده‌سازی اتصال…",
                        color = Accent,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .alpha(subtitleAlpha.value)
                    )
                }
            }

            Text(
                text = "Hossein Akbar VPN",
                color = Color.White.copy(alpha = 0.28f),
                fontSize = 11.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 28.dp)
            )
        }
    }
}
