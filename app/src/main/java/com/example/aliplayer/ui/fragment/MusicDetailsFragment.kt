package com.example.aliplayer.ui.fragment

import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.aliplayer.R
import com.example.aliplayer.databinding.FragmentMusicDetailsBinding
import com.example.aliplayer.model.Audio
import com.example.aliplayer.service.AudioService
import com.example.aliplayer.viewmodel.MainViewModel
import com.example.aliplayer.viewmodel.MusicDetailsViewModel
import java.io.File
import java.net.URI


class MusicDetailsFragment : Fragment() {

    lateinit var audio: Audio
    lateinit var binding: FragmentMusicDetailsBinding
    lateinit var viewmodel:MusicDetailsViewModel
    var audioService: AudioService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = MusicDetailsFragmentArgs.fromBundle(requireArguments())

        args.apply {
            audio = Audio(title, artistName, duration, coverPath, false, id)
        }

        val intent = Intent(requireActivity(), AudioService::class.java)
        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

    }

    val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
            val binder = (iBinder as AudioService.AudioBinder)
            audioService = binder.getInstance()
            audioService?.playAudio(audio)
            audioService?.isAudioCompletedLive?.observe(this@MusicDetailsFragment, Observer {
                audioService?.playNext()
                initUI()
            })

            binding.seekBar.progress = 0
            binding.seekBar.max = audio.duration
            audioService?.currentPosition?.observe(this@MusicDetailsFragment, Observer {
                binding.seekBar.progress = it
            })

            audioService?.isPlayingFromNotification?.observe(this@MusicDetailsFragment, Observer {
                if (it) {
                    binding.imgPlayOrStop.setImageResource(R.drawable.ic_baseline_pause_circle_24)
                } else {
                    binding.imgPlayOrStop.setImageResource(R.drawable.ic_baseline_play_circle_24)
                }
            })

            audioService?.isNextPressedNotification?.observe(
                viewLifecycleOwner,
                Observer { isNextPressed ->
                    initUI()
                })

        }

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

        binding.seekBar.progress = 0
        binding.seekBar.max = audio.duration
        binding.txtTitle.text = audio.title
        binding.txtArtist.text = audio.artistName
        binding.imgRepeat.setOnClickListener {
            audioService?.setLooping(true)
        }
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

        setOnSeekBarChangeListener()

        binding.imgPlayOrStop.setOnClickListener {
            onPlayOrStop()
        }


        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }



        binding.imgPrevious.setOnClickListener {
            audioService?.playPrevious()
            initUI()
        }

        binding.imgNext.setOnClickListener {
            audioService?.playNext()
            initUI()
        }

    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MusicDetailsFragment().apply {

            }
    }

    fun onPlayOrStop() {
        if (audioService?.playAndPostCurrentPositionOrStop()!!) {
            binding.imgPlayOrStop.setImageResource(R.drawable.ic_baseline_pause_circle_24)
        } else {
            binding.imgPlayOrStop.setImageResource(R.drawable.ic_baseline_play_circle_24)
        }
    }

    fun setOnSeekBarChangeListener() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.progress?.let { audioService?.seekToPostion(it) }
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
//        mainViewModel.audioToPlayLive.observe(viewLifecycleOwner, Observer {
//            mainViewModel.shouldStopLive.value = false
//
//            audio = it

//        })
    }

    fun initUI() {
        audioService?.currentAudio?.apply {
            binding.txtTitle.text = this.title
            binding.txtArtist.text = this.artistName
            binding.seekBar.progress = 0
            binding.seekBar.max = this.duration
            binding.imgPlayOrStop.setImageResource(R.drawable.ic_baseline_pause_circle_24)
        }
    }

    override fun onDestroy() {
        requireActivity().unbindService(serviceConnection)
        super.onDestroy()
    }


}