package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameStateRepository
import com.example.data.model.Weapon
import com.example.data.model.WeaponCategory
import com.example.ui.theme.*
import com.example.util.VibratorHelper

@Composable
fun ArmoryScreen(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  BackHandler { onBack() }

  val weapons by GameStateRepository.weaponsList.collectAsState()
  val equippedWeapon by GameStateRepository.equippedWeapon.collectAsState()

  var selectedCategory by remember { mutableStateOf<WeaponCategory?>(null) }
  var testFiringMessage by remember { mutableStateOf<String?>(null) }

  val filteredWeapons = remember(selectedCategory, weapons) {
    if (selectedCategory == null) weapons else weapons.filter { it.category == selectedCategory }
  }

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
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Text(
          text = "WEAPON ARMORY (हथियार भंडार)",
          color = FireOrange,
          fontWeight = FontWeight.Black,
          fontSize = 16.sp
        )
      }
    }

    // Category Tabs
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      FilterChip(
        selected = selectedCategory == null,
        onClick = { selectedCategory = null },
        label = { Text("ALL", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = FireOrange,
          selectedLabelColor = Color.White,
          containerColor = BattleCard,
          labelColor = Color.White.copy(alpha = 0.7f)
        )
      )
      listOf(WeaponCategory.SMG, WeaponCategory.AR, WeaponCategory.SHOTGUN, WeaponCategory.SNIPER).forEach { cat ->
        FilterChip(
          selected = selectedCategory == cat,
          onClick = { selectedCategory = cat },
          label = { Text(cat.name, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = FireOrange,
            selectedLabelColor = Color.White,
            containerColor = BattleCard,
            labelColor = Color.White.copy(alpha = 0.7f)
          )
        )
      }
    }

    // Weapons List
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(horizontal = 14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(filteredWeapons) { wp ->
        val isEquipped = wp.id == equippedWeapon.id

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
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = wp.name,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = wp.category.name,
                    color = GlooCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  )
                }
                Text(
                  text = wp.skinName,
                  color = BooyahGold,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 12.sp
                )
              }

              if (isEquipped) {
                Text(
                  text = "EQUIPPED",
                  color = SafeGreen,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp
                )
              } else {
                Button(
                  onClick = { GameStateRepository.equipWeapon(wp) },
                  colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("EQUIP", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stat bars (Damage, Rate of Fire, Range, Accuracy)
            WeaponStatRow(label = "DAMAGE", value = wp.baseDamage, maxValue = 150, color = DangerRed)
            WeaponStatRow(label = "HEADSHOT", value = wp.headshotDamage, maxValue = 300, color = BooyahGold)
            WeaponStatRow(label = "RATE OF FIRE", value = wp.rateOfFire, maxValue = 100, color = FireOrange)
            WeaponStatRow(label = "RANGE", value = wp.range, maxValue = 100, color = GlooCyan)
            WeaponStatRow(label = "ACCURACY", value = wp.accuracy, maxValue = 100, color = SafeGreen)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = wp.description,
              color = Color.White.copy(alpha = 0.65f),
              fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Test fire button
            OutlinedButton(
              onClick = {
                VibratorHelper.vibrateShot(context, true)
                testFiringMessage = "Fired ${wp.name}! Recoil Haptics triggered."
              },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.VolumeUp, contentDescription = "Sound", tint = BooyahGold, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("TEST FIRE & RECOIL (फायर टेस्ट)", color = BooyahGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

@Composable
private fun WeaponStatRow(
  label: String,
  value: Int,
  maxValue: Int,
  color: Color
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      color = Color.White.copy(alpha = 0.7f),
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.width(80.dp)
    )
    LinearProgressIndicator(
      progress = { (value.toFloat() / maxValue).coerceIn(0f, 1f) },
      modifier = Modifier
        .weight(1f)
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp)),
      color = color,
      trackColor = Color(0xFF262A38)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = "$value",
      color = Color.White,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.width(26.dp)
    )
  }
}
