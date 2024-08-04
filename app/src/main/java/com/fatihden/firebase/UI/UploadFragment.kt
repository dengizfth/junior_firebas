package com.fatihden.firebase.UI

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fatihden.firebase.R
import com.fatihden.firebase.databinding.FragmentUploadBinding
import com.google.android.material.snackbar.Snackbar


class UploadFragment : Fragment() {

    private var _binding:FragmentUploadBinding? = null
    private val binding get() = _binding!!

    // Yeni activity açmak için kullanıcaz ( Galeriyi açmak gibi )
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    // izinler için
    private lateinit var permissionLauncher:ActivityResultLauncher<String>
    // Açılan pencereden seçilen resmin adresi tutmak için
    var selectedPicture : Uri? = null
    // Secilen resmi Bitmap olarak tutmak için :
    var selectedBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        reginsterLauncher()
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
            if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                // İzin yok, izin iste :

                //Daha öncesinde izin istemiyi red etti mi ? kontrol edilmesi :
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_MEDIA_IMAGES)) {
                    //İzni red etmiş : Bunun için neden izin istediğimizi açıklayarak bir kere daha izin istenilmeli :
                    Snackbar.make(view , "Galeriden Fotoraf seçmek için vermeniz gerekiyor",Snackbar.LENGTH_INDEFINITE)
                        .setAction("İzin Ver" , View.OnClickListener {
                                // İzin İsteyeceğimiz alan :
                        }).show()
                } else {
                    // izin isteyeceğimiz alan :

                }

            } else {
                // izin var , telefonun galerisini aç :

            }


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

    // onCreate içersinden çağırmak gerekiyor :
    private fun reginsterLauncher() {
        // initialize
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    selectedPicture = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            // SDK 28 ve üstü
                            val source = ImageDecoder.createSource(
                                requireActivity().contentResolver,
                                selectedPicture!!
                            )
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(selectedBitmap)

                        } else {
                            // 27 ve altı için :
                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                requireActivity().contentResolver,
                                selectedPicture
                            )
                            binding.imageView.setImageBitmap(selectedBitmap)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}