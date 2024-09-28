package com.example.lasheslam

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lasheslam.core.GralCtrlEditText
import com.example.lasheslam.databinding.FragmentRecoverPassBinding
import com.example.lasheslam.utils.Utilities.Companion.isValidEmail
import com.example.lasheslam.utils.Utilities.Companion.setOnClickListenerCloseUnfocus
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth

class RecoverPassFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentRecoverPassBinding
    private var gralCtrlEditText = GralCtrlEditText()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecoverPassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    @SuppressLint("SetTextI18n")
    private fun initializeView() {
        gralCtrlEditText.setDataEditText(
            binding.emailTextField,
            binding.etEmail,
            getString(R.string.login_email),
            null
        )
        binding.enterButton.setOnClickListenerCloseUnfocus(requireContext(),binding.root){
            if (checkFields()) {
                if (isValidEmail(binding.etEmail.text.toString())){
                    recoverPassword(binding.etEmail.text.toString().trim())
                }else{
                    binding.emailTextField.error = getString(R.string.a_login_error_email_incorrect)
                }
            }
        }
    }

    private fun checkFields(): Boolean {
        val validate = gralCtrlEditText.validateEditText(
            binding.emailTextField,
            binding.etEmail,
            getString(R.string.a_login_error_email_empty),
            true)
        return validate
    }

    private fun recoverPassword(email: String) {
        val auth = FirebaseAuth.getInstance()

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Correo de restablecimiento de contraseña enviado")
                } else {
                    task.exception?.let {
                        println("Error al enviar el correo: ${it.message}")
                    }
                }
            }
    }
}