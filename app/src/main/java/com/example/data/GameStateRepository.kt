package com.example.data

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

object GameStateRepository {

  val defaultWeapons = listOf(
    Weapon(
      id = "mp40_cobra",
      name = "MP40",
      skinName = "Predator Cobra (Evo)",
      category = WeaponCategory.SMG,
      baseDamage = 26,
      headshotDamage = 138,
      rateOfFire = 92,
      accuracy = 78,
      range = 45,
      magazineSize = 32,
      currentAmmo = 32,
      description = "Free Fire's most iconic close-range SMG. Blistering rate of fire with instant headshot drag capability!"
    ),
    Weapon(
      id = "ak47_draco",
      name = "AK47",
      skinName = "Blue Flame Draco (Evo)",
      category = WeaponCategory.AR,
      baseDamage = 38,
      headshotDamage = 186,
      rateOfFire = 68,
      accuracy = 65,
      range = 75,
      magazineSize = 30,
      currentAmmo = 30,
      description = "Devastating assault rifle with high armor penetration. Master the upward drag recoil for instant kills."
    ),
    Weapon(
      id = "m1887_rapper",
      name = "M1887",
      skinName = "Rapper Slayer",
      category = WeaponCategory.SHOTGUN,
      baseDamage = 85,
      headshotDamage = 245,
      rateOfFire = 42,
      accuracy = 45,
      range = 25,
      magazineSize = 2,
      currentAmmo = 2,
      description = "The deadly double-barrel shotgun. High burst damage capable of one-shotting enemies in close combat."
    ),
    Weapon(
      id = "awm_duke",
      name = "AWM",
      skinName = "Duke Swallowtail",
      category = WeaponCategory.SNIPER,
      baseDamage = 150,
      headshotDamage = 300,
      rateOfFire = 28,
      accuracy = 95,
      range = 95,
      magazineSize = 5,
      currentAmmo = 5,
      description = "Air-drop sniper rifle. Penetrates level 3 helmets effortlessly. One shot, one kill."
    ),
    Weapon(
      id = "desert_eagle",
      name = "Desert Eagle",
      skinName = "Golden Hawk",
      category = WeaponCategory.PISTOL,
      baseDamage = 54,
      headshotDamage = 210,
      rateOfFire = 35,
      accuracy = 70,
      range = 40,
      magazineSize = 7,
      currentAmmo = 7,
      description = "The Clash Squad favorite sidearm. One tap headshot legend."
    ),
    Weapon(
      id = "katana_blade",
      name = "Katana",
      skinName = "Blood Moon Edge",
      category = WeaponCategory.MELEE,
      baseDamage = 65,
      headshotDamage = 130,
      rateOfFire = 40,
      accuracy = 80,
      range = 10,
      magazineSize = 1,
      currentAmmo = 1,
      description = "Legendary samurai blade. Deflects incoming bullets while running and cuts through armor."
    )
  )

  val defaultCharacters = listOf(
    Character(
      id = "alok",
      name = "DJ Alok",
      role = "Support & Rush",
      skillName = "Drop the Beat",
      skillDescription = "Creates a 5m rhythmic aura that increases movement speed by 15% and restores 5 HP/sec for 10 seconds.",
      cooldownSeconds = 45,
      level = 6,
      isEquipped = true
    ),
    Character(
      id = "chrono",
      name = "Chrono",
      role = "Defender",
      skillName = "Time Turner",
      skillDescription = "Creates an impenetrable hexagonal force field that blocks up to 800 damage from all enemy fire.",
      cooldownSeconds = 75,
      level = 5,
      isEquipped = false
    ),
    Character(
      id = "kelly",
      name = "Kelly",
      role = "Speedster",
      skillName = "Dash",
      skillDescription = "Passive ability increases sprinting velocity by 6%. Awakening enables first shot burst damage.",
      cooldownSeconds = 0,
      level = 6,
      isEquipped = false
    ),
    Character(
      id = "hayato",
      name = "Hayato",
      role = "Warrior",
      skillName = "Bushido",
      skillDescription = "For every 10% decrease in max HP, armor penetration increases by 10%. Lethal at low health.",
      cooldownSeconds = 0,
      level = 5,
      isEquipped = false
    ),
    Character(
      id = "wukong",
      name = "Wukong",
      role = "Infiltrator",
      skillName = "Camouflage",
      skillDescription = "Transforms into a camouflage bush for 15 seconds, evading enemy auto-aim. Resets on downing an enemy.",
      cooldownSeconds = 120,
      level = 5,
      isEquipped = false
    ),
    Character(
      id = "moco",
      name = "Moco",
      role = "Intel / Hacker",
      skillName = "Hacker's Eye",
      skillDescription = "Tags enemies hit by your bullets for 5 seconds. Tagged position is shared with your squad.",
      cooldownSeconds = 0,
      level = 6,
      isEquipped = false
    )
  )

  val defaultDropLocations = listOf(
    DropLocation(
      id = "clock_tower",
      name = "Clock Tower",
      zoneType = "Hot Drop",
      lootTier = "Tier 3 Ultra",
      dangerScore = 5,
      description = "The ultimate Free Fire battleground. High ground, underground tunnels, intense early shotgun fights.",
      xRatio = 0.58f,
      yRatio = 0.35f
    ),
    DropLocation(
      id = "peak",
      name = "Peak",
      zoneType = "Center Hot Drop",
      lootTier = "Tier 3 High",
      dangerScore = 5,
      description = "Highest point on Bermuda. Houses legendary crates, sniping spots, and guaranteed combat.",
      xRatio = 0.49f,
      yRatio = 0.52f
    ),
    DropLocation(
      id = "factory",
      name = "Factory",
      zoneType = "Roof Challenge",
      lootTier = "Tier 3 High",
      dangerScore = 4,
      description = "Famous for the 1v1 fist fight on the roof. Plentiful SMG and shotgun spawns inside warehouse.",
      xRatio = 0.22f,
      yRatio = 0.42f
    ),
    DropLocation(
      id = "bimasakti",
      name = "Bimasakti Strip",
      zoneType = "Bridge & Valley",
      lootTier = "Tier 2",
      dangerScore = 3,
      description = "Long open stretch flanked by hilltops. Great for mid-range AR combat and airdrop intercepts.",
      xRatio = 0.55f,
      yRatio = 0.22f
    ),
    DropLocation(
      id = "pochinok",
      name = "Pochinok",
      zoneType = "Urban Compounds",
      lootTier = "Tier 2",
      dangerScore = 3,
      description = "Dense cluster of houses with lots of level 2/3 vests, gloo walls, and quick medkit replenishment.",
      xRatio = 0.42f,
      yRatio = 0.68f
    ),
    DropLocation(
      id = "mill",
      name = "Mill",
      zoneType = "Highland Arsenal",
      lootTier = "Tier 3",
      dangerScore = 4,
      description = "Carved into mountain cliffside. Abundant sniper ammo, high-tier armor, and steep vantage points.",
      xRatio = 0.72f,
      yRatio = 0.46f
    )
  )

  // Player state
  private val _playerStats = MutableStateFlow(PlayerStats())
  val playerStats: StateFlow<PlayerStats> = _playerStats.asStateFlow()

  // Selected Weapon & Character
  private val _equippedWeapon = MutableStateFlow(defaultWeapons[0])
  val equippedWeapon: StateFlow<Weapon> = _equippedWeapon.asStateFlow()

  private val _equippedCharacter = MutableStateFlow(defaultCharacters[0])
  val equippedCharacter: StateFlow<Character> = _equippedCharacter.asStateFlow()

  private val _charactersList = MutableStateFlow(defaultCharacters)
  val charactersList: StateFlow<List<Character>> = _charactersList.asStateFlow()

  private val _weaponsList = MutableStateFlow(defaultWeapons)
  val weaponsList: StateFlow<List<Weapon>> = _weaponsList.asStateFlow()

  // Sensitivity
  private val _sensitivity = MutableStateFlow(SensitivityConfig())
  val sensitivity: StateFlow<SensitivityConfig> = _sensitivity.asStateFlow()

  fun equipWeapon(weapon: Weapon) {
    _equippedWeapon.value = weapon
  }

  fun equipCharacter(character: Character) {
    _equippedCharacter.value = character
    _charactersList.value = _charactersList.value.map {
      it.copy(isEquipped = (it.id == character.id))
    }
  }

  fun upgradeCharacter(characterId: String): Boolean {
    val currentGold = _playerStats.value.goldCoins
    val cost = 2500
    if (currentGold >= cost) {
      _playerStats.value = _playerStats.value.copy(goldCoins = currentGold - cost)
      _charactersList.value = _charactersList.value.map {
        if (it.id == characterId && it.level < 7) it.copy(level = it.level + 1) else it
      }
      return true
    }
    return false
  }

  fun updateSensitivity(newConfig: SensitivityConfig) {
    _sensitivity.value = newConfig
  }

  fun recordMatchResult(isBooyah: Boolean, kills: Int, headshots: Int) {
    val stats = _playerStats.value
    val newMatches = stats.totalMatches + 1
    val newBooyahs = if (isBooyah) stats.totalBooyahs + 1 else stats.totalBooyahs
    val newKills = stats.totalKills + kills
    val earnedGold = if (isBooyah) 850 + kills * 50 else 250 + kills * 30
    val earnedDiamonds = if (isBooyah) 15 else 2
    val starsDelta = if (isBooyah) 3 else if (kills >= 3) 1 else 0

    _playerStats.value = stats.copy(
      totalMatches = newMatches,
      totalBooyahs = newBooyahs,
      totalKills = newKills,
      goldCoins = stats.goldCoins + earnedGold,
      diamonds = stats.diamonds + earnedDiamonds,
      rankStars = stats.rankStars + starsDelta
    )
  }

  fun getRandomBotKillFeedItem(): KillFeedItem {
    val botNames = listOf(
      "★Skyler_Pro★", "Toxic_Ninja", "OP_RAHUL_99", "CRX_Vampire",
      "Garena_Sniper", "AWM_King_7", "Thunder_Bolt", "Legend_Rohan",
      "Poco_Killer", "Black_Panther_FF", "Viper_007", "Total_Gamer_Fan"
    )
    val weapons = listOf("MP40", "M1887", "AK47", "AWM", "Desert Eagle", "Groza")
    val killer = botNames.random()
    val victim = botNames.filter { it != killer }.random()
    val weapon = weapons.random()
    val isHeadshot = Random.nextBoolean()
    return KillFeedItem(killer, victim, weapon, isHeadshot)
  }
}
