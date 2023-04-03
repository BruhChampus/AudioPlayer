package com.example.mediaplayer.fragments

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.example.mediaplayer.R
import com.example.mediaplayer.TestConstants
import com.example.mediaplayer.activities.MainActivity
import com.example.mediaplayer.databinding.FragmentMusicPlayerPanelBinding


private const val AUDIO_ID = "audio_id"


class MusicPlayerFragmentPanel : Fragment() {

    private lateinit var binding: FragmentMusicPlayerPanelBinding
    private var audioId: Int = 0
    private lateinit var mediaPlayer:MediaPlayer


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

        binding = FragmentMusicPlayerPanelBinding.inflate(layoutInflater)
        binding.tvControlPanelSongTitle.text = TestConstants.audioList[audioId].name
        binding.tvControlPanelSongAuthor.text = TestConstants.audioList[audioId].author
        binding.tvControlPanelSongDuration.text = TestConstants.audioList[audioId].duration
        // Inflate the layout for this fragment

        mediaPlayer = MediaPlayer.create(requireContext(), TestConstants.audioList[audioId].resId)
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.text_rolling)

        mediaPlayer.start()
        binding.ibPlay.visibility = View.GONE
        binding.ibPause.visibility = View.VISIBLE
        binding.tvControlPanelSongTitle.startAnimation(animation)

        binding.ibPlay.setOnClickListener {
            mediaPlayer.start()
            binding.ibPlay.visibility = View.GONE
            binding.ibPause.visibility = View.VISIBLE
            binding.tvControlPanelSongTitle.startAnimation(animation)
        }
        binding.ibPause.setOnClickListener {
            mediaPlayer.pause()
            animation.cancel()
            binding.ibPlay.visibility = View.VISIBLE
            binding.ibPause.visibility = View.GONE
        }

        return binding.root
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }


    companion object {

        @JvmStatic
        fun newInstance(audioId: Int) =
            MusicPlayerFragmentPanel().apply {
                arguments = Bundle().apply {
                    putInt(AUDIO_ID, audioId)
                }
            }
    }
}