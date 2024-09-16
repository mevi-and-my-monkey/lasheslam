package com.example.lasheslam

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.lasheslam.core.GralCtrlEditText
import com.example.lasheslam.databinding.FragmentCreateAccountBinding
import com.example.lasheslam.utils.Utilities.Companion.isValidEmail
import com.example.lasheslam.utils.Utilities.Companion.setOnClickListenerCloseUnfocus
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth


class CreateAccountFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCreateAccountBinding
    private var gralCtrlEditText = GralCtrlEditText()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
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
        gralCtrlEditText.setDataEditText(
            binding.passwordTextField,
            binding.etPassword,
            getString(R.string.login_password),
            null
        )
        gralCtrlEditText.setDataEditText(
            binding.firstNameTextField,
            binding.etFirstName,
            getString(R.string.login_email),
            null
        )
        gralCtrlEditText.setDataEditText(
            binding.lastNameTextField,
            binding.etLastName,
            getString(R.string.login_password),
            null
        )
        gralCtrlEditText.setDataEditText(
            binding.phoneNumberTextField,
            binding.etPhoneNumber,
            getString(R.string.login_email),
            null
        )
        binding.enterButton.setOnClickListenerCloseUnfocus(requireContext(),binding.root){
            if (checkFields()) {
                if (isValidEmail(binding.etEmail.text.toString())){
                    registrarUsuario(binding.etEmail.text.toString().trim(), binding.etPassword.text.toString().trim())
                }else{
                    binding.emailTextField.error = getString(R.string.a_login_error_email_incorrect)
                }
            }
        }
    }
    private fun registrarUsuario(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userId = user.uid
                        Log.i("AUTH","Registro exitoso, UID: $userId")
                        Log.i("AUTH","${user.email}")
                        user.sendEmailVerification()
                        dismiss()
                    }
                } else {
                    task.exception?.let {
                        Toast.makeText(requireContext(), "Error al registrar: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun checkFields(): Boolean {
        var validate = gralCtrlEditText.validateEditText(
            binding.emailTextField,
            binding.etEmail,
            getString(R.string.a_login_error_email_empty),
            true)
        validate = gralCtrlEditText.validateEditText(
            binding.passwordTextField,
            binding.etPassword,
            getString(R.string.a_login_error_nip_empty),
            validate)
        validate = gralCtrlEditText.validateEditText(
            binding.firstNameTextField,
            binding.etFirstName,
            getString(R.string.a_login_error_nip_empty),
            validate)
        validate = gralCtrlEditText.validateEditText(
            binding.phoneNumberTextField,
            binding.etPhoneNumber,
            getString(R.string.a_login_error_nip_empty),
            validate)
        return validate
    }

}