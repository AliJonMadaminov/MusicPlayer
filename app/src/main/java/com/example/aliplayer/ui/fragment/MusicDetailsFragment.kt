package com.example.aliplayer.ui.fragment

import android.content.ContentUris
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.aliplayer.R
import com.example.aliplayer.databinding.FragmentMusicDetailsBinding
import com.example.aliplayer.model.Audio
import com.example.aliplayer.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import java.lang.Exception
import java.lang.Thread.sleep


class MusicDetailsFragment : Fragment() {
//TODO remove redundant audio
    lateinit var audio: Audio
    lateinit var binding: FragmentMusicDetailsBinding
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = MusicDetailsFragmentArgs.fromBundle(requireArguments())


        args.apply {
            audio = Audio(title, artistName, duration, id)

        }


        mainViewModel = createMainViewModel()
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

//        binding.txtTitle.text = audio.title
//        binding.txtArtist.text = audio.artistName
        observeAudioChanged()

        val contentUri = getContentUri()

        //TODO move to observeAudioChanged
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

        setOnSeekBarChangeListenerAndDuration()

        binding.imgPlayOrStop.setOnClickListener {
            onPlayOrStop()
        }

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        mainViewModel.audioCurrentPosition.observe(viewLifecycleOwner, Observer {
            binding.seekBar.progress = it
        })




        binding.imgPrevious.setOnClickListener {
            val audios = mainViewModel.getAudios().value
            val index= audios?.indexOf(audio)
            if (index == 0) {
                mainViewModel.audioToPlayLive.value = audios[audios.size-1]
            }else {
                if (index != null) {
                    mainViewModel.audioToPlayLive.value = audios[index - 1]
                }
            }
        }

        binding.imgNext.setOnClickListener {
            val audios = mainViewModel.getAudios().value
            val index= audios?.indexOf(audio)
            if (index == audios?.size?.minus(1)) {
                mainViewModel.audioToPlayLive.value = audios?.get(0)
            }else {
                if (index != null) {
                    mainViewModel.audioToPlayLive.value = audios[index + 1]
                }
            }
        }

    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MusicDetailsFragment().apply {

            }
    }

    fun onPlayOrStop() {
        mainViewModel.shouldStopLive.apply {
            if (this.value == null) {
                this.value = true
                binding.imgPlayOrStop.setImageResource(R.drawable.ic_baseline_play_circle_24)
                return
            }

            mainViewModel.shouldStopLive.value?.let {
                if (!it) {
                    this.value = true
                    binding.imgPlayOrStop.setImageResource(R.drawable.ic_baseline_play_circle_24)
                } else {
                    this.value = false
                    binding.imgPlayOrStop.setImageResource(R.drawable.ic_baseline_pause_circle_24)
                }
            }
        }
    }

    fun setOnSeekBarChangeListenerAndDuration() {
//        binding.seekBar.max = audio.duration
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mainViewModel.seektoLive.value = seekBar?.progress
            }
        })
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

    fun observeAudioChanged() {
        mainViewModel.audioToPlayLive.observe(viewLifecycleOwner, Observer {
            mainViewModel.shouldStopLive.value = false

            audio = it
            binding.txtTitle.text = it.title
            binding.txtArtist.text = it.artistName
            binding.seekBar.progress = 0
            binding.seekBar.max = it.duration
            binding.imgPlayOrStop.setImageResource(R.drawable.ic_baseline_pause_circle_24)
        })
    }
}