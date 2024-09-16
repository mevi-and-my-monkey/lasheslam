package com.example.lasheslam

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lasheslam.core.GralCtrlEditText
import com.example.lasheslam.databinding.FragmentLoginBinding
import com.example.lasheslam.utils.Utilities.Companion.isValidEmail
import com.example.lasheslam.utils.Utilities.Companion.setOnClickListenerCloseUnfocus
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private var gralCtrlEditText = GralCtrlEditText()
    private var loginInterface: LoginInterface? = null
    val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configurarGoogleSignIn()
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
            val bottomSheet = RecoverPassFragment()
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }
        binding.creatAccountbutton.setOnClickListenerCloseUnfocus(requireContext(),binding.root) {
            val bottomSheet = CreateAccountFragment()
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }
        binding.continueGooglebutton.setOnClickListenerCloseUnfocus(requireContext(), binding.root){
            iniciarSesionConGoogle()
        }
    }
    private fun configurarGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))  // Aquí va el client ID de Firebase
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
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

    private fun iniciarSesionConGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthConGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                println("Error en Google Sign-In: ${e.message}")
            }
        }
    }

    private fun firebaseAuthConGoogle(idToken: String) {
        val auth = FirebaseAuth.getInstance()
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        loginInterface?.showHomeActivity()
                        println("Inicio de sesión con Google exitoso, UID: ${user.uid}")
                        println("Inicio de sesión con Google exitoso, UID: ${user.email}")
                        println("Inicio de sesión con Google exitoso, UID: ${user.displayName}")
                        println("Inicio de sesión con Google exitoso, UID: ${user.photoUrl}")
                    }
                } else {
                    task.exception?.let {
                        println("Error en la autenticación con Firebase: ${it.message}")
                    }
                }
            }
    }
}