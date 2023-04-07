package com.example.mediaplayer.fragments

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Toast
import com.example.mediaplayer.ControlPanelReceiver
import com.example.mediaplayer.MusicService
import com.example.mediaplayer.R
import com.example.mediaplayer.TestConstants
import com.example.mediaplayer.databinding.FragmentMusicPlayerFullScreenBinding
import com.example.mediaplayer.databinding.FragmentMusicPlayerPanelBinding

private const val AUDIO_ID = TestConstants.AUDIO_ID
private const val BUTTON_ID = TestConstants.BUTTON_ID


class MusicPlayerFragmentFullScreen : Fragment() {

    private lateinit var binding: FragmentMusicPlayerFullScreenBinding
    private var buttonId: Int = R.drawable.ic_pause
    private var audioId: Int = 0
    private var maxSongs: Int = TestConstants.audioList.size - 1
    private lateinit var animation: Animation
    private lateinit var mediaStateBroadcastReceiver: ControlPanelReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            audioId = it.getInt(AUDIO_ID)
            buttonId = it.getInt(BUTTON_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicPlayerFullScreenBinding.inflate(layoutInflater)

        init()



        return binding.root
    }

    private fun init() {
        binding.tvDetailsSongTitle.text = TestConstants.audioList[audioId].name
        binding.tvDetailsSongAuthor.text = TestConstants.audioList[audioId].author
        binding.tvDetailsSongDuration.text = TestConstants.audioList[audioId].duration

        if (buttonId == R.drawable.ic_pause) binding.ibPause.visibility = View.VISIBLE
        else binding.ibPlay.visibility = View.VISIBLE

        if (audioId == maxSongs && audioId != 0) {
            binding.ibPreviousSkip.visibility = View.VISIBLE
        } else if (audioId == 0 && audioId != maxSongs) {
            binding.ibNextSkip.visibility = View.VISIBLE
        } else {
            binding.ibPreviousSkip.visibility = View.VISIBLE
            binding.ibNextSkip.visibility = View.VISIBLE
        }

        mediaStateBroadcastReceiver =
            ControlPanelReceiver(object : ControlPanelReceiver.MediaStateListener {
                override fun onPlayClicked() {
                    binding.ibPlay.visibility = View.GONE
                    binding.ibPause.visibility = View.VISIBLE
                }

                override fun onPauseClicked() {
                    binding.ibPlay.visibility = View.VISIBLE
                    binding.ibPause.visibility = View.GONE
                }

                override fun onNextClicked() {
                    Toast.makeText(requireContext(), "NExt", Toast.LENGTH_SHORT).show()

                }

                override fun onPreviousClicked() {
                    Toast.makeText(requireContext(), "Prev", Toast.LENGTH_SHORT).show()
                }
            })
        val intentFilter = IntentFilter().apply {
            addAction(MusicService.ACTION_PLAY)
            addAction(MusicService.ACTION_PAUSE)
            addAction(MusicService.ACTION_NEXT)
            addAction(MusicService.ACTION_PREVIOUS)
        }
        activity?.registerReceiver(mediaStateBroadcastReceiver, intentFilter)


        binding.ibPlay.setOnClickListener {
            val playIntent = Intent(MusicService.ACTION_PLAY)
            activity?.sendBroadcast(playIntent)
        }
        binding.ibPause.setOnClickListener {
            val pauseIntent = Intent(MusicService.ACTION_PAUSE)
            activity?.sendBroadcast(pauseIntent)
        }
        binding.ibPreviousSkip.setOnClickListener {
            val skipPreviousIntent = Intent(MusicService.ACTION_PREVIOUS)
            activity?.sendBroadcast(skipPreviousIntent)
        }
        binding.ibNextSkip.setOnClickListener {
            val skipNextIntent = Intent(MusicService.ACTION_NEXT)
            activity?.sendBroadcast(skipNextIntent)
        }

    }

    override fun onDestroy() {
        activity?.unregisterReceiver(mediaStateBroadcastReceiver)
        super.onDestroy()
    }


    companion object {

        @JvmStatic
        fun newInstance(audioId: Int, isPlaying: Boolean) =
            MusicPlayerFragmentFullScreen().apply {
                arguments = Bundle().apply {
                    putInt(AUDIO_ID, audioId)
                    putInt(BUTTON_ID, if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
                }
            }
    }
}