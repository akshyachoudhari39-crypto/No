package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
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
import com.example.data.model.PlayerStats
import com.example.ui.theme.*

@Composable
fun GamingTopBar(
  playerStats: PlayerStats,
  onEditNickname: (String) -> Unit = {}
) {
  var showEditDialog by remember { mutableStateOf(false) }
  var editedName by remember { mutableStateOf(playerStats.nickname) }

  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = BattleDark.copy(alpha = 0.95f),
    tonalElevation = 4.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Player profile badge
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(BattleCard)
          .border(1.dp, FireOrange.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
          .clickable { showEditDialog = true }
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        // Level badge
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                listOf(FireOrange, BooyahGold)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${playerStats.level}",
            color = Color.Black,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
          )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = playerStats.nickname,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              maxLines = 1
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit Nickname",
              tint = BooyahGold,
              modifier = Modifier.size(12.dp)
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = playerStats.rankTitle,
              color = BooyahGold,
              fontWeight = FontWeight.SemiBold,
              fontSize = 10.sp
            )
            Spacer(modifier = Modifier.width(3.dp))
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = "Rank Stars",
              tint = BooyahGold,
              modifier = Modifier.size(11.dp)
            )
            Text(
              text = "x${playerStats.rankStars}",
              color = Color.White.copy(alpha = 0.8f),
              fontSize = 10.sp
            )
          }
        }
      }

      // Currency balances (Diamonds & Gold)
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Diamonds
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BattleCardElevated)
            .border(1.dp, GlooCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Diamond,
            contentDescription = "Diamonds",
            tint = GlooCyan,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "${playerStats.diamonds}",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }

        // Gold Coins
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BattleCardElevated)
            .border(1.dp, BooyahGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Icon(
            imageVector = Icons.Default.MonetizationOn,
            contentDescription = "Gold Coins",
            tint = BooyahGold,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "${playerStats.goldCoins}",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      }
    }
  }

  if (showEditDialog) {
    AlertDialog(
      onDismissRequest = { showEditDialog = false },
      title = {
        Text(
          text = "Edit Nickname (नाम बदलें)",
          fontWeight = FontWeight.Bold,
          color = FireOrange
        )
      },
      text = {
        Column {
          Text(
            text = "Enter your Free Fire in-game name:",
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.8f)
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = editedName,
            onValueChange = { editedName = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (editedName.isNotBlank()) {
              onEditNickname(editedName.trim())
            }
            showEditDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange)
        ) {
          Text("Save", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditDialog = false }) {
          Text("Cancel", color = Color.White.copy(alpha = 0.7f))
        }
      },
      containerColor = BattleCard,
      tonalElevation = 6.dp
    )
  }
}
