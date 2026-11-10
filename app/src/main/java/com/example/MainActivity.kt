package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameStateRepository
import com.example.data.model.GameScreen
import com.example.ui.components.GamingTopBar
import com.example.ui.screens.*
import com.example.ui.theme.BattleCard
import com.example.ui.theme.BattleDark
import com.example.ui.theme.BooyahGold
import com.example.ui.theme.FireOrange
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainGameApp()
      }
    }
  }
}

@Composable
fun MainGameApp() {
  var currentScreen by remember { mutableStateOf(GameScreen.LOBBY) }
  val playerStats by GameStateRepository.playerStats.collectAsState()

  val showBottomNav = currentScreen != GameScreen.BATTLE_ROYALE && currentScreen != GameScreen.CLASH_SQUAD

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(BattleDark),
    topBar = {
      if (showBottomNav) {
        GamingTopBar(
          playerStats = playerStats,
          onEditNickname = { newName ->
            // Update player nickname in repository
          }
        )
      }
    },
    bottomBar = {
      if (showBottomNav) {
        NavigationBar(
          containerColor = BattleCard,
          tonalElevation = 8.dp
        ) {
          NavigationBarItem(
            selected = currentScreen == GameScreen.LOBBY,
            onClick = { currentScreen = GameScreen.LOBBY },
            icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Lobby") },
            label = { Text("LOBBY", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = FireOrange,
              selectedTextColor = FireOrange,
              indicatorColor = FireOrange.copy(alpha = 0.2f),
              unselectedIconColor = Color.White.copy(alpha = 0.6f),
              unselectedTextColor = Color.White.copy(alpha = 0.6f)
            ),
            modifier = Modifier.testTag("nav_lobby")
          )

          NavigationBarItem(
            selected = currentScreen == GameScreen.BERMUDA_MAP,
            onClick = { currentScreen = GameScreen.BERMUDA_MAP },
            icon = { Icon(Icons.Default.Explore, contentDescription = "Map") },
            label = { Text("BERMUDA", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = BooyahGold,
              selectedTextColor = BooyahGold,
              indicatorColor = BooyahGold.copy(alpha = 0.2f),
              unselectedIconColor = Color.White.copy(alpha = 0.6f),
              unselectedTextColor = Color.White.copy(alpha = 0.6f)
            ),
            modifier = Modifier.testTag("nav_bermuda")
          )

          NavigationBarItem(
            selected = currentScreen == GameScreen.ARMORY,
            onClick = { currentScreen = GameScreen.ARMORY },
            icon = { Icon(Icons.Default.LocalFireDepartment, contentDescription = "Armory") },
            label = { Text("ARMORY", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = FireOrange,
              selectedTextColor = FireOrange,
              indicatorColor = FireOrange.copy(alpha = 0.2f),
              unselectedIconColor = Color.White.copy(alpha = 0.6f),
              unselectedTextColor = Color.White.copy(alpha = 0.6f)
            ),
            modifier = Modifier.testTag("nav_armory")
          )

          NavigationBarItem(
            selected = currentScreen == GameScreen.CHARACTERS,
            onClick = { currentScreen = GameScreen.CHARACTERS },
            icon = { Icon(Icons.Default.Person, contentDescription = "Characters") },
            label = { Text("HEROES", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = FireOrange,
              selectedTextColor = FireOrange,
              indicatorColor = FireOrange.copy(alpha = 0.2f),
              unselectedIconColor = Color.White.copy(alpha = 0.6f),
              unselectedTextColor = Color.White.copy(alpha = 0.6f)
            ),
            modifier = Modifier.testTag("nav_characters")
          )

          NavigationBarItem(
            selected = currentScreen == GameScreen.SENSITIVITY,
            onClick = { currentScreen = GameScreen.SENSITIVITY },
            icon = { Icon(Icons.Default.Tune, contentDescription = "Sensitivity") },
            label = { Text("SETTINGS", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = FireOrange,
              selectedTextColor = FireOrange,
              indicatorColor = FireOrange.copy(alpha = 0.2f),
              unselectedIconColor = Color.White.copy(alpha = 0.6f),
              unselectedTextColor = Color.White.copy(alpha = 0.6f)
            ),
            modifier = Modifier.testTag("nav_sensitivity")
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
        when (screen) {
          GameScreen.LOBBY -> LobbyScreen(onNavigate = { currentScreen = it })
          GameScreen.BATTLE_ROYALE -> BattleRoyaleScreen(onReturnToLobby = { currentScreen = GameScreen.LOBBY })
          GameScreen.CLASH_SQUAD -> ClashSquadScreen(onReturnToLobby = { currentScreen = GameScreen.LOBBY })
          GameScreen.BERMUDA_MAP -> BermudaMapScreen(onBack = { currentScreen = GameScreen.LOBBY })
          GameScreen.ARMORY -> ArmoryScreen(onBack = { currentScreen = GameScreen.LOBBY })
          GameScreen.CHARACTERS -> CharactersScreen(onBack = { currentScreen = GameScreen.LOBBY })
          GameScreen.SENSITIVITY -> SensitivityScreen(onBack = { currentScreen = GameScreen.LOBBY })
        }
      }
    }
  }
}
