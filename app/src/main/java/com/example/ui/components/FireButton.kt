package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BooyahGold
import com.example.ui.theme.DangerRed
import com.example.ui.theme.FireOrange
import kotlin.math.abs

@Composable
fun FireButton(
  sizeDp: Int = 76,
  onFire: (isHeadshot: Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  var isPressed by remember { mutableStateOf(false) }
  var totalDragY by remember { mutableStateOf(0f) }
  var firedInCurrentGesture by remember { mutableStateOf(false) }

  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.92f else 1.0f,
    label = "fire_button_scale"
  )

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Hint arrow for drag headshot
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(bottom = 2.dp)
    ) {
      Icon(
        imageVector = Icons.Default.KeyboardDoubleArrowUp,
        contentDescription = "Drag up for headshot",
        tint = if (totalDragY < -40f) DangerRed else BooyahGold.copy(alpha = 0.8f),
        modifier = Modifier.size(16.dp)
      )
      Text(
        text = if (totalDragY < -40f) "HEADSHOT DRAG!" else "DRAG UP",
        color = if (totalDragY < -40f) DangerRed else BooyahGold.copy(alpha = 0.8f),
        fontSize = 9.sp,
        fontWeight = FontWeight.Black
      )
    }

    Box(
      modifier = Modifier
        .size(sizeDp.dp)
        .scale(scale)
        .testTag("fire_button")
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            colors = listOf(
              if (totalDragY < -40f) DangerRed.copy(alpha = 0.85f) else FireOrange.copy(alpha = 0.85f),
              Color(0xFF2A150D).copy(alpha = 0.9f)
            )
          )
        )
        .border(
          width = 3.dp,
          brush = Brush.linearGradient(
            listOf(BooyahGold, if (totalDragY < -40f) DangerRed else FireOrange)
          ),
          shape = CircleShape
        )
        .pointerInput(Unit) {
          detectDragGestures(
            onDragStart = {
              isPressed = true
              totalDragY = 0f
              firedInCurrentGesture = false
            },
            onDragEnd = {
              if (!firedInCurrentGesture) {
                val isHeadshot = totalDragY < -35f
                onFire(isHeadshot)
              }
              isPressed = false
              totalDragY = 0f
              firedInCurrentGesture = false
            },
            onDragCancel = {
              isPressed = false
              totalDragY = 0f
              firedInCurrentGesture = false
            },
            onDrag = { change, dragAmount ->
              change.consume()
              totalDragY += dragAmount.y
              // If dragged up sufficiently during active swipe, fire instant headshot
              if (totalDragY < -60f && !firedInCurrentGesture) {
                onFire(true)
                firedInCurrentGesture = true
              }
            }
          )
        },
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        // Outer crosshair ring
        drawCircle(
          color = Color.White.copy(alpha = 0.2f),
          radius = size.width * 0.38f
        )
      }

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.LocalFireDepartment,
          contentDescription = "Fire",
          tint = if (totalDragY < -40f) DangerRed else BooyahGold,
          modifier = Modifier.size((sizeDp * 0.42f).dp)
        )
        Text(
          text = "FIRE",
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Black
        )
      }
    }
  }
}
