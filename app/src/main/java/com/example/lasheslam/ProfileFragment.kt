package com.example.lasheslam

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.lasheslam.core.GralCtrlEditText
import com.example.lasheslam.core.User
import com.example.lasheslam.core.User.Companion.dataStore
import com.example.lasheslam.databinding.FragmentProfileBinding
import com.example.lasheslam.utils.Constants
import com.example.lasheslam.utils.Utilities.Companion.isValidEmail
import com.example.lasheslam.utils.Utilities.Companion.setOnClickListenerCloseUnfocus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private var gralCtrlEditText = GralCtrlEditText()
    private var homeInterface: HomeInterface? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
        loadImage()
        initButton()
        initView()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeInterface) {
            homeInterface = context
        }
    }
    override fun onDetach() {
        super.onDetach()
        homeInterface = null
    }

    private fun initButton() {
        binding.btnEdit.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                binding.updateDataButton.visibility = View.VISIBLE
                binding.etFirstName.isEnabled = true
                binding.firstNameTextField.isEnabled = true
                binding.phoneNumberTextField.isEnabled = true
                binding.etPhoneNumber.isEnabled = true
            }else{
                binding.updateDataButton.visibility = View.GONE
                binding.etFirstName.isEnabled = false
                binding.firstNameTextField.isEnabled = false
                binding.phoneNumberTextField.isEnabled = false
                binding.etPhoneNumber.isEnabled = false
            }
        }
        binding.logoutButton.setOnClickListener {
            homeInterface?.logOut()
        }
        binding.logoinButton.setOnClickListener {
            homeInterface?.goLogin()
        }
        binding.updateDataButton.setOnClickListener {

        }
    }

    private fun loadImage(){
        auth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            if (currentUser.photoUrl != null) {
                currentUser.let {
                    val profileImageUrl = currentUser.photoUrl?.toString()
                    if (profileImageUrl != null) {
                        Glide.with(this)
                            .load(profileImageUrl)
                            .apply(RequestOptions.circleCropTransform())
                            .into(binding.profileImageView)
                    }
                }
            }
        }
    }

    private fun initView(){
        if (User.userInvited){
            binding.nameUser.text = getString(R.string.create_account)
            binding.logoinButton.visibility = View.VISIBLE
            binding.emailUser.visibility = View.GONE
            binding.logoutButton.visibility = View.GONE
            binding.etFirstName.visibility = View.GONE
            binding.firstNameTextField.visibility = View.GONE
            binding.phoneNumberTextField.visibility = View.GONE
            binding.etPhoneNumber.visibility = View.GONE
        }else{
            binding.nameUser.text = User.userName
            binding.emailUser.text = User.userEmail
            binding.etFirstName.isEnabled = false
            binding.firstNameTextField.isEnabled = false
            binding.phoneNumberTextField.isEnabled = false
            binding.etPhoneNumber.isEnabled = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initializeView() {
        gralCtrlEditText.setDataEditText(
            binding.firstNameTextField,
            binding.etFirstName,
            User.userName,
            null
        )
        gralCtrlEditText.setDataEditText(
            binding.phoneNumberTextField,
            binding.etPhoneNumber,
            User.userPhone,
            null
        )

    }

    private fun checkFields(): Boolean {
        var validate = gralCtrlEditText.validateEditText(
            binding.firstNameTextField,
            binding.etFirstName,
            getString(R.string.a_login_error_name_empty),
            true)
        validate = gralCtrlEditText.validateEditText(
            binding.phoneNumberTextField,
            binding.etPhoneNumber,
            getString(R.string.a_login_error_phone_number_empty),
            validate)
        return validate
    }

}