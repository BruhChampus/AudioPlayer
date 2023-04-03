package com.example.mediaplayer

import com.example.mediaplayer.model.AudioFile

interface AudioClickListener {
    fun onClick(audio:AudioFile)
}