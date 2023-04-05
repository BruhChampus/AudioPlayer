package com.example.mediaplayer.fragments

import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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


class MusicPlayerFragmentPanel : Fragment() {

    private lateinit var binding: FragmentMusicPlayerPanelBinding
    private var audioId: Int = 0
    private lateinit var animation: Animation
    private lateinit var  mediaStateBroadcastReceiver: ControlPanelReceiver
    private lateinit var serviceIntent: Intent


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
        init()
        bindControlBtnByMusicState()

        return binding.root
    }



    private fun init(){
        binding.tvControlPanelSongTitle.text = TestConstants.audioList[audioId].name
        binding.tvControlPanelSongAuthor.text = TestConstants.audioList[audioId].author
        binding.tvControlPanelSongDuration.text = TestConstants.audioList[audioId].duration

        animation = AnimationUtils.loadAnimation(requireContext(), R.anim.text_rolling)
        serviceIntent = Intent(requireContext(), MusicService::class.java)



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
            })
        val intentFilter = IntentFilter().apply {
            addAction(MusicService.ACTION_PLAY)
            addAction(MusicService.ACTION_PAUSE)
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

    }


    private fun bindControlBtnByMusicState() {
        val musicService = MusicService()
        val isPlaying = musicService.isMusicPlaying()

        if(isPlaying)
        {
            binding.ibPlay.visibility = View.GONE
            binding.ibPause.visibility = View.VISIBLE
        }
        else{
            binding.ibPlay.visibility = View.VISIBLE
            binding.ibPause.visibility = View.GONE
        }
    }




    override fun onDestroy() {
        binding.root.clearAnimation()
        activity?.unregisterReceiver(mediaStateBroadcastReceiver)
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