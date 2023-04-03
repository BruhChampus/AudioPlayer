package com.example.mediaplayer.activities

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.AudioClickListener
import com.example.mediaplayer.AudioFilesAdapter
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.ActivityMainBinding
import com.example.mediaplayer.databinding.FragmentMusicPlayerPanelBinding
import com.example.mediaplayer.fragments.MusicPlayerFragmentPanel
import com.example.mediaplayer.model.AudioFile
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), AudioClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item1 -> Toast.makeText(this, "SKDL", Toast.LENGTH_SHORT).show()
            }
            true
        }
        val audioList = ArrayList<AudioFile>()
        addSong(R.raw.health_major_crimes_cyberpunk_2077, audioList)
        addSong(R.raw.omori_underwater_prom_queens, audioList)
        setupListOfAudioIntoRecyclerView(audioList)
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
            val audio = AudioFile(title ?: resourceName, durationString ?: "00:00", author ?: "Various artists")

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
        return "$minutesInt:$minutesRemnant"
    }

    private fun setupListOfAudioIntoRecyclerView(
        audioList: ArrayList<AudioFile>
    ) {
        if (audioList.isNotEmpty()) {
            val audioAdapter = AudioFilesAdapter(
                audioList, this)
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
        val fragment = MusicPlayerFragmentPanel.newInstance("2","24")
       supportFragmentManager.beginTransaction().apply {
          replace(R.id.fl_bottom_sheet, fragment).commit()
       }
    }
}