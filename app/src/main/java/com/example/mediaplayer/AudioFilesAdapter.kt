package com.example.mediaplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.databinding.ActivityMainBinding
import com.example.mediaplayer.databinding.FragmentMusicPlayerPanelBinding
import com.example.mediaplayer.databinding.SongItemBinding
import com.example.mediaplayer.model.AudioFile

class AudioFilesAdapter(private val audioList: ArrayList<AudioFile>,  private val audioClickListener: AudioClickListener) :
    RecyclerView.Adapter<AudioFilesAdapter.AudioFilesViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AudioFilesAdapter.AudioFilesViewHolder {
val from =LayoutInflater.from(parent.context)
        val binding = SongItemBinding.inflate(from, parent, false)
        return AudioFilesViewHolder(binding, audioClickListener)
    }

    override fun onBindViewHolder(holder: AudioFilesAdapter.AudioFilesViewHolder, position: Int) {
        holder.bindAudio(audioList[position])
    }

    override fun getItemCount(): Int {
        return audioList.size
    }

    inner class AudioFilesViewHolder(private val binding: SongItemBinding, private val audioClickListener: AudioClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindAudio(audio: AudioFile) {
            binding.tvSongTitle.text = audio.name
            binding.tvSongAuthor.text = audio.author
            binding.tvSongDuration.text = audio.duration
            binding.clSongItem.setOnClickListener { audioClickListener.onClick(audio) }
        }
    }
}