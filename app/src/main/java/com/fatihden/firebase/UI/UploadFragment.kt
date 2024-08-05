package com.fatihden.firebase.UI

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.fatihden.firebase.databinding.FragmentUploadBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.UUID


class UploadFragment : Fragment() {

    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!

    // Yeni activity açmak için kullanıcaz ( Galeriyi açmak gibi )
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    // izinler için
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    // Açılan pencereden seçilen resmin adresi tutmak için
    var selectedPicture: Uri? = null

    // Secilen resmi Bitmap olarak tutmak için :
    var selectedBitmap: Bitmap? = null

    // Firebase kullanıcı bilgilerini almak için :
    private lateinit var auth: FirebaseAuth

    //Kullanıcı media'larını depoya yüklemek için
    private lateinit var storage: FirebaseStorage

    // veri tabanına kaydetmek için
    private lateinit var db :FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        storage = Firebase.storage
        db = Firebase.firestore

        reginsterLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Resim seçme için api 33 altı , 33 ve üstü için ayrı ayrı kontroller:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // sdk 33 ve yukarısı için -> read media images izni :
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // İzin yok, izin iste :

                //Daha öncesinde izin istemiyi red etti mi ? kontrol edilmesi :
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                ) {
                    //İzni red etmiş : Bunun için neden izin istediğimizi açıklayarak bir kere daha izin istenilmeli :
                    Snackbar.make(
                        view,
                        "Galeriden Fotoraf seçmek için vermeniz gerekiyor",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("İzin Ver", View.OnClickListener {
                            // İzin İsteyeceğimiz alan :
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }).show()
                } else {
                    // izin isteyeceğimiz alan :
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }

            } else {
                // izin var , telefonun galerisini aç :
                // Galeriyi aç :
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }


        } else {
            // sdk 33'ün altındaki eski versionlar için -> read external storage :
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // İzin yok, izin iste :

                //Daha öncesinde izin istemiyi red etti mi ? kontrol edilmesi :
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    //İzni red etmiş : Bunun için neden izin istediğimizi açıklayarak bir kere daha izin istenilmeli :
                    Snackbar.make(
                        view,
                        "Galeriden Fotoraf seçmek için vermeniz gerekiyor",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("İzin Ver", View.OnClickListener {
                            // İzin İsteyeceğimiz alan :
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()
                } else {
                    // izin isteyeceğimiz alan :
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }

            } else {
                // izin var , telefonun galerisini aç :
                // Galeriyi aç :
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }


        binding.uploadBtn.setOnClickListener {
            val uuid = UUID.randomUUID()
            val gorselAdi = "${uuid}.jpg"

            val reference = storage.reference
            val gorselReferansi = reference.child("images").child(gorselAdi)
            if (selectedPicture != null) {
                gorselReferansi.putFile(selectedPicture!!).addOnSuccessListener { uploadTask ->
                    // Url alma işlemi yapacağız
                    gorselReferansi.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        //println(downloadUrl) // Log'da resim url yazdırıp kontrol yapılabilir .Verilen url tıklanınca taracıda resmi gösterir

                        // veri tabanına kayıt yap :
                        val postMap = hashMapOf<String,Any>()
                        postMap.put("downloadUrl",downloadUrl)
                        postMap.put("email",auth.currentUser?.email.toString())

                    }
                }.addOnFailureListener { exception ->
                    // Yükleme esnasında hata fırlatıyorsa :
                    Toast.makeText(requireContext(),exception.toString(),Toast.LENGTH_LONG).show()
                }
            }


        }


    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    // onCreate içersinden çağırmak gerekiyor :
    private fun reginsterLauncher() {
        // initialize
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        selectedPicture = intentFromResult.data
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                // SDK 28 ve üstü
                                val source = ImageDecoder.createSource(
                                    requireActivity().contentResolver, selectedPicture!!
                                )
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitmap)

                            } else {
                                // 27 ve altı için :
                                selectedBitmap = MediaStore.Images.Media.getBitmap(
                                    requireActivity().contentResolver, selectedPicture
                                )
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        // izin isteme :
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    // izin verildi
                    // Galeriyi aç :
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    // kullanici izini reddetti
                    Toast.makeText(
                        requireContext(),
                        "Fotoraf Seçimi İçin İzin Verilmesi Gerekiyor",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}