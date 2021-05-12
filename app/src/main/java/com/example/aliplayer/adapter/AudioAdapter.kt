package com.example.aliplayer.adapter

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aliplayer.R
import com.example.aliplayer.databinding.ItemAudioBinding
import com.example.aliplayer.model.Audio

class AudioAdapter(
    private var audios: List<Audio> = listOf(),
    val onRootClick: (audio: Audio) -> Unit
) :
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

            if (audio.coverPath != null) {
//                val image = getAlbumToAdapter(Uri.parse(audio.coverPath))
                if (true) {

                    Glide.with(imageView.context)
                        .load(Uri.parse(audio.coverPath))
                        .placeholder(R.drawable.audio_icon)
                        .into(imageView)

                }
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

    fun addAll(_audios: List<Audio>) {
        audios = _audios
        notifyDataSetChanged()
    }

    private fun getAlbumToAdapter(uri: Uri): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }

}
