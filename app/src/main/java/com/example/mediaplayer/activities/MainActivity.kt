package com.example.mediaplayer.activities

import android.app.ActivityManager
import android.app.PendingIntent.getService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.*
import com.example.mediaplayer.databinding.ActivityMainBinding
import com.example.mediaplayer.fragments.MusicPlayerFragmentFullScreen
import com.example.mediaplayer.fragments.MusicPlayerFragmentPanel
import com.example.mediaplayer.model.AudioFile
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), AudioClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    val audioList = TestConstants.audioList
    var currentAudio: Int = 0
    private lateinit var mediaStateBroadcastReceiver: ControlPanelReceiver
    private lateinit var serviceIntent: Intent
    var isPlaying = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToggle()

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item1 -> Toast.makeText(this, "Item 1 Jokerge", Toast.LENGTH_SHORT).show()
            }
            true
        }
        serviceIntent = Intent(this@MainActivity, MusicService::class.java)

        addSong(R.raw.health_major_crimes_cyberpunk_2077, audioList)
        addSong(R.raw.gorillaz_cracker_island_ft_thundercat, audioList)
        addSong(R.raw.omori_underwater_prom_queens, audioList)
        setupListOfAudioIntoRecyclerView(audioList)
        initializeBottomSheet()
        initializeBroadcastAndRegister()

    }


    private fun initializeBroadcastAndRegister() {
        mediaStateBroadcastReceiver =
            ControlPanelReceiver(object : ControlPanelReceiver.MediaStateListener {
                override fun onPlayClicked() {
                    serviceIntent.action = MusicService.ACTION_PLAY
                    startService(serviceIntent)
                    isPlaying = true
                 }

                override fun onPauseClicked() {
                    serviceIntent.action = MusicService.ACTION_PAUSE
                    startService(serviceIntent)
                    isPlaying = false
                }

                override fun onNextClicked() {
                    if (currentAudio != audioList.size-1) {
                        currentAudio++
                        isPlaying = true
                        serviceIntent.putExtra(TestConstants.AUDIO_ID, currentAudio)


                        when (supportFragmentManager.findFragmentById(R.id.fl_bottom_sheet)) {
                            is MusicPlayerFragmentPanel -> {
                                val fragment =
                                    MusicPlayerFragmentPanel.newInstance(currentAudio, isPlaying)
                                supportFragmentManager.beginTransaction().apply {
                                    replace(R.id.fl_bottom_sheet, fragment).commitAllowingStateLoss()
                                }
                            }
                            is MusicPlayerFragmentFullScreen -> {
                                val fragment =
                                    MusicPlayerFragmentFullScreen.newInstance(currentAudio, isPlaying)
                                supportFragmentManager.beginTransaction().apply {
                                    replace(R.id.fl_bottom_sheet, fragment).commitAllowingStateLoss()
                                }
                            }
                        }



                        serviceIntent.action = MusicService.ACTION_NEXT
                        startService(serviceIntent)
                    }
                }

                override fun onPreviousClicked() {
                    if (currentAudio != 0) {
                        currentAudio--
                        isPlaying = true
                        serviceIntent.putExtra(TestConstants.AUDIO_ID, currentAudio)

                        when (supportFragmentManager.findFragmentById(R.id.fl_bottom_sheet)) {
                            is MusicPlayerFragmentPanel -> {
                                val fragment =
                                    MusicPlayerFragmentPanel.newInstance(currentAudio, isPlaying)
                                supportFragmentManager.beginTransaction().apply {
                                    replace(R.id.fl_bottom_sheet, fragment).commitAllowingStateLoss()
                                }
                            }
                            is MusicPlayerFragmentFullScreen -> {
                                val fragment =
                                    MusicPlayerFragmentFullScreen.newInstance(currentAudio, isPlaying)
                                supportFragmentManager.beginTransaction().apply {
                                    replace(R.id.fl_bottom_sheet, fragment).commitAllowingStateLoss()
                                }
                            }
                        }

                        serviceIntent.action = MusicService.ACTION_PREVIOUS
                        startService(serviceIntent)
                    }
                }
            })
        val intentFilter = IntentFilter().apply {
            addAction(MusicService.ACTION_PLAY)
            addAction(MusicService.ACTION_PAUSE)
            addAction(MusicService.ACTION_NEXT)
            addAction(MusicService.ACTION_PREVIOUS)
        }
        registerReceiver(mediaStateBroadcastReceiver, intentFilter)

    }

    private fun initializeToggle() {
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    private fun initializeBottomSheet() {
        val bottomSheet = binding.flBottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.apply {
            peekHeight = 400
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    val fragment = MusicPlayerFragmentFullScreen.newInstance(currentAudio, isPlaying)
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_bottom_sheet, fragment).commit()
                    }

                }
            }


            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset == 0f) {
                    val fragment =
                        MusicPlayerFragmentPanel.newInstance(currentAudio, isPlaying)
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fl_bottom_sheet, fragment).commit()
                    }

                    Log.e("isMusicPlay", "$isPlaying")

                }
            }
        })
    }


    private fun addSong(resId: Int, audioList: ArrayList<AudioFile>): Boolean {
        if (!(resources.getResourceEntryName(resId).endsWith(".mp3"))) {
            val uri = Uri.parse("android.resource://$packageName/$resId")
            val meta = MediaMetadataRetriever()
            meta.setDataSource(
                applicationContext, uri
            )
            val title = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val author = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR)
            val duration = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)


            val durationString = duration?.let { calculateMinutes(it.toInt()) }
            val resourceName = resources.getResourceEntryName(resId)
            val audio = AudioFile(
                title ?: resourceName,
                durationString ?: "00:00",
                author ?: "Various artists",
                resId
            )

            audioList.add(audio)
        } else {
            return false
        }
        return true
    }


    private fun calculateMinutes(duration: Int): String {
        val durationMinutes = duration.toFloat() / 60_000
        val minutesInt = durationMinutes.toInt()
        val minutesRemnant = ((durationMinutes - minutesInt) * 60).toInt()
        return String.format("%02d:%02d", minutesInt, minutesRemnant)
    }


    private fun setupListOfAudioIntoRecyclerView(
        audioList: ArrayList<AudioFile>
    ) {
        if (audioList.isNotEmpty()) {
            val audioAdapter = AudioFilesAdapter(
                audioList, this
            )
            binding.rvAudioList.layoutManager = LinearLayoutManager(this)
            binding.rvAudioList.adapter = audioAdapter
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        when (item.itemId) {
            R.id.mi_do_something -> Snackbar.make(binding.drawerLayout, "Bruh", 4).show()

        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_screen_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onClick(audio: AudioFile) {
        isPlaying = true
        currentAudio = audioList.indexOf(audio)
        val fragment = MusicPlayerFragmentPanel.newInstance(audioList.indexOf(audio), isPlaying)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_bottom_sheet, fragment).commit()
        }

        serviceIntent.action = MusicService.ACTION_PLAY
        serviceIntent.putExtra(TestConstants.AUDIO_ID, currentAudio)

        startService(serviceIntent)

    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mediaStateBroadcastReceiver)
    }
}