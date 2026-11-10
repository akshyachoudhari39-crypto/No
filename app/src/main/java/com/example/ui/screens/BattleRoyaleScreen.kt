package com.example.ui.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.GameStateRepository
import com.example.data.model.*
import com.example.ui.components.FireButton
import com.example.ui.components.GlooWallButton
import com.example.ui.components.KillFeedView
import com.example.ui.theme.*
import com.example.util.VibratorHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun BattleRoyaleScreen(
  onReturnToLobby: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val equippedWeapon by GameStateRepository.equippedWeapon.collectAsState()
  val equippedCharacter by GameStateRepository.equippedCharacter.collectAsState()

  BackHandler {
    onReturnToLobby()
  }

  // Match State
  var matchStatus by remember { mutableStateOf(MatchStatus.DROP_PHASE) }
  var selectedDropLocation by remember { mutableStateOf(GameStateRepository.defaultDropLocations[0]) }
  var dropAltitude by remember { mutableIntStateOf(1200) }

  // Combat State
  var playerHp by remember { mutableIntStateOf(200) }
  var playerEp by remember { mutableIntStateOf(150) }
  var helmetDurability by remember { mutableIntStateOf(100) }
  var vestDurability by remember { mutableIntStateOf(100) }
  var glooWalls by remember { mutableIntStateOf(4) }
  var medkits by remember { mutableIntStateOf(3) }
  var isGlooShieldActive by remember { mutableStateOf(false) }
  var glooShieldTimeLeft by remember { mutableIntStateOf(0) }

  // Character skill state
  var isSkillActive by remember { mutableStateOf(false) }
  var skillCooldownTime by remember { mutableIntStateOf(0) }

  // Game flow state
  var aliveCount by remember { mutableIntStateOf(50) }
  var killCount by remember { mutableIntStateOf(0) }
  var headshotKills by remember { mutableIntStateOf(0) }
  var totalDamageDealt by remember { mutableIntStateOf(0) }
  var safeZoneCountdown by remember { mutableIntStateOf(30) }
  var safeZoneShrinkStage by remember { mutableIntStateOf(1) }
  var airdropAvailable by remember { mutableStateOf(false) }

  // Active enemies list
  var currentTarget by remember {
    mutableStateOf(
      EnemyTarget(
        id = "enemy_1",
        name = "Toxic_Ninja",
        distanceMeters = 24,
        health = 200,
        angleDeg = 45f,
        weaponName = "MP40"
      )
    )
  }

  // Killfeed list
  val killFeed = remember { mutableStateListOf<KillFeedItem>() }

  // Floating damage indicators
  val floatingDamages = remember { mutableStateListOf<FloatingDamage>() }

  // Active weapon in hands
  var activeWeapon by remember { mutableStateOf(equippedWeapon) }

  // Combat background loops
  LaunchedEffect(matchStatus) {
    if (matchStatus == MatchStatus.IN_COMBAT) {
      while (matchStatus == MatchStatus.IN_COMBAT) {
        delay(1000)

        // Safe zone timer
        if (safeZoneCountdown > 0) {
          safeZoneCountdown--
        } else {
          safeZoneCountdown = 25
          safeZoneShrinkStage++
        }

        // Gloo shield decay
        if (isGlooShieldActive) {
          if (glooShieldTimeLeft > 0) {
            glooShieldTimeLeft--
          } else {
            isGlooShieldActive = false
          }
        }

        // Character skill aura & cooldown
        if (isSkillActive) {
          // Alok restores HP
          if (equippedCharacter.id == "alok" && playerHp < 200) {
            playerHp = (playerHp + 5).coerceAtMost(200)
          }
        }
        if (skillCooldownTime > 0) {
          skillCooldownTime--
          if (skillCooldownTime == 0) {
            isSkillActive = false
          }
        }

        // EP natural conversion to HP if injured
        if (playerHp < 200 && playerEp > 0) {
          playerEp = (playerEp - 2).coerceAtLeast(0)
          playerHp = (playerHp + 2).coerceAtMost(200)
        }

        // Simulate other bots fighting in the island
        if (Random.nextInt(100) < 45 && aliveCount > 2) {
          aliveCount = (aliveCount - 1).coerceAtLeast(2)
          killFeed.add(GameStateRepository.getRandomBotKillFeedItem())
          if (killFeed.size > 5) killFeed.removeAt(0)
        }

        // Chance of airdrop falling
        if (Random.nextInt(100) < 15 && !airdropAvailable) {
          airdropAvailable = true
        }

        // Enemy shooting back if alive and not shielded
        if (!currentTarget.isDead && matchStatus == MatchStatus.IN_COMBAT) {
          if (Random.nextInt(100) < 35) {
            if (isGlooShieldActive) {
              // Gloo wall absorbs 100% damage!
            } else {
              val incomingDamage = Random.nextInt(12, 28)
              if (vestDurability > 0) {
                vestDurability = (vestDurability - 10).coerceAtLeast(0)
                playerHp = (playerHp - (incomingDamage * 0.6).toInt()).coerceAtLeast(0)
              } else {
                playerHp = (playerHp - incomingDamage).coerceAtLeast(0)
              }
              VibratorHelper.vibrateShot(context, false)
              if (playerHp <= 0) {
                matchStatus = MatchStatus.ELIMINATED
                GameStateRepository.recordMatchResult(false, killCount, headshotKills)
              }
            }
          }
        }
      }
    }
  }

  // Clear floating damages after short delay
  LaunchedEffect(floatingDamages.size) {
    if (floatingDamages.isNotEmpty()) {
      delay(800)
      if (floatingDamages.isNotEmpty()) {
        floatingDamages.removeAt(0)
      }
    }
  }

  // Drop Phase Countdown Animation
  LaunchedEffect(matchStatus, dropAltitude) {
    if (matchStatus == MatchStatus.DROP_PHASE && dropAltitude < 1200) {
      while (dropAltitude > 0) {
        delay(200)
        dropAltitude = (dropAltitude - 150).coerceAtLeast(0)
      }
      matchStatus = MatchStatus.IN_COMBAT
    }
  }

  // FIRE HANDLER
  fun handleFire(isHeadshot: Boolean) {
    if (currentTarget.isDead || matchStatus != MatchStatus.IN_COMBAT) return

    val damage = if (isHeadshot) {
      activeWeapon.headshotDamage + Random.nextInt(0, 15)
    } else {
      activeWeapon.baseDamage + Random.nextInt(-3, 6)
    }

    VibratorHelper.vibrateShot(context, isHeadshot)

    // Add floating damage indicator
    floatingDamages.add(
      FloatingDamage(
        id = System.currentTimeMillis(),
        damage = damage,
        isHeadshot = isHeadshot,
        xOffset = Random.nextInt(-40, 40).toFloat(),
        yOffset = Random.nextInt(-40, 10).toFloat()
      )
    )

    totalDamageDealt += damage
    val remainingHp = currentTarget.health - damage
    currentTarget.health = remainingHp

    if (remainingHp <= 0) {
      // Enemy eliminated!
      currentTarget.isDead = true
      killCount++
      if (isHeadshot) headshotKills++
      aliveCount = (aliveCount - 1).coerceAtLeast(1)

      killFeed.add(
        KillFeedItem(
          killer = GameStateRepository.playerStats.value.nickname,
          victim = currentTarget.name,
          weaponName = activeWeapon.name,
          isHeadshot = isHeadshot
        )
      )

      // Check Victory
      if (aliveCount <= 1) {
        matchStatus = MatchStatus.VICTORY_BOOYAH
        VibratorHelper.vibrateBooyah(context)
        GameStateRepository.recordMatchResult(true, killCount, headshotKills)
      } else {
        // Spawn next enemy after brief moment
        val botNames = listOf("AWM_Master", "Ghost_Killer", "Sniper_Girl", "Apex_Predator", "Cobra_Boy")
        val nextName = botNames.random()
        currentTarget = EnemyTarget(
          id = "enemy_${System.currentTimeMillis()}",
          name = nextName,
          distanceMeters = Random.nextInt(15, 45),
          health = 200,
          angleDeg = Random.nextInt(0, 360).toFloat(),
          weaponName = listOf("AK47", "M1887", "MP40", "Groza").random()
        )
      }
    }
  }

  // Main UI by Match Status
  when (matchStatus) {
    MatchStatus.DROP_PHASE -> {
      DropPhaseScreen(
        selectedLocation = selectedDropLocation,
        onSelectLocation = { selectedDropLocation = it },
        dropAltitude = dropAltitude,
        onStartDrop = { dropAltitude = 1199 },
        onCancel = onReturnToLobby
      )
    }

    MatchStatus.IN_COMBAT -> {
      Box(
        modifier = modifier
          .fillMaxSize()
          .background(BattleDark)
      ) {
        Column(modifier = Modifier.fillMaxSize()) {
          // Top Match Status Bar (Alive, Kills, Safe Zone, KillFeed)
          CombatTopHeader(
            aliveCount = aliveCount,
            killCount = killCount,
            headshotKills = headshotKills,
            safeZoneTimer = safeZoneCountdown,
            killFeed = killFeed,
            onExit = onReturnToLobby
          )

          // Tactical Battlefield View (Radar / Crosshair / Enemy Target / Gloo Wall)
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
              .padding(horizontal = 12.dp)
          ) {
            TacticalRadarArena(
              enemy = currentTarget,
              isGlooActive = isGlooShieldActive,
              glooSecondsLeft = glooShieldTimeLeft,
              floatingDamages = floatingDamages,
              airdropAvailable = airdropAvailable,
              onLootAirdrop = {
                airdropAvailable = false
                glooWalls += 2
                medkits += 1
                vestDurability = 100
                helmetDurability = 100
              }
            )
          }

          // Player Status Bar (HP, EP, Armor, Active Buffs)
          PlayerStatusHUD(
            hp = playerHp,
            ep = playerEp,
            vest = vestDurability,
            helmet = helmetDurability,
            character = equippedCharacter,
            isSkillActive = isSkillActive,
            skillCooldown = skillCooldownTime,
            onActivateSkill = {
              if (skillCooldownTime == 0) {
                isSkillActive = true
                skillCooldownTime = equippedCharacter.cooldownSeconds
              }
            }
          )

          // Action Controls HUD (Weapons, Gloo Wall, Medkit, Fire Button)
          CombatControlsHUD(
            activeWeapon = activeWeapon,
            glooWallsCount = glooWalls,
            isGlooActive = isGlooShieldActive,
            medkitsCount = medkits,
            onDeployGloo = {
              if (glooWalls > 0 && !isGlooShieldActive) {
                glooWalls--
                isGlooShieldActive = true
                glooShieldTimeLeft = 7
                VibratorHelper.vibrateGlooWall(context)
              }
            },
            onUseMedkit = {
              if (medkits > 0 && playerHp < 200) {
                medkits--
                playerHp = (playerHp + 75).coerceAtMost(200)
              }
            },
            onUseMushroom = {
              playerEp = 200
            },
            onSwitchWeapon = { newWeapon ->
              activeWeapon = newWeapon
            },
            onFire = { isHeadshot ->
              handleFire(isHeadshot)
            }
          )
        }
      }
    }

    MatchStatus.VICTORY_BOOYAH -> {
      BooyahVictoryScreen(
        kills = killCount,
        headshots = headshotKills,
        damage = totalDamageDealt,
        rankPoints = 45,
        onPlayAgain = {
          // Reset match state
          playerHp = 200
          playerEp = 150
          glooWalls = 4
          medkits = 3
          aliveCount = 50
          killCount = 0
          headshotKills = 0
          totalDamageDealt = 0
          dropAltitude = 1200
          matchStatus = MatchStatus.DROP_PHASE
        },
        onReturnToLobby = onReturnToLobby
      )
    }

    MatchStatus.ELIMINATED -> {
      EliminatedScreen(
        rank = aliveCount + 1,
        kills = killCount,
        onTryAgain = {
          playerHp = 200
          playerEp = 150
          glooWalls = 4
          medkits = 3
          aliveCount = 50
          killCount = 0
          dropAltitude = 1200
          matchStatus = MatchStatus.DROP_PHASE
        },
        onReturnToLobby = onReturnToLobby
      )
    }
  }
}

@Composable
private fun DropPhaseScreen(
  selectedLocation: DropLocation,
  onSelectLocation: (DropLocation) -> Unit,
  dropAltitude: Int,
  onStartDrop: () -> Unit,
  onCancel: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(BattleDark)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onCancel) {
          Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.White)
        }
        Text(
          text = "CHOOSE DROP ZONE (लैंडिंग जगह चुनें)",
          color = BooyahGold,
          fontWeight = FontWeight.Black,
          fontSize = 14.sp
        )
        Box(modifier = Modifier.size(48.dp))
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Tactical Island Map Card
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(240.dp)
          .clip(RoundedCornerShape(16.dp))
          .border(2.dp, FireOrange, RoundedCornerShape(16.dp))
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_bermuda_map_1794282013773),
          contentDescription = "Bermuda Island Map",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Drop Pin at selected location
        Box(
          modifier = Modifier
            .align(Alignment.Center)
            .clip(CircleShape)
            .background(DangerRed)
            .padding(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Pin",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Drop locations chips
      Text(
        text = "POPULAR BERMUDA HOT-DROPS:",
        color = Color.White.copy(alpha = 0.7f),
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        GameStateRepository.defaultDropLocations.take(3).forEach { loc ->
          FilterChip(
            selected = selectedLocation.id == loc.id,
            onClick = { onSelectLocation(loc) },
            label = { Text(loc.name, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = FireOrange,
              selectedLabelColor = Color.White,
              containerColor = BattleCard,
              labelColor = Color.White.copy(alpha = 0.8f)
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Location details card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BattleCard),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(listOf(FireOrange, BattleCardBorder))
        )
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = selectedLocation.name,
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 15.sp
            )
            Text(
              text = selectedLocation.lootTier,
              color = BooyahGold,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = selectedLocation.description,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.weight(1f))

      if (dropAltitude < 1200) {
        // Parachute descending countdown
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          CircularProgressIndicator(color = FireOrange)
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "PARACHUTING TO ${selectedLocation.name.uppercase()}...",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp
          )
          Text(
            text = "Altitude: ${dropAltitude}m",
            color = BooyahGold,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
      } else {
        Button(
          onClick = onStartDrop,
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("jump_drop_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.FlightTakeoff, contentDescription = "Drop", tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "JUMP & PARACHUTE (कूदें)",
              color = Color.Black,
              fontWeight = FontWeight.Black,
              fontSize = 16.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun CombatTopHeader(
  aliveCount: Int,
  killCount: Int,
  headshotKills: Int,
  safeZoneTimer: Int,
  killFeed: List<KillFeedItem>,
  onExit: () -> Unit
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = BattleDark.copy(alpha = 0.95f),
    tonalElevation = 6.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Alive & Kills stats badge
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Alive
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BattleCard)
            .border(1.dp, SafeGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Icon(Icons.Default.Group, contentDescription = "Alive", tint = SafeGreen, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "ALIVE: $aliveCount",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
          )
        }

        // Kills
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BattleCard)
            .border(1.dp, DangerRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Icon(Icons.Default.CrisisAlert, contentDescription = "Kills", tint = DangerRed, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "KILLS: $killCount",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
          )
        }
      }

      // Safe zone timer
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF261208))
          .border(1.dp, FireOrange.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Icon(Icons.Default.Timer, contentDescription = "Zone Timer", tint = FireOrange, modifier = Modifier.size(13.dp))
        Spacer(modifier = Modifier.width(3.dp))
        Text(
          text = "ZONE: 00:${safeZoneTimer.toString().padStart(2, '0')}",
          color = FireOrange,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
      }

      // Exit button
      IconButton(
        onClick = onExit,
        modifier = Modifier.size(28.dp)
      ) {
        Icon(Icons.Default.Logout, contentDescription = "Leave Match", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
      }
    }
  }

  // Killfeed right under top header
  if (killFeed.isNotEmpty()) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 2.dp)
    ) {
      KillFeedView(
        killFeed = killFeed,
        modifier = Modifier.align(Alignment.TopEnd)
      )
    }
  }
}

@Composable
private fun TacticalRadarArena(
  enemy: EnemyTarget,
  isGlooActive: Boolean,
  glooSecondsLeft: Int,
  floatingDamages: List<FloatingDamage>,
  airdropAvailable: Boolean,
  onLootAirdrop: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .clip(RoundedCornerShape(16.dp))
      .background(
        Brush.radialGradient(
          colors = listOf(Color(0xFF141926), BattleDark)
        )
      )
      .border(1.dp, BattleCardBorder, RoundedCornerShape(16.dp)),
    contentAlignment = Alignment.Center
  ) {
    // Canvas for Radar Grid & Rings
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2, size.height / 2)
      val radius = size.width.coerceAtMost(size.height) * 0.45f

      // Outer Safe Zone Ring
      drawCircle(
        color = GlooCyan.copy(alpha = 0.35f),
        radius = radius,
        center = center,
        style = Stroke(width = 2f)
      )

      // Inner 30m / 50m rings
      drawCircle(
        color = Color.White.copy(alpha = 0.1f),
        radius = radius * 0.65f,
        center = center,
        style = Stroke(width = 1.5f)
      )

      // Crosshair guide lines
      drawLine(
        color = Color.White.copy(alpha = 0.08f),
        start = Offset(center.x, center.y - radius),
        end = Offset(center.x, center.y + radius),
        strokeWidth = 1f
      )
      drawLine(
        color = Color.White.copy(alpha = 0.08f),
        start = Offset(center.x - radius, center.y),
        end = Offset(center.x + radius, center.y),
        strokeWidth = 1f
      )

      // Player Arrow at center
      drawCircle(
        color = BooyahGold,
        radius = 8f,
        center = center
      )
    }

    // Gloo Wall Shield Barrier (If deployed)
    if (isGlooActive) {
      Box(
        modifier = Modifier
          .align(Alignment.Center)
          .offset(y = (-45).dp)
          .clip(RoundedCornerShape(12.dp))
          .background(GlooCyan.copy(alpha = 0.35f))
          .border(2.dp, GlooCyan, RoundedCornerShape(12.dp))
          .padding(horizontal = 14.dp, vertical = 6.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Shield, contentDescription = "Shield Active", tint = GlooCyan, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "GLOO WALL SHIELD ACTIVE (${glooSecondsLeft}s)",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
          )
        }
      }
    }

    // Airdrop Crate Alert
    if (airdropAvailable) {
      Box(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(12.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(Color(0xFF3E2723))
          .border(1.5.dp, BooyahGold, RoundedCornerShape(10.dp))
          .clickable { onLootAirdrop() }
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CardGiftcard, contentDescription = "Airdrop", tint = BooyahGold, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text("AIRDROP CRATE LANDED!", color = BooyahGold, fontWeight = FontWeight.Black, fontSize = 10.sp)
            Text("TAP TO LOOT GROZA & VEST", color = Color.White, fontSize = 9.sp)
          }
        }
      }
    }

    // Enemy Target Locked in Crosshair
    Column(
      modifier = Modifier
        .align(Alignment.Center)
        .offset(y = (-30).dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Enemy card & health
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(BattleDark.copy(alpha = 0.85f))
          .border(1.dp, DangerRed, RoundedCornerShape(8.dp))
          .padding(horizontal = 8.dp, vertical = 3.dp)
      ) {
        Text(
          text = enemy.name,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "${enemy.distanceMeters}m",
          color = BooyahGold,
          fontSize = 10.sp
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Enemy HP Bar
      LinearProgressIndicator(
        progress = { (enemy.health.toFloat() / enemy.maxHealth).coerceIn(0f, 1f) },
        modifier = Modifier
          .width(100.dp)
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = DangerRed,
        trackColor = Color.DarkGray
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Crosshair Target Center Icon
      Box(
        modifier = Modifier
          .size(54.dp)
          .clip(CircleShape)
          .border(2.dp, if (enemy.health < 80) DangerRed else FireOrange, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.GpsFixed,
          contentDescription = "Target",
          tint = if (enemy.health < 80) DangerRed else BooyahGold,
          modifier = Modifier.size(32.dp)
        )
      }
    }

    // Floating Animated Damage Numbers
    floatingDamages.forEach { dmg ->
      Text(
        text = "${dmg.damage}",
        color = if (dmg.isHeadshot) DangerRed else Color(0xFFFFD600),
        fontSize = if (dmg.isHeadshot) 28.sp else 20.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier
          .align(Alignment.Center)
          .offset(x = dmg.xOffset.dp, y = (dmg.yOffset - 60).dp)
      )
    }
  }
}

@Composable
private fun PlayerStatusHUD(
  hp: Int,
  ep: Int,
  vest: Int,
  helmet: Int,
  character: Character,
  isSkillActive: Boolean,
  skillCooldown: Int,
  onActivateSkill: () -> Unit
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = BattleDark,
    tonalElevation = 4.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
      // HP and EP Bars
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // HP Bar
        Column(modifier = Modifier.weight(1f)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "HP $hp / 200",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 11.sp
            )
            Text(
              text = "HELMET $helmet% | VEST $vest%",
              color = Color.White.copy(alpha = 0.6f),
              fontSize = 9.sp
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          LinearProgressIndicator(
            progress = { (hp / 200f).coerceIn(0f, 1f) },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = if (hp > 60) Color(0xFF00E676) else DangerRed,
            trackColor = Color(0xFF333333)
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Character Skill Button (Alok / Chrono / Kelly)
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(if (isSkillActive) FireOrange else if (skillCooldown == 0) GlooCyanDark else Color.DarkGray)
            .border(2.dp, if (skillCooldown == 0) GlooCyan else Color.Transparent, CircleShape)
            .clickable(enabled = skillCooldown == 0) { onActivateSkill() },
          contentAlignment = Alignment.Center
        ) {
          if (skillCooldown > 0) {
            Text(
              text = "${skillCooldown}s",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 10.sp
            )
          } else {
            Icon(
              imageVector = Icons.Default.Bolt,
              contentDescription = character.skillName,
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // EP Bar (Yellow EP Bar)
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "EP $ep / 200",
          color = EpYellow,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp,
          modifier = Modifier.width(70.dp)
        )
        LinearProgressIndicator(
          progress = { (ep / 200f).coerceIn(0f, 1f) },
          modifier = Modifier
            .weight(1f)
            .height(5.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = EpYellow,
          trackColor = Color(0xFF333333)
        )
      }
    }
  }
}

@Composable
private fun CombatControlsHUD(
  activeWeapon: Weapon,
  glooWallsCount: Int,
  isGlooActive: Boolean,
  medkitsCount: Int,
  onDeployGloo: () -> Unit,
  onUseMedkit: () -> Unit,
  onUseMushroom: () -> Unit,
  onSwitchWeapon: (Weapon) -> Unit,
  onFire: (isHeadshot: Boolean) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(BattleCardElevated)
      .padding(horizontal = 12.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Bottom
    ) {
      // Left side: Medkit, Mushroom, Gloo Wall
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          // Medkit button
          FilledTonalButton(
            onClick = onUseMedkit,
            enabled = medkitsCount > 0,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            colors = ButtonDefaults.filledTonalButtonColors(containerColor = BattleCard)
          ) {
            Icon(Icons.Default.MedicalServices, contentDescription = "Medkit", tint = SafeGreen, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("MEDKIT ($medkitsCount)", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }

          // Mushroom button
          FilledTonalButton(
            onClick = onUseMushroom,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
            colors = ButtonDefaults.filledTonalButtonColors(containerColor = BattleCard)
          ) {
            Icon(Icons.Default.Eco, contentDescription = "Mushroom", tint = EpYellow, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("MUSHROOM", color = EpYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }

        // Gloo Wall Tactical Button
        GlooWallButton(
          wallsCount = glooWallsCount,
          isShieldActive = isGlooActive,
          onDeploy = onDeployGloo
        )
      }

      // Middle: Weapon Switch Slot
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 6.dp)
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          GameStateRepository.defaultWeapons.take(3).forEach { wp ->
            val isSelected = wp.id == activeWeapon.id
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) FireOrange.copy(alpha = 0.3f) else BattleCard)
                .border(1.dp, if (isSelected) FireOrange else BattleCardBorder, RoundedCornerShape(8.dp))
                .clickable { onSwitchWeapon(wp) }
                .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
              Text(
                text = wp.name,
                color = if (isSelected) BooyahGold else Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "${activeWeapon.name} - ${activeWeapon.skinName}",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp
        )
      }

      // Right: Authentic Drag-up Fire Button
      FireButton(
        sizeDp = 80,
        onFire = onFire
      )
    }
  }
}

@Composable
private fun BooyahVictoryScreen(
  kills: Int,
  headshots: Int,
  damage: Int,
  rankPoints: Int,
  onPlayAgain: () -> Unit,
  onReturnToLobby: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(BattleDark)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Spacer(modifier = Modifier.height(10.dp))

      // Booyah Victory Banner Image
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(230.dp)
          .clip(RoundedCornerShape(20.dp))
          .border(2.dp, BooyahGold, RoundedCornerShape(20.dp))
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_booyah_banner_1794282030106),
          contentDescription = "BOOYAH Victory Banner",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      }

      // Victory Stats Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BattleCard),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(listOf(BooyahGold, FireOrange))
        )
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "MATCH SUMMARY (मैच परिणाम)",
            color = BooyahGold,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            StatItem(label = "KILLS", value = "$kills", icon = Icons.Default.CrisisAlert, tint = DangerRed)
            StatItem(label = "HEADSHOTS", value = "$headshots", icon = Icons.Default.GpsFixed, tint = BooyahGold)
            StatItem(label = "DAMAGE", value = "$damage", icon = Icons.Default.LocalFireDepartment, tint = FireOrange)
            StatItem(label = "RANK PTS", value = "+$rankPoints ⭐", icon = Icons.Default.Star, tint = BooyahGold)
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "REWARD: +850 Gold Coins 🪙 | +15 Diamonds 💎",
            color = SafeGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }

      // Action Buttons
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = onPlayAgain,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange)
        ) {
          Text("PLAY AGAIN (फिर से खेलें)", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 15.sp)
        }

        OutlinedButton(
          onClick = onReturnToLobby,
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("RETURN TO LOBBY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
      }
    }
  }
}

@Composable
private fun EliminatedScreen(
  rank: Int,
  kills: Int,
  onTryAgain: () -> Unit,
  onReturnToLobby: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(BattleDark),
    contentAlignment = Alignment.Center
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .padding(16.dp),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = BattleCard),
      border = CardDefaults.outlinedCardBorder().copy(
        brush = Brush.verticalGradient(listOf(DangerRed, BattleCardBorder))
      )
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Icon(
          imageVector = Icons.Default.SentimentVeryDissatisfied,
          contentDescription = "Eliminated",
          tint = DangerRed,
          modifier = Modifier.size(54.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "ELIMINATED! (खत्म)",
          color = DangerRed,
          fontWeight = FontWeight.Black,
          fontSize = 20.sp
        )

        Text(
          text = "Rank #$rank / 50",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Kills: $kills • Tip: Use Gloo Walls instantly when under fire!",
          color = Color.White.copy(alpha = 0.7f),
          fontSize = 12.sp,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = onTryAgain,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange)
        ) {
          Text("TRY AGAIN", color = Color.Black, fontWeight = FontWeight.Black)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onReturnToLobby) {
          Text("RETURN TO LOBBY", color = Color.White.copy(alpha = 0.7f))
        }
      }
    }
  }
}

@Composable
private fun StatItem(
  label: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  tint: Color
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
    Spacer(modifier = Modifier.height(3.dp))
    Text(text = value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
    Text(text = label, color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp)
  }
}
