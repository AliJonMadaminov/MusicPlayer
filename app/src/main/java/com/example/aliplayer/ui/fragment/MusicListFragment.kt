package com.example.aliplayer.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.aliplayer.R
import com.example.aliplayer.databinding.FragmentMusicListBinding
import com.example.aliplayer.model.Audio
import com.example.aliplayer.ui.adapter.AudioAdapter
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


//        val mainViewModel = MainViewModel.Companion.Factory().create(MainViewModel::class.java)
        val mainFactory = MainViewModel.Companion.Factory()
        val mainViewModel =
            ViewModelProvider(requireActivity(), mainFactory).get(MainViewModel::class.java)


        if (!permissionIsGranted()) {
            requestPermission()
        } else {
            val cursor = context?.contentResolver?.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                getProjection(),
                null,
                null,
                null
            )

            val adapter = AudioAdapter() {

                it.apply {

                    if (title != null && artistName != null && id != null) {

                        val directions = MusicListFragmentDirections.actionMusicListFragmentToMusicDetailsFragment(
                            title!!,
                            artistName!!,
                            duration,
                            id
                        )
                        findNavController().navigate(directions)

                    }
                }
            }

            mainViewModel.fetchAudios(cursor).observe(requireActivity(), Observer {
                adapter.addAll(it)
            })

            binding.recycler.adapter = adapter
        }


    }

    companion object {


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MusicListFragment().apply {

            }
    }


    private fun getProjection(): Array<String> {

        return arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.DURATION
        )

    }

    fun playMusic() {

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