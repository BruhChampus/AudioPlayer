package com.example.mediaplayer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mediaplayer.R
import com.example.mediaplayer.TestConstants
import com.example.mediaplayer.databinding.FragmentMusicPlayerFullScreenBinding
import com.example.mediaplayer.databinding.FragmentMusicPlayerPanelBinding

private const val AUDIO_ID = TestConstants.AUDIO_ID


class MusicPlayerFragmentFullScreen : Fragment() {

    private lateinit var binding: FragmentMusicPlayerFullScreenBinding
    private var audioId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            audioId = it.getInt(AUDIO_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicPlayerFullScreenBinding.inflate(layoutInflater)

        binding.tvDetailsSongTitle.text = TestConstants.audioList[audioId].name
        binding.tvDetailsSongAuthor.text = TestConstants.audioList[audioId].author
        binding.tvDetailsSongDuration.text = TestConstants.audioList[audioId].duration

        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MusicPlayerFragmentFullScreen().apply {
                arguments = Bundle().apply {
                    putInt(AUDIO_ID, audioId)
                }
            }
    }
}