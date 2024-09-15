package com.example.lasheslam

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lasheslam.core.GralCtrlEditText
import com.example.lasheslam.databinding.FragmentLoginBinding
import com.example.lasheslam.utils.Utilities.Companion.setOnClickListenerCloseUnfocus

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private var gralCtrlEditText = GralCtrlEditText()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
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
        binding.enterButton.setOnClickListenerCloseUnfocus(requireContext(),binding.root){
            if (checkFields()) {
                if (isValidEmail(binding.etEmail.text.toString())){
                }else{
                    binding.emailTextField.error = getString(R.string.a_login_error_email_incorrect)
                }
            }
        }
        binding.forgotNipButton.setOnClickListenerCloseUnfocus(requireContext(),binding.root) {

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
}