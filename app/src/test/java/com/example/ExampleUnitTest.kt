package com.example

import com.example.data.GameStateRepository
import com.example.data.model.SensitivityConfig
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testWeaponEquip() {
    val weapons = GameStateRepository.defaultWeapons
    assertTrue(weapons.isNotEmpty())

    val mp40 = weapons.first { it.id == "mp40_cobra" }
    GameStateRepository.equipWeapon(mp40)
    assertEquals("MP40", GameStateRepository.equippedWeapon.value.name)
  }

  @Test
  fun testCharacterEquipAndUpgrade() {
    val characters = GameStateRepository.defaultCharacters
    assertTrue(characters.isNotEmpty())

    val alok = characters.first { it.id == "alok" }
    GameStateRepository.equipCharacter(alok)
    assertEquals("DJ Alok", GameStateRepository.equippedCharacter.value.name)

    val upgraded = GameStateRepository.upgradeCharacter("alok")
    assertTrue(upgraded)
  }

  @Test
  fun testSensitivityConfig() {
    val newConfig = SensitivityConfig(general = 98, redDot = 92)
    GameStateRepository.updateSensitivity(newConfig)
    assertEquals(98, GameStateRepository.sensitivity.value.general)
    assertEquals(92, GameStateRepository.sensitivity.value.redDot)
  }
}
