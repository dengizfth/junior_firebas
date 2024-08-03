package com.fatihden.firebase.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.Navigation
import com.fatihden.firebase.R
import com.fatihden.firebase.databinding.FragmentFeedBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class FeedFragment : Fragment() , PopupMenu.OnMenuItemClickListener {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    // Pop Up menu decleration
    private lateinit var popupMenu: PopupMenu

    //Firebase Decleration
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Firebase , Initialize Auth
        auth = Firebase.auth
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

}