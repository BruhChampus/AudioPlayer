package com.example.mediaplayer

import com.example.mediaplayer.model.AudioFile

object TestConstants {
    val audioList = ArrayList<AudioFile>()
    const val AUDIO_ID = "audio_id"
    const val BUTTON_ID = "button_id"
    const val TIME_PASSED = "time_passed"
    const val AUDIO_PROGRESS = "audio_progress"

    private fun calculateMinutes(duration: Int): String {
        val durationMinutes = duration.toFloat() / 60_000
        val minutesInt = durationMinutes.toInt()
        val minutesRemnant = ((durationMinutes - minutesInt) * 60).toInt()
        return String.format("%02d:%02d", minutesInt, minutesRemnant)
    }
}