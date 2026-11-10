package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.model.Character
import com.example.ui.theme.*

@Composable
fun CharactersScreen(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val characters by GameStateRepository.charactersList.collectAsState()
  val equippedCharacter by GameStateRepository.equippedCharacter.collectAsState()
  val playerStats by GameStateRepository.playerStats.collectAsState()

  var snackbarMsg by remember { mutableStateOf<String?>(null) }

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
            text = "CHARACTERS (कैरेक्टर्स)",
            color = FireOrange,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp
          )
        }

        // Gold coin balance
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(end = 12.dp)
        ) {
          Icon(Icons.Default.MonetizationOn, contentDescription = "Gold", tint = BooyahGold, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("${playerStats.goldCoins}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }
    }

    if (snackbarMsg != null) {
      Surface(
        color = FireOrange,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = snackbarMsg ?: "",
          color = Color.White,
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
      items(characters) { ch ->
        val isEquipped = ch.id == equippedCharacter.id

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = BattleCard),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
              listOf(if (isEquipped) FireOrange else BattleCardBorder, BattleCardBorder)
            )
          )
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                      if (isEquipped) Brush.radialGradient(listOf(FireOrange, FireOrangeDark))
                      else Brush.radialGradient(listOf(BattleCardBorder, BattleDark))
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = ch.name,
                    tint = if (isEquipped) Color.White else Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(28.dp)
                  )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                  Text(
                    text = ch.name,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                  )
                  Text(
                    text = "${ch.role} • LV. ${ch.level}",
                    color = BooyahGold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                  )
                }
              }

              if (isEquipped) {
                Text(
                  text = "IN USE",
                  color = SafeGreen,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp
                )
              } else {
                Button(
                  onClick = {
                    GameStateRepository.equipCharacter(ch)
                    snackbarMsg = "Equipped ${ch.name} successfully!"
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
                  contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("EQUIP", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Skill details badge
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(BattleCardElevated)
                .padding(10.dp)
            ) {
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "Ability: ${ch.skillName}",
                    color = GlooCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                  )
                  Text(
                    text = if (ch.cooldownSeconds > 0) "Cooldown: ${ch.cooldownSeconds}s" else "Passive Skill",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.sp
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = ch.skillDescription,
                  color = Color.White.copy(alpha = 0.8f),
                  fontSize = 11.sp
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Upgrade level button
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (ch.level >= 6) "MAX LEVEL REACHED" else "Upgrade to LV.${ch.level + 1}",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp
              )

              if (ch.level < 6) {
                OutlinedButton(
                  onClick = {
                    val success = GameStateRepository.upgradeCharacter(ch.id)
                    if (success) {
                      snackbarMsg = "Upgraded ${ch.name} to Level ${ch.level + 1}!"
                    } else {
                      snackbarMsg = "Not enough gold! Earn gold by playing Battle Royale."
                    }
                  },
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                ) {
                  Icon(Icons.Default.ArrowUpward, contentDescription = "Upgrade", tint = BooyahGold, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("UPGRADE (2,500 🪙)", color = BooyahGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
      item {
        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}
