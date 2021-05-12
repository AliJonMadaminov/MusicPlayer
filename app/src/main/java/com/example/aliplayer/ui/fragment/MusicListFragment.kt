package com.example.aliplayer.ui.fragment

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.aliplayer.databinding.FragmentMusicListBinding
import com.example.aliplayer.model.Audio
import com.example.aliplayer.service.AudioService
import com.example.aliplayer.adapter.AudioAdapter
import com.example.aliplayer.viewmodel.MainViewModel


class MusicListFragment : Fragment() {

    var audios: MutableList<Audio> = mutableListOf()
    lateinit var binding: FragmentMusicListBinding
    val REQUEST_PERMISSION_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMusicListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val mainFactory = MainViewModel.Companion.Factory()
        val mainViewModel =
            ViewModelProvider(requireActivity(), mainFactory).get(MainViewModel::class.java)


        if (!permissionIsGranted()) {
            requestPermission()
        } else {

            audios = mainViewModel.getAudios()

            val adapter = AudioAdapter(audios) {

                it.apply {
                    val intent = Intent(requireActivity(), AudioService::class.java)
                    
                    requireActivity().bindService(intent, object : ServiceConnection {
                        override fun onServiceDisconnected(name: ComponentName?) {

                        }

                        override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
                            val service = (iBinder as AudioService.AudioBinder).getInstance()
                            service.audioList = audios
                        }

                    }, Context.BIND_AUTO_CREATE)
                    if (title != null && artistName != null && id != null) {

                        val directions =
                            coverPath?.let { it1 ->
                                MusicListFragmentDirections.actionMusicListFragmentToMusicDetailsFragment(
                                    title!!,
                                    artistName!!,
                                    duration,
                                    it1,
                                    id
                                )
                            }
                        if (directions != null) {
                            findNavController().navigate(directions)
                        }

                    }
                }

            }
            binding.recycler.adapter = adapter

        }


    }

    fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    fun permissionIsGranted(): Boolean {
        val isPermissionGranted =
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

        return isPermissionGranted == PackageManager.PERMISSION_GRANTED
    }

}