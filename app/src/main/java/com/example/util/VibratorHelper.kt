package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object VibratorHelper {
  private fun getVibrator(context: Context): Vibrator? {
    return try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
      } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
      }
    } catch (_: Exception) {
      null
    }
  }

  fun vibrateShot(context: Context, isHeadshot: Boolean) {
    try {
      val vibrator = getVibrator(context) ?: return
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val duration = if (isHeadshot) 80L else 35L
        val amplitude = if (isHeadshot) VibrationEffect.DEFAULT_AMPLITUDE else 120
        vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(if (isHeadshot) 80L else 35L)
      }
    } catch (_: Exception) {}
  }

  fun vibrateGlooWall(context: Context) {
    try {
      val vibrator = getVibrator(context) ?: return
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 40, 30, 60), -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(80L)
      }
    } catch (_: Exception) {}
  }

  fun vibrateBooyah(context: Context) {
    try {
      val vibrator = getVibrator(context) ?: return
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 80, 120, 80, 250), -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(400L)
      }
    } catch (_: Exception) {}
  }
}
