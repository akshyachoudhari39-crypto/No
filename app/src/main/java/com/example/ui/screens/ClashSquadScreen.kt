package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameStateRepository
import com.example.data.model.Weapon
import com.example.ui.components.FireButton
import com.example.ui.components.GlooWallButton
import com.example.ui.theme.*
import com.example.util.VibratorHelper
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun ClashSquadScreen(
  onReturnToLobby: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  BackHandler { onReturnToLobby() }

  // CS Match State
  var roundNumber by remember { mutableIntStateOf(1) }
  var teamBlueScore by remember { mutableIntStateOf(0) }
  var teamRedScore by remember { mutableIntStateOf(0) }
  var csCash by remember { mutableIntStateOf(1400) }
  var isBuyPhase by remember { mutableStateOf(true) }
  var buyPhaseTimer by remember { mutableIntStateOf(10) }

  // Round Combat State
  var playerHp by remember { mutableIntStateOf(200) }
  var glooWalls by remember { mutableIntStateOf(2) }
  var isGlooActive by remember { mutableStateOf(false) }
  var enemyHp by remember { mutableIntStateOf(200) }
  var enemyName by remember { mutableStateOf("Red_Striker_1") }
  var enemyAliveCount by remember { mutableIntStateOf(4) }
  var allyAliveCount by remember { mutableIntStateOf(4) }
  var isRoundOver by remember { mutableStateOf(false) }
  var roundWon by remember { mutableStateOf(false) }

  // Inventory
  var equippedGun by remember { mutableStateOf(GameStateRepository.defaultWeapons[4]) } // Start with Desert Eagle
  var hasHelmet by remember { mutableStateOf(false) }
  var hasVest by remember { mutableStateOf(false) }

  // Buy phase countdown
  LaunchedEffect(isBuyPhase, buyPhaseTimer) {
    if (isBuyPhase && buyPhaseTimer > 0) {
      delay(1000)
      buyPhaseTimer--
      if (buyPhaseTimer == 0) {
        isBuyPhase = false
      }
    }
  }

  // Combat loop
  LaunchedEffect(isBuyPhase, isRoundOver) {
    if (!isBuyPhase && !isRoundOver) {
      while (!isBuyPhase && !isRoundOver) {
        delay(1200)
        // Bot allies/enemies shoot
        if (Random.nextInt(100) < 30 && enemyAliveCount > 1) {
          enemyAliveCount--
        }
        if (Random.nextInt(100) < 25 && allyAliveCount > 1) {
          allyAliveCount--
        }

        // Enemy fires at player
        if (playerHp > 0 && !isGlooActive && Random.nextInt(100) < 40) {
          val dmg = Random.nextInt(18, 35)
          playerHp = (playerHp - dmg).coerceAtLeast(0)
          VibratorHelper.vibrateShot(context, false)
          if (playerHp <= 0) {
            isRoundOver = true
            roundWon = false
            teamRedScore++
          }
        }
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(BattleDark)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Top Clash Squad Score Header (Blue 4v4 Red)
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BattleCard,
        tonalElevation = 6.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          IconButton(onClick = onReturnToLobby) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
          }

          // Team Scores
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "BLUE $teamBlueScore",
              color = GlooCyan,
              fontWeight = FontWeight.Black,
              fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = ":",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "$teamRedScore RED",
              color = DangerRed,
              fontWeight = FontWeight.Black,
              fontSize = 18.sp
            )
          }

          // Cash
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.MonetizationOn, contentDescription = "Cash", tint = BooyahGold, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "$$csCash",
              color = BooyahGold,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }
        }
      }

      if (isBuyPhase) {
        // Buy Phase Store Screen
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "ROUND $roundNumber BUY STORE",
                color = BooyahGold,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
              )
              Text(
                text = "Starts in ${buyPhaseTimer}s",
                color = FireOrange,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Weapon buy cards
            Text(
              text = "SELECT WEAPONS & GEAR:",
              color = Color.White.copy(alpha = 0.7f),
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            val shopItems = listOf(
              Triple("Desert Eagle", 800, GameStateRepository.defaultWeapons[4]),
              Triple("MP40 Cobra", 1500, GameStateRepository.defaultWeapons[0]),
              Triple("M1887 Shotgun", 1700, GameStateRepository.defaultWeapons[2]),
              Triple("AK47 Draco", 1600, GameStateRepository.defaultWeapons[1])
            )

            shopItems.forEach { (name, cost, wp) ->
              val isOwned = equippedGun.id == wp.id
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(BattleCard)
                  .border(1.dp, if (isOwned) FireOrange else BattleCardBorder, RoundedCornerShape(10.dp))
                  .clickable(enabled = csCash >= cost || isOwned) {
                    if (!isOwned && csCash >= cost) {
                      csCash -= cost
                      equippedGun = wp
                    }
                  }
                  .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                  Text("Damage: ${wp.baseDamage} • Fire Rate: ${wp.rateOfFire}%", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                }

                if (isOwned) {
                  Text("EQUIPPED", color = SafeGreen, fontWeight = FontWeight.Black, fontSize = 11.sp)
                } else {
                  Button(
                    onClick = {
                      if (csCash >= cost) {
                        csCash -= cost
                        equippedGun = wp
                      }
                    },
                    enabled = csCash >= cost,
                    colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                  ) {
                    Text("BUY $$cost", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Utility items
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = {
                  if (csCash >= 300) {
                    csCash -= 300
                    glooWalls += 2
                  }
                },
                enabled = csCash >= 300,
                modifier = Modifier.weight(1f)
              ) {
                Text("+2 Gloo Walls ($300)", color = GlooCyan, fontSize = 10.sp)
              }

              OutlinedButton(
                onClick = {
                  if (csCash >= 400 && !hasVest) {
                    csCash -= 400
                    hasVest = true
                  }
                },
                enabled = csCash >= 400 && !hasVest,
                modifier = Modifier.weight(1f)
              ) {
                Text(if (hasVest) "Vest Owned" else "Vest Lv.2 ($400)", color = BooyahGold, fontSize = 10.sp)
              }
            }
          }

          Button(
            onClick = { isBuyPhase = false },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FireOrange)
          ) {
            Text("ENTER ROUND FACE-OFF", color = Color.Black, fontWeight = FontWeight.Black)
          }
        }
      } else if (isRoundOver) {
        // Round Result Card
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Text(
            text = if (roundWon) "ROUND VICTORY!" else "ROUND DEFEAT!",
            color = if (roundWon) BooyahGold else DangerRed,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Blue Team: $teamBlueScore | Red Team: $teamRedScore",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )

          Spacer(modifier = Modifier.height(20.dp))

          if (teamBlueScore >= 4 || teamRedScore >= 4) {
            Text(
              text = if (teamBlueScore >= 4) "🏆 CLASH SQUAD BOOYAH! 🏆" else "MATCH OVER",
              color = if (teamBlueScore >= 4) BooyahGold else DangerRed,
              fontWeight = FontWeight.Black,
              fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
              onClick = onReturnToLobby,
              colors = ButtonDefaults.buttonColors(containerColor = FireOrange)
            ) {
              Text("CLAIM REWARDS & RETURN", color = Color.Black, fontWeight = FontWeight.Black)
            }
          } else {
            Button(
              onClick = {
                roundNumber++
                isBuyPhase = true
                buyPhaseTimer = 10
                csCash += if (roundWon) 2200 else 1400
                playerHp = 200
                enemyHp = 200
                enemyAliveCount = 4
                allyAliveCount = 4
                isRoundOver = false
              },
              colors = ButtonDefaults.buttonColors(containerColor = FireOrange)
            ) {
              Text("NEXT ROUND ($roundNumber / 7)", color = Color.Black, fontWeight = FontWeight.Black)
            }
          }
        }
      } else {
        // Live Round Combat Screen
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          // Squad Status Counters
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("ALLLIES ALIVE: $allyAliveCount/4", color = GlooCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text("ENEMIES ALIVE: $enemyAliveCount/4", color = DangerRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }

          // Enemy target box
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = BattleCard)
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(enemyName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
              Spacer(modifier = Modifier.height(4.dp))
              LinearProgressIndicator(
                progress = { (enemyHp / 200f).coerceIn(0f, 1f) },
                modifier = Modifier
                  .width(180.dp)
                  .height(8.dp)
                  .clip(RoundedCornerShape(4.dp)),
                color = DangerRed
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text("Distance: 12m (Close Range Clash)", color = BooyahGold, fontSize = 11.sp)
            }
          }

          // Player status & Gloo active indicator
          Column {
            if (isGlooActive) {
              Text("🛡️ GLOO WALL BLOCKING ENEMY BULLETS!", color = GlooCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("YOUR HP: $playerHp / 200", color = if (playerHp > 50) SafeGreen else DangerRed, fontWeight = FontWeight.Black, fontSize = 13.sp)
            LinearProgressIndicator(
              progress = { (playerHp / 200f).coerceIn(0f, 1f) },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = if (playerHp > 50) SafeGreen else DangerRed
            )
          }

          // Bottom Controls: Gloo Wall + Drag Fire Button
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
          ) {
            GlooWallButton(
              wallsCount = glooWalls,
              isShieldActive = isGlooActive,
              onDeploy = {
                if (glooWalls > 0 && !isGlooActive) {
                  glooWalls--
                  isGlooActive = true
                  VibratorHelper.vibrateGlooWall(context)
                }
              }
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("${equippedGun.name} (${equippedGun.skinName})", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Text("Drag up for headshot!", color = BooyahGold, fontSize = 9.sp)
            }

            FireButton(
              sizeDp = 76,
              onFire = { isHeadshot ->
                val damage = if (isHeadshot) equippedGun.headshotDamage else equippedGun.baseDamage
                VibratorHelper.vibrateShot(context, isHeadshot)
                val remaining = enemyHp - damage
                enemyHp = remaining
                if (remaining <= 0) {
                  enemyAliveCount = (enemyAliveCount - 1).coerceAtLeast(0)
                  if (enemyAliveCount <= 0) {
                    isRoundOver = true
                    roundWon = true
                    teamBlueScore++
                    VibratorHelper.vibrateBooyah(context)
                  } else {
                    enemyHp = 200
                    enemyName = "Red_Striker_${5 - enemyAliveCount}"
                  }
                }
              }
            )
          }
        }
      }
    }
  }
}
