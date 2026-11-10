package com.example.data.model

enum class GameScreen {
  LOBBY,
  BATTLE_ROYALE,
  CLASH_SQUAD,
  BERMUDA_MAP,
  ARMORY,
  CHARACTERS,
  SENSITIVITY
}

enum class MatchStatus {
  DROP_PHASE,
  IN_COMBAT,
  VICTORY_BOOYAH,
  ELIMINATED
}

enum class WeaponCategory {
  SMG,
  AR,
  SHOTGUN,
  SNIPER,
  PISTOL,
  MELEE
}

data class Weapon(
  val id: String,
  val name: String,
  val skinName: String,
  val category: WeaponCategory,
  val baseDamage: Int,
  val headshotDamage: Int,
  val rateOfFire: Int, // 1 - 100
  val accuracy: Int,
  val range: Int,
  val magazineSize: Int,
  val currentAmmo: Int,
  val description: String
)

data class Character(
  val id: String,
  val name: String,
  val role: String,
  val skillName: String,
  val skillDescription: String,
  val cooldownSeconds: Int,
  val level: Int = 5,
  val isEquipped: Boolean = false
)

data class DropLocation(
  val id: String,
  val name: String,
  val zoneType: String, // Hot Drop, Medium, Safe
  val lootTier: String, // Tier 3 (High), Tier 2, Tier 1
  val dangerScore: Int, // 1 to 5
  val description: String,
  val xRatio: Float,
  val yRatio: Float
)

data class KillFeedItem(
  val killer: String,
  val victim: String,
  val weaponName: String,
  val isHeadshot: Boolean,
  val timeMillis: Long = System.currentTimeMillis()
)

data class EnemyTarget(
  val id: String,
  val name: String,
  val distanceMeters: Int,
  var health: Int,
  val maxHealth: Int = 200,
  val angleDeg: Float,
  val weaponName: String,
  val isBot: Boolean = true,
  var isDead: Boolean = false
)

data class FloatingDamage(
  val id: Long,
  val damage: Int,
  val isHeadshot: Boolean,
  val xOffset: Float,
  val yOffset: Float
)

data class SensitivityConfig(
  val general: Int = 95,
  val redDot: Int = 90,
  val scope2x: Int = 85,
  val scope4x: Int = 75,
  val awmScope: Int = 60,
  val freeLook: Int = 80,
  val fireButtonSize: Int = 55
)

data class PlayerStats(
  val nickname: String = "꧁★FIRE_STRIKE★꧂",
  val rankTitle: String = "Grandmaster",
  val rankStars: Int = 42,
  val level: Int = 58,
  val diamonds: Int = 2850,
  val goldCoins: Int = 34500,
  val totalKills: Int = 1840,
  val totalMatches: Int = 320,
  val totalBooyahs: Int = 168,
  val headshotRatePercent: Int = 68
)
