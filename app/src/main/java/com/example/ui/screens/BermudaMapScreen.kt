package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.GameStateRepository
import com.example.data.model.DropLocation
import com.example.ui.theme.*

@Composable
fun BermudaMapScreen(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val dropLocations = GameStateRepository.defaultDropLocations
  var selectedLocation by remember { mutableStateOf(dropLocations[0]) }

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
          text = "BERMUDA MAP RADAR (बरमूडा मैप)",
          color = FireOrange,
          fontWeight = FontWeight.Black,
          fontSize = 16.sp
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
      // Map Image Card
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(2.dp, Brush.horizontalGradient(listOf(FireOrange, GlooCyan)), RoundedCornerShape(18.dp))
        ) {
          Image(
            painter = painterResource(id = R.drawable.img_bermuda_map_1794282013773),
            contentDescription = "Bermuda Island Map Full Radar",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )

          // Selected zone pin badge
          Surface(
            modifier = Modifier
              .align(Alignment.BottomCenter)
              .padding(10.dp)
              .clip(RoundedCornerShape(10.dp)),
            color = BattleDark.copy(alpha = 0.9f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.LocationOn, contentDescription = "Zone", tint = FireOrange, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Viewing: ${selectedLocation.name} (${selectedLocation.zoneType})",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }
        }
      }

      // Selected Zone Details Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = BattleCard),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(BooyahGold, BattleCardBorder))
          )
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = selectedLocation.name,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Danger: ",
                  color = Color.White.copy(alpha = 0.6f),
                  fontSize = 11.sp
                )
                repeat(selectedLocation.dangerScore) {
                  Icon(Icons.Default.Whatshot, contentDescription = "Fire", tint = DangerRed, modifier = Modifier.size(14.dp))
                }
              }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "Loot Level: ${selectedLocation.lootTier}",
              color = BooyahGold,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = selectedLocation.description,
              color = Color.White.copy(alpha = 0.8f),
              fontSize = 12.sp
            )
          }
        }
      }

      // Hot-drops List
      item {
        Text(
          text = "ALL BERMUDA LANDMARKS & DROP ZONES:",
          color = Color.White.copy(alpha = 0.7f),
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
      }

      items(dropLocations) { loc ->
        val isSelected = loc.id == selectedLocation.id

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) BattleCardElevated else BattleCard)
            .border(1.dp, if (isSelected) FireOrange else BattleCardBorder, RoundedCornerShape(12.dp))
            .clickable { selectedLocation = loc }
            .padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(
              text = loc.name,
              color = if (isSelected) BooyahGold else Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Text(
              text = "${loc.zoneType} • ${loc.lootTier}",
              color = Color.White.copy(alpha = 0.6f),
              fontSize = 11.sp
            )
          }

          Icon(
            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.ChevronRight,
            contentDescription = "Select",
            tint = if (isSelected) FireOrange else Color.White.copy(alpha = 0.4f)
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}
