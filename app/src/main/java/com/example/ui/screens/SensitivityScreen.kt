package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameStateRepository
import com.example.data.model.SensitivityConfig
import com.example.ui.theme.*

@Composable
fun SensitivityScreen(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val currentConfig by GameStateRepository.sensitivity.collectAsState()

  var general by remember { mutableFloatStateOf(currentConfig.general.toFloat()) }
  var redDot by remember { mutableFloatStateOf(currentConfig.redDot.toFloat()) }
  var scope2x by remember { mutableFloatStateOf(currentConfig.scope2x.toFloat()) }
  var scope4x by remember { mutableFloatStateOf(currentConfig.scope4x.toFloat()) }
  var awmScope by remember { mutableFloatStateOf(currentConfig.awmScope.toFloat()) }
  var freeLook by remember { mutableFloatStateOf(currentConfig.freeLook.toFloat()) }
  var fireButtonSize by remember { mutableFloatStateOf(currentConfig.fireButtonSize.toFloat()) }

  var savedNotice by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(BattleDark)
  ) {
    // Top Bar
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = BattleCard,
      tonalElevation = 4.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
          Text(
            text = "PRO SENSITIVITY & HEADSHOT HUD",
            color = FireOrange,
            fontWeight = FontWeight.Black,
            fontSize = 15.sp
          )
        }

        IconButton(
          onClick = {
            // Reset to Free Fire Pro Defaults
            general = 95f
            redDot = 90f
            scope2x = 85f
            scope4x = 75f
            awmScope = 60f
            freeLook = 80f
            fireButtonSize = 55f
            GameStateRepository.updateSensitivity(
              SensitivityConfig(
                general = 95,
                redDot = 90,
                scope2x = 85,
                scope4x = 75,
                awmScope = 60,
                freeLook = 80,
                fireButtonSize = 55
              )
            )
          }
        ) {
          Icon(Icons.Default.Refresh, contentDescription = "Reset Defaults", tint = BooyahGold)
        }
      }
    }

    if (savedNotice) {
      Surface(
        color = SafeGreen,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = "✓ Settings Saved to Cloud Profile!",
          color = Color.Black,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          modifier = Modifier.padding(8.dp)
        )
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Drag Headshot Pro Tutorial Guide Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = BattleCard),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(DangerRed, FireOrange))
          )
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CrisisAlert, contentDescription = "Headshot Trick", tint = DangerRed, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "ONE TAP & DRAG HEADSHOT TRICK (हेडशॉट ट्रिक)",
                color = BooyahGold,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "1. White to Red Crosshair: Place your aim near the enemy's chest.\n" +
                  "2. Upward Flick: Drag the FIRE button quickly towards the sky!\n" +
                  "3. Sensitivity Secret: Keep Red Dot at 90-100 for maximum friction lock on enemy helmets.\n" +
                  "4. Gloo Wall Combo: Immediately tap Gloo Wall after shooting to cancel enemy counter-attack.",
              color = Color.White.copy(alpha = 0.85f),
              fontSize = 11.sp,
              lineHeight = 17.sp
            )
          }
        }
      }

      // Sliders
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = BattleCard)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "IN-GAME SENSITIVITY SLIDERS:",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            SensitivitySlider(label = "General (जनरल)", value = general, onValueChange = { general = it })
            SensitivitySlider(label = "Red Dot (रेड डॉट)", value = redDot, onValueChange = { redDot = it })
            SensitivitySlider(label = "2X Scope (2X स्कोप)", value = scope2x, onValueChange = { scope2x = it })
            SensitivitySlider(label = "4X Scope (4X स्कोप)", value = scope4x, onValueChange = { scope4x = it })
            SensitivitySlider(label = "Sniper Scope (स्नाइपर स्कोप AWM)", value = awmScope, onValueChange = { awmScope = it })
            SensitivitySlider(label = "Free Look (फ्री लुक)", value = freeLook, onValueChange = { freeLook = it })
            SensitivitySlider(label = "Fire Button Size (बटन साइज)", value = fireButtonSize, onValueChange = { fireButtonSize = it })

            Spacer(modifier = Modifier.height(10.dp))

            Button(
              onClick = {
                GameStateRepository.updateSensitivity(
                  SensitivityConfig(
                    general = general.toInt(),
                    redDot = redDot.toInt(),
                    scope2x = scope2x.toInt(),
                    scope4x = scope4x.toInt(),
                    awmScope = awmScope.toInt(),
                    freeLook = freeLook.toInt(),
                    fireButtonSize = fireButtonSize.toInt()
                  )
                )
                savedNotice = true
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = FireOrange)
            ) {
              Text("APPLY SENSITIVITY (लागू करें)", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}

@Composable
private fun SensitivitySlider(
  label: String,
  value: Float,
  onValueChange: (Float) -> Unit
) {
  Column(modifier = Modifier.padding(vertical = 4.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(label, color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
      Text("${value.toInt()}%", color = BooyahGold, fontSize = 12.sp, fontWeight = FontWeight.Black)
    }
    Slider(
      value = value,
      onValueChange = onValueChange,
      valueRange = 0f..100f,
      colors = SliderDefaults.colors(
        thumbColor = FireOrange,
        activeTrackColor = FireOrange,
        inactiveTrackColor = Color(0xFF232838)
      )
    )
  }
}
