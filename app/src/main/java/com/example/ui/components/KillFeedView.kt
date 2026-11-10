package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KillFeedItem
import com.example.ui.theme.BattleDark
import com.example.ui.theme.BooyahGold
import com.example.ui.theme.DangerRed
import com.example.ui.theme.HealthRed

@Composable
fun KillFeedView(
  killFeed: List<KillFeedItem>,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(4.dp),
    horizontalAlignment = Alignment.End
  ) {
    killFeed.takeLast(3).forEach { item ->
      AnimatedVisibility(
        visible = true,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BattleDark.copy(alpha = 0.85f))
            .border(
              width = 1.dp,
              color = if (item.isHeadshot) DangerRed.copy(alpha = 0.8f) else BooyahGold.copy(alpha = 0.4f),
              shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = item.killer,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )

          Spacer(modifier = Modifier.width(4.dp))

          // Weapon / Headshot icon
          if (item.isHeadshot) {
            Icon(
              imageVector = Icons.Default.CrisisAlert,
              contentDescription = "Headshot",
              tint = DangerRed,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = item.weaponName,
              color = HealthRed,
              fontWeight = FontWeight.Black,
              fontSize = 9.sp
            )
          } else {
            Icon(
              imageVector = Icons.Default.GpsFixed,
              contentDescription = "Eliminated",
              tint = BooyahGold,
              modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = item.weaponName,
              color = BooyahGold,
              fontWeight = FontWeight.SemiBold,
              fontSize = 9.sp
            )
          }

          Spacer(modifier = Modifier.width(4.dp))

          Text(
            text = item.victim,
            color = Color.White.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp
          )
        }
      }
    }
  }
}
