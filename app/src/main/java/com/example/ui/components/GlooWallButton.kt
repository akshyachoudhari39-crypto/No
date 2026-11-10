package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BattleDark
import com.example.ui.theme.GlooCyan
import com.example.ui.theme.GlooCyanDark

@Composable
fun GlooWallButton(
  wallsCount: Int,
  isShieldActive: Boolean,
  onDeploy: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .size(64.dp)
      .testTag("gloo_wall_button")
      .clip(CircleShape)
      .background(
        Brush.radialGradient(
          colors = listOf(
            if (isShieldActive) GlooCyan.copy(alpha = 0.9f) else Color(0xFF00363A).copy(alpha = 0.85f),
            BattleDark
          )
        )
      )
      .border(
        width = 2.dp,
        brush = Brush.linearGradient(
          listOf(GlooCyan, if (isShieldActive) Color.White else GlooCyanDark)
        ),
        shape = CircleShape
      )
      .clickable(enabled = wallsCount > 0 && !isShieldActive) {
        onDeploy()
      },
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = Icons.Default.Shield,
        contentDescription = "Gloo Wall",
        tint = if (isShieldActive) Color.White else GlooCyan,
        modifier = Modifier.size(26.dp)
      )
      Text(
        text = if (isShieldActive) "ACTIVE" else "GLOO",
        color = if (isShieldActive) Color.White else GlooCyan,
        fontSize = 9.sp,
        fontWeight = FontWeight.Black
      )
    }

    // Badge showing remaining Gloo Walls
    Box(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .offset(x = (-2).dp, y = 2.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(if (wallsCount > 0) GlooCyan else Color.Gray)
        .padding(horizontal = 4.dp, vertical = 1.dp)
    ) {
      Text(
        text = "$wallsCount",
        color = Color.Black,
        fontWeight = FontWeight.Black,
        fontSize = 9.sp
      )
    }
  }
}
