package com.example.aliplayer.ui.adapter

import android.content.ContentUris
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aliplayer.R
import com.example.aliplayer.databinding.ItemAudioBinding
import com.example.aliplayer.model.Audio
import java.lang.Error

class AudioAdapter(var audios: List<Audio> = listOf(), val onRootClick:(audio:Audio) -> Unit) :
    RecyclerView.Adapter<AudioAdapter.ViewHolderAudio>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAudio {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_audio, parent, false)

        return ViewHolderAudio(view)
    }

    override fun onBindViewHolder(holder: ViewHolderAudio, position: Int) {
        val audio = audios[position]

        holder.binding.apply {
            txtTitle.text = audio.title
            txtArtistName.text = audio.artistName

            try {
                val contentUri = audio.id?.toLong()?.let {
                    ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        it
                    )
                }

                
            }catch (e:Error) {

            }

            holder.binding.root.setOnClickListener {
                onRootClick(audio)
            }
        }
    }

    override fun getItemCount() = audios.size

    class ViewHolderAudio(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemAudioBinding.bind(view)
    }

    fun addAll(_audios:List<Audio>) {
        audios = _audios
        notifyDataSetChanged()
    }
}
