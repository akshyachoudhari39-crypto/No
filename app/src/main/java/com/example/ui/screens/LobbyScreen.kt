package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.GameStateRepository
import com.example.data.model.Character
import com.example.data.model.GameScreen
import com.example.data.model.Weapon
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun LobbyScreen(
  onNavigate: (GameScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  val equippedCharacter by GameStateRepository.equippedCharacter.collectAsState()
  val equippedWeapon by GameStateRepository.equippedWeapon.collectAsState()
  val playerStats by GameStateRepository.playerStats.collectAsState()

  var selectedEmote by remember { mutableStateOf<String?>(null) }
  var showModeSelectDialog by remember { mutableStateOf(false) }

  LaunchedEffect(selectedEmote) {
    if (selectedEmote != null) {
      delay(2500)
      selectedEmote = null
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(BattleDark)
      .padding(horizontal = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top Hero Banner
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
          .clip(RoundedCornerShape(18.dp))
          .border(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(listOf(FireOrange, GlooCyan)),
            shape = RoundedCornerShape(18.dp)
          )
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_hero_lobby_1794282013815),
          contentDescription = "Bermuda Battle Royale Hero Banner",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay for readability
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color.Transparent,
                  Color.Black.copy(alpha = 0.5f),
                  BattleDark.copy(alpha = 0.95f)
                )
              )
            )
        )

        // Banner overlay text & season tag
        Column(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(14.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(FireOrange)
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "SEASON 38 RANKED",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 10.sp
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "BERMUDA BATTLE ROYALE",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp
          )

          Text(
            text = "50 Players • 10 Mins • 1 BOOYAH!",
            color = BooyahGold,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
          )
        }

        // Active Emote Pop-up
        AnimatedVisibility(
          visible = selectedEmote != null,
          enter = scaleIn() + fadeIn(),
          exit = scaleOut() + fadeOut(),
          modifier = Modifier.align(Alignment.Center)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(BattleCard.copy(alpha = 0.95f))
              .border(2.dp, BooyahGold, RoundedCornerShape(16.dp))
              .padding(horizontal = 18.dp, vertical = 12.dp)
          ) {
            Text(
              text = selectedEmote ?: "",
              color = BooyahGold,
              fontWeight = FontWeight.Black,
              fontSize = 16.sp
            )
          }
        }
      }
    }

    // Survivor & Loadout Showcase Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BattleCard),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(listOf(FireOrange.copy(alpha = 0.5f), BattleCardBorder))
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
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(
                    Brush.radialGradient(listOf(FireOrange, FireOrangeDark))
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = "Character",
                  tint = Color.White,
                  modifier = Modifier.size(28.dp)
                )
              }

              Spacer(modifier = Modifier.width(10.dp))

              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = equippedCharacter.name,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "LV.${equippedCharacter.level}",
                    color = BooyahGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                }
                Text(
                  text = "Skill: ${equippedCharacter.skillName}",
                  color = GlooCyan,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 12.sp
                )
              }
            }

            OutlinedButton(
              onClick = { onNavigate(GameScreen.CHARACTERS) },
              shape = RoundedCornerShape(10.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.horizontalGradient(listOf(FireOrange, BooyahGold))
              )
            ) {
              Text("CHANGE", color = FireOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Equipped weapon badge
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(BattleCardElevated)
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "Weapon",
                tint = FireOrange,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text(
                  text = "${equippedWeapon.name} - ${equippedWeapon.skinName}",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
                Text(
                  text = "Rate of Fire: ${equippedWeapon.rateOfFire}% | Damage: ${equippedWeapon.baseDamage}",
                  color = Color.White.copy(alpha = 0.6f),
                  fontSize = 10.sp
                )
              }
            }

            TextButton(
              onClick = { onNavigate(GameScreen.ARMORY) },
              contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text("ARMORY", color = GlooCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // MAIN START BUTTON - Huge Free Fire Style Yellow/Orange Play Button
    item {
      Button(
        onClick = { onNavigate(GameScreen.BATTLE_ROYALE) },
        modifier = Modifier
          .fillMaxWidth()
          .height(58.dp)
          .testTag("start_match_button")
          .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = FireOrange),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = FireOrange
        ),
        contentPadding = PaddingValues(0.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.horizontalGradient(
                listOf(FireOrange, Color(0xFFFF9100), BooyahGold)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Start Match",
              tint = Color.Black,
              modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.Start) {
              Text(
                text = "START MATCH",
                color = Color.Black,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                letterSpacing = 1.sp
              )
              Text(
                text = "BERMUDA BATTLE ROYALE (RANKED)",
                color = Color.Black.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              )
            }
          }
        }
      }
    }

    // Game Mode Selection Cards
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Clash Squad (CS Mode) Card
        Card(
          modifier = Modifier
            .weight(1f)
            .clickable { onNavigate(GameScreen.CLASH_SQUAD) }
            .testTag("clash_squad_card"),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = BattleCard),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(GlooCyan.copy(alpha = 0.6f), BattleCardBorder))
          )
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(
                imageVector = Icons.Default.SportsKabaddi,
                contentDescription = "Clash Squad",
                tint = GlooCyan,
                modifier = Modifier.size(24.dp)
              )
              Text(
                text = "4v4",
                color = GlooCyan,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "CLASH SQUAD",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 13.sp
            )
            Text(
              text = "Round Buy & 4v4 Faceoff",
              color = Color.White.copy(alpha = 0.6f),
              fontSize = 10.sp
            )
          }
        }

        // Bermuda Map Tactical Radar Card
        Card(
          modifier = Modifier
            .weight(1f)
            .clickable { onNavigate(GameScreen.BERMUDA_MAP) }
            .testTag("bermuda_map_card"),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = BattleCard),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(BooyahGold.copy(alpha = 0.6f), BattleCardBorder))
          )
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = "Bermuda Map",
                tint = BooyahGold,
                modifier = Modifier.size(24.dp)
              )
              Text(
                text = "RADAR",
                color = BooyahGold,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "BERMUDA MAP",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 13.sp
            )
            Text(
              text = "Clock Tower, Peak, Factory",
              color = Color.White.copy(alpha = 0.6f),
              fontSize = 10.sp
            )
          }
        }
      }
    }

    // Emotes Bar (Iconic Free Fire Emotes!)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BattleCardElevated)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.EmojiEmotions,
                contentDescription = "Emotes",
                tint = BooyahGold,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "QUICK EMOTES (इमोट्स)",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
            Text(
              text = "Tap to Play",
              color = Color.White.copy(alpha = 0.5f),
              fontSize = 10.sp
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            val emotes = listOf(
              "🏆 BOOYAH!" to "🏆 *Lifts Golden Booyah Cup with Fireworks!*",
              "☕ Tea Time" to "☕ *Sits on Royal Throne sipping Tea!*",
              "😂 Lol Laugh" to "😂 *Points and Laughs hysterically!*",
              "🐶 Doggie" to "🐶 *Cute Shiba Inu Dance with Player!*",
              "👏 Applause" to "👏 *Claps for the Victory!*"
            )

            emotes.forEach { (label, actionText) ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(BattleCard)
                  .border(1.dp, BattleCardBorder, RoundedCornerShape(10.dp))
                  .clickable { selectedEmote = actionText }
                  .padding(horizontal = 8.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = label,
                  color = Color.White,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 10.sp
                )
              }
            }
          }
        }
      }
    }

    // Pro Sensitivity & Aim Drag Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigate(GameScreen.SENSITIVITY) }
          .testTag("sensitivity_banner"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BattleCard),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(listOf(FireOrangeDark, BattleCardBorder))
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = "Sensitivity Settings",
              tint = FireOrange,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "PRO SENSITIVITY & HEADSHOT SETTINGS",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
              Text(
                text = "Red Dot 90 • General 95 • Drag Headshot Trick",
                color = BooyahGold,
                fontSize = 10.sp
              )
            }
          }

          Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Open",
            tint = Color.White.copy(alpha = 0.7f)
          )
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
