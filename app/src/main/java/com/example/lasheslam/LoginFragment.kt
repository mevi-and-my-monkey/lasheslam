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
import androidx.datastore.preferences.core.edit
import com.example.lasheslam.core.User.Companion.dataStore
import com.example.lasheslam.core.GralCtrlEditText
import com.example.lasheslam.core.User
import com.example.lasheslam.databinding.FragmentLoginBinding
import com.example.lasheslam.utils.Constants.Companion.EMAIL
import com.example.lasheslam.utils.Constants.Companion.MODE_INVITED
import com.example.lasheslam.utils.Utilities.Companion.isValidEmail
import com.example.lasheslam.utils.Utilities.Companion.setOnClickListenerCloseUnfocus
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private var gralCtrlEditText = GralCtrlEditText()
    private var loginInterface: LoginInterface? = null
    private val db = Firebase.firestore
    private val GOOGLE_SIGN_IN = 100

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
        modInvited()
        initializeView()
    }

    override fun onResume() {
        super.onResume()
        val email = getSaveEmail()
        if (email.isNotEmpty()) {
            binding.etEmail.setText(email)
        }
    }

    private fun modInvited() {
        if (getSavedValue()){
            User.userInvited = true
            loginInterface?.showHomeActivity()
        }
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

    private fun getSavedValue(): Boolean {
        var savedValue = false
        runBlocking {
            val preferences = requireContext().dataStore.data.map { preferences ->
                preferences[MODE_INVITED] ?: false
            }
            savedValue = preferences.first()
        }
        return savedValue
    }

    private fun getSaveEmail(): String {
        var email = ""
        runBlocking {
            val preferences = requireContext().dataStore.data.map { preferences ->
                preferences[EMAIL] ?: ""
            }
            email = preferences.first()
        }
        return email
    }

    @OptIn(DelicateCoroutinesApi::class)
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
        binding.forgotPassButton.setOnClickListenerCloseUnfocus(requireContext(),binding.root) {
            val bottomSheet = RecoverPassFragment()
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }
        binding.creatAccountbutton.setOnClickListenerCloseUnfocus(requireContext(),binding.root) {
            val bottomSheet = CreateAccountFragment()
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }
        binding.continueInvitedUser.setOnClickListenerCloseUnfocus(requireContext(), binding.root){
            GlobalScope.launch {
                requireContext().dataStore.edit { preferences ->
                    preferences[MODE_INVITED] = true
                }
            }
            User.userInvited = true
            loginInterface?.showHomeActivity()
        }
        binding.continueGooglebutton.setOnClickListenerCloseUnfocus(requireContext(), binding.root){
            iniciarSesionConGoogle()
        }
    }
    private fun configurarGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun openSession() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            User.userId = currentUser.uid
        }
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
                    User.userId = user?.uid.toString()
                    user?.let {
                        saveUserData(user)
                    }
                } else {
                    task.exception?.let {
                        println("Error en la autenticación con Firebase: ${it.message}")
                    }
                }
            }
    }

    private fun saveUserData(user: FirebaseUser) {
        val isAdmi = user.email == "alejandromevi26@gmail.com"
        val userData = hashMapOf(
            "email" to user.email,
            "isAdmin" to isAdmi,
            "name" to user.displayName,
            "phoneNumber" to "*"
            )
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