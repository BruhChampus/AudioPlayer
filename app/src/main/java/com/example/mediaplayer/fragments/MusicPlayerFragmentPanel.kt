package com.example.mediaplayer.fragments

import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.core.content.ContextCompat.startForegroundService
import androidx.core.content.getSystemService
import com.example.mediaplayer.ControlPanelReceiver
import com.example.mediaplayer.MusicService
import com.example.mediaplayer.R
import com.example.mediaplayer.TestConstants
import com.example.mediaplayer.activities.MainActivity
import com.example.mediaplayer.databinding.FragmentMusicPlayerPanelBinding


private const val AUDIO_ID = TestConstants.AUDIO_ID
private const val BUTTON_ID = TestConstants.BUTTON_ID


class MusicPlayerFragmentPanel : Fragment() {

    private lateinit var binding: FragmentMusicPlayerPanelBinding
    private var buttonId: Int = R.drawable.ic_pause
    private var audioId: Int = 0
    private var maxSongs: Int = TestConstants.audioList.size - 1
    private lateinit var animation: Animation
    private lateinit var mediaStateBroadcastReceiver: ControlPanelReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            audioId = it.getInt(AUDIO_ID)
            Log.e("SONG", "audioId = $audioId")
            buttonId = it.getInt(BUTTON_ID)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicPlayerPanelBinding.inflate(layoutInflater)
        init()


        return binding.root
    }


    private fun init() {
        binding.tvControlPanelSongTitle.text = TestConstants.audioList[audioId].name
        binding.tvControlPanelSongAuthor.text = TestConstants.audioList[audioId].author
        binding.tvControlPanelSongDuration.text = TestConstants.audioList[audioId].durationString
        binding.ivControlPanelSongCover.setImageResource(TestConstants.audioList[audioId].background)

        animation = AnimationUtils.loadAnimation(requireContext(), R.anim.text_rolling)
        binding.tvControlPanelSongTitle.startAnimation(animation)

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
                    binding.tvControlPanelSongTitle.startAnimation(animation)
                    binding.ibPlay.visibility = View.GONE
                    binding.ibPause.visibility = View.VISIBLE
                }

                override fun onPauseClicked() {
                    binding.ibPlay.visibility = View.VISIBLE
                    binding.ibPause.visibility = View.GONE
                    animation.cancel()
                }

                override fun onNextClicked() {
                    Toast.makeText(requireContext(), "Next", Toast.LENGTH_SHORT).show()

                }

                override fun onPreviousClicked() {
                    Toast.makeText(requireContext(), "Previous", Toast.LENGTH_SHORT).show()
                    binding.sbControlPanelSeekBar.progress = 23

                }

                override fun onRewindClicked(audioProgress: Int, audioPassed: Int) {
                  binding.sbControlPanelSeekBar.progress = audioProgress
                 }

            })

        val intentFilter = IntentFilter().apply {
            addAction(MusicService.ACTION_PLAY)
            addAction(MusicService.ACTION_PAUSE)
            addAction(MusicService.ACTION_NEXT)
            addAction(MusicService.ACTION_PREVIOUS)
            addAction(MusicService.ACTION_REWIND)
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
        super.onDestroy()
        binding.root.clearAnimation()
        activity?.unregisterReceiver(mediaStateBroadcastReceiver)
    }


    companion object {
        @JvmStatic
        fun newInstance(audioId: Int, isPlaying: Boolean) =
            MusicPlayerFragmentPanel().apply {
                arguments = Bundle().apply {
                    putInt(AUDIO_ID, audioId)
                    putInt(BUTTON_ID, if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
                }
            }
    }
}