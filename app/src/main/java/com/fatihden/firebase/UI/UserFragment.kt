package com.fatihden.firebase.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.fatihden.firebase.R
import com.fatihden.firebase.databinding.FragmentUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class UserFragment : Fragment() {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    //Firebase:
    private lateinit var auth: FirebaseAuth // Declare
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Firebase , Initialize Auth
        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpBtn.setOnClickListener {

            val email = binding.emailET.text.toString()
            val password = binding.passwordET.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Kullanıcı Oluşturulduysa :
                            val action = UserFragmentDirections.actionUserFragmentToFeedFragment()
                            Navigation.findNavController(it).navigate(action)
                        }
                    }

            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}