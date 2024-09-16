package com.example.lasheslam

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lasheslam.core.GralCtrlEditText
import com.example.lasheslam.databinding.FragmentLoginBinding
import com.example.lasheslam.utils.Utilities.Companion.setOnClickListenerCloseUnfocus
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private var gralCtrlEditText = GralCtrlEditText()
    private var loginInterface: LoginInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openSession()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LoginInterface) {
            loginInterface = context
        }
    }
    override fun onDetach() {
        super.onDetach()
        loginInterface = null
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
        binding.enterButton.setOnClickListenerCloseUnfocus(requireContext(),binding.root){
            if (checkFields()) {
                if (isValidEmail(binding.etEmail.text.toString())){
                    login(binding.etEmail.text.toString().trim(), binding.etPassword.text.toString().trim())
                }else{
                    binding.emailTextField.error = getString(R.string.a_login_error_email_incorrect)
                }
            }
        }
        binding.forgotNipButton.setOnClickListenerCloseUnfocus(requireContext(),binding.root) {

        }
        binding.creatAccountbutton.setOnClickListenerCloseUnfocus(requireContext(),binding.root) {
            val bottomSheet = CreateAccountFragment()
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }
    }

    private fun openSession() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            loginInterface?.showHomeActivity()
        } else {
            Log.i("AUT_LOG","No hay usuario autenticado")
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
        return validate
    }

    private fun isValidEmail(correo: String): Boolean {
        val patron = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        return correo.matches(patron)
    }

    private fun login(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        loginInterface?.showHomeActivity()
                        Log.i("AUTH_SIGN_IN","Inicio de sesión exitoso, UID: ${user.uid}")
                    }
                } else {
                    task.exception?.let {
                        Log.i("AUTH_SIGN_IN","Error al iniciar sesión: ${it.message}")
                    }
                }
            }
    }
}