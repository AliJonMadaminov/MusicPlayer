package com.example.aliplayer.ui.fragment

import android.content.ContentUris
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.aliplayer.R
import com.example.aliplayer.databinding.FragmentMusicDetailsBinding
import com.example.aliplayer.model.Audio
import com.example.aliplayer.viewmodel.MainViewModel
import java.lang.Exception


class MusicDetailsFragment : Fragment() {

    lateinit var audio: Audio
    lateinit var binding: FragmentMusicDetailsBinding
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = MusicDetailsFragmentArgs.fromBundle(requireArguments())



        args.apply {
            audio = Audio(title, artistName, duration, id)

        }

        val contentUri = getContentUri()
        val mainViewModel = createMainViewModel()
        mainViewModel.audioToPlayLive.value = audio

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtTitle.text = audio.title
        binding.txtArtist.text = audio.artistName

        val contentUri = audio.id?.toLong()?.let {
            ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                it
            )
        }

        if (contentUri != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    val cover =
                        context?.contentResolver?.loadThumbnail(contentUri, Size(300, 300), null)

                    Glide.with(requireContext()).asBitmap().load(cover).into(binding.imgCover)
                } catch (e: Exception) {

                }
            } else {

            }
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MusicDetailsFragment().apply {

            }
    }


    fun getContentUri(): Uri? {
        return audio.id?.toLong()?.let {
            ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                it
            )
        }
    }

    fun createMainViewModel(): MainViewModel {
        val mainFactory = MainViewModel.Companion.Factory()
        return ViewModelProvider(requireActivity(), mainFactory).get(MainViewModel::class.java)
    }
}