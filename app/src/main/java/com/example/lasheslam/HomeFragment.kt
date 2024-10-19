package com.example.lasheslam

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lasheslam.core.User
import com.example.lasheslam.databinding.FragmentHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserData(User.userId) { userData ->
            if (userData != null) {
                binding.user.text = userData["name"] as String
            }
        }
    }

    private fun getUserData(userId: String, onComplete: (Map<String, Any>?) -> Unit) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    onComplete(document.data)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error al obtener usuario", e)
                onComplete(null)
            }
    }
}