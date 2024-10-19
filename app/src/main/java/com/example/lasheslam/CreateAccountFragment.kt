package com.example.lasheslam

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import com.example.lasheslam.core.GralCtrlEditText
import com.example.lasheslam.core.User
import com.example.lasheslam.core.User.Companion.dataStore
import com.example.lasheslam.databinding.FragmentCreateAccountBinding
import com.example.lasheslam.utils.Constants.Companion.EMAIL
import com.example.lasheslam.utils.Utilities.Companion.isValidEmail
import com.example.lasheslam.utils.Utilities.Companion.setOnClickListenerCloseUnfocus
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CreateAccountFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCreateAccountBinding
    private val db = Firebase.firestore
    private var gralCtrlEditText = GralCtrlEditText()
    private var loginInterface: LoginInterface? = null

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
        gralCtrlEditText.setDataEditText(
            binding.firstNameTextField,
            binding.etFirstName,
            getString(R.string.first_name),
            null
        )
        gralCtrlEditText.setDataEditText(
            binding.phoneNumberTextField,
            binding.etPhoneNumber,
            getString(R.string.phone_numbre),
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
    @OptIn(DelicateCoroutinesApi::class)
    private fun registrarUsuario(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    GlobalScope.launch {
                        requireContext().dataStore.edit { preferences ->
                            preferences[EMAIL] = email
                        }
                    }
                    user?.let {
                        val dialog = GenericDialogFragment()
                            .setType(0)
                            .setTitle(getString(R.string.register))
                            .setMessage(getString(R.string.register_succes))
                            .setPositiveButton(getString(R.string.accept)){
                                saveUserData(user)
                                dismiss()
                            }
                        dialog.show(requireActivity().supportFragmentManager, "customDialog")
                     }
                } else {
                    task.exception?.let {
                        val dialog = GenericDialogFragment()
                            .setType(1)
                            .setTitle(getString(R.string.failed))
                            .setMessage("${getString(R.string.register_failed)} ${it.message}")
                            .setNegativeButton(getString(R.string.accept)) {
                                dismiss()
                            }
                        dialog.show(requireActivity().supportFragmentManager, "customDialog")
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
            getString(R.string.a_login_error_name_empty),
            validate)
        validate = gralCtrlEditText.validateEditText(
            binding.phoneNumberTextField,
            binding.etPhoneNumber,
            getString(R.string.a_login_error_phone_number_empty),
            validate)
        return validate
    }

    private fun saveUserData(user: FirebaseUser) {
        val userData = hashMapOf(
            "email" to user.email,
            "isAdmin" to false,
            "name" to binding.etFirstName.text.toString(),
            "phoneNumber" to binding.etPhoneNumber.text.toString()
        )
        User.userId = user.uid
        db.collection("users").document(user.uid)
            .set(userData)
            .addOnSuccessListener {
                loginInterface?.showHomeActivity()
                Log.d("Firestore", "Usuario guardado correctamente")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error al guardar usuario", e)
            }
    }
}