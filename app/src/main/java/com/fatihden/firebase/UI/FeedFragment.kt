package com.fatihden.firebase.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.Navigation
import com.fatihden.firebase.Model.Post
import com.fatihden.firebase.R
import com.fatihden.firebase.databinding.FragmentFeedBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class FeedFragment : Fragment() , PopupMenu.OnMenuItemClickListener {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    // Pop Up menu decleration
    private lateinit var popupMenu: PopupMenu

    //Firebase Decleration
    private lateinit var auth : FirebaseAuth

    //databse decleration
    private lateinit var db:FirebaseFirestore

    // Gelen verileri List olarak tutmak için :
    val postList : ArrayList<Post> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Firebase , Initialize Auth
        auth = Firebase.auth

        //database initialization
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // xml'deki bir şeyi koda bağlarken sonu inflater ile biten yapıları kullanırız
        // layout'u koda bağlayınca LayoutInflater kullandığımız gibi menu içinde inflater var
        // istediği context e requireContext() verdikten sonra ne ile bağlantı yapıcağız
        // bağlantı yapılacak yapının id'si verilir -> ,binding.floatingActionButton gibi
        popupMenu = PopupMenu(requireContext(),binding.floatingActionButton)
        // bağlamak için menuInflater kullanılır . menu xml koda bağlamak için kullanılır.
        val inflater = popupMenu.menuInflater
        //kodu ile xml bağlamak için son olarak :
        inflater.inflate(R.menu.my_popup_menu,popupMenu.menu)
        binding.floatingActionButton.setOnClickListener {

            //Görünümde artık gözükmesi için :
            popupMenu.show()

        }
        //FeedFragment tetikler ve alttaki fun çalıştırır
        popupMenu.setOnMenuItemClickListener(this)


        //FireStore'dan verileri çeken function
        fireStoreGetData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {

        when(item?.itemId){
            R.id.uploadItem -> {
                // Resim Yükleme ekranı :
                val action = FeedFragmentDirections.actionFeedFragmentToUploadFragment()
                Navigation.findNavController(requireView()).navigate(action)
            }
            R.id.logOutItem -> {
                // Çıkış :
                auth.signOut()
                val action = FeedFragmentDirections.actionFeedFragmentToUserFragment()
                Navigation.findNavController(requireView()).navigate(action)

            }
        }
        return true
    }

    // FireStore'dan verileri çek :
    private fun fireStoreGetData() {
        db.collection("Posts").addSnapshotListener { value,error ->
            if (error != null){
                // error null değilse value' de sıkıntı olabilir .
                Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_LONG).show()
            } else {
                // error null ve value içinde değer var -> verileri çektiğimiz anlamda
                if (value != null) { // value null değilse
                    if (!value.isEmpty) {// value ! boş değilse
                        val documents = value.documents
                        for (document in documents) {

                            // Gelen Any? tipini casting yaparak String olarak alma :
                            val comment = document.get("comment") as String // Castin işlemi --> String'e cast edicez
                            val email = document.get("email") as String // casting Stiring
                            val downloadUrl =  document.get("downloadUrl") as String

                            var post = Post(email,comment,downloadUrl)
                            postList.add(post)
                        }
                    }
                }
            }
        }
    }
}