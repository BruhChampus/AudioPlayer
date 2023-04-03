package com.example.mediaplayer.fragments

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentMusicPlayerPanelBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MusicPlayerFragmentPanel : Fragment() {

    private lateinit var binding:FragmentMusicPlayerPanelBinding
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMusicPlayerPanelBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

//        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.health_major_crimes_cyberpunk2077)
//        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.text_rolling)
//
//        binding.ibPlay.setOnClickListener {
//            mediaPlayer.start()
//            binding.ibPlay.visibility = View.GONE
//            binding.ibPause.visibility = View.VISIBLE
//            binding.tvControlPanelSongTitle.startAnimation(animation)
//        }
//        binding.ibPause.setOnClickListener {
//            mediaPlayer.pause()
//            binding.ibPlay.visibility = View.VISIBLE
//            binding.ibPause.visibility = View.GONE
//        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MusicPlayerFragmentPanel().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}