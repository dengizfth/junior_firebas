package com.fatihden.firebase.UI

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fatihden.firebase.R
import com.fatihden.firebase.databinding.FragmentUploadBinding


class UploadFragment : Fragment() {

    private var _binding:FragmentUploadBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUploadBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Resim seçme için api 33 altı , 33 ve üstü için ayrı ayrı kontroller:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ){
            // sdk 33 ve yukarısı için -> read media images izni :


        } else {
            // sdk 33'ün altındaki eski versionlar için -> read external storage :

        }


        binding.uploadBtn.setOnClickListener {


        }


    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}