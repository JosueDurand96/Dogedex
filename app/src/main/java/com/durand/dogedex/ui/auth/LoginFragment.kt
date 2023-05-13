package com.durand.dogedex.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.durand.dogedex.api.User
import com.durand.dogedex.api.dto.AddLoginDTO
import com.durand.dogedex.databinding.FragmentLoginBinding
import com.durand.dogedex.ui.admin_fragment.AdminHome
import com.durand.dogedex.ui.user_fragment.UserHome

class LoginFragment : Fragment() {

    interface LoginFragmentActions {
        fun onRegisterButtonClick()
        fun onLoginFieldsValidated(email: String, password: String)
    }

    private lateinit var loginFragmentActions: LoginFragmentActions
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: AuthViewModel
    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginFragmentActions = try {
            context as LoginFragmentActions
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must be implement LoginFragmentActions")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        binding = FragmentLoginBinding.inflate(layoutInflater)
        binding.loginRegisterButton.setOnClickListener {
            loginFragmentActions.onRegisterButtonClick()
        }
        binding.loginButton.setOnClickListener {
            validateFields()
        }

        viewModel.login.observe(requireActivity()) {

            User.setLoggedInUser(requireActivity(), User(it.idUsuario.toLong(), it.correoElectronico,""))
            Log.d("login", "nombre: " + it.nombre)
            Log.d("login", "tipoUsuario: " + it.tipoUsuario)
            if (it.tipoUsuario == "U") {
                val intent = Intent(requireContext(), UserHome::class.java)
                startActivity(intent)
            } else if (it.tipoUsuario == "A") {
                val intent = Intent(requireContext(), AdminHome::class.java)
                startActivity(intent)
            }
        }
        return binding.root
    }

    private fun validateFields() {
        binding.emailInput.error = ""
        binding.passwordInput.error = ""
        val email = binding.emailEdit.text.toString()
//        if (!isValidEmail(email)){
//            binding.emailInput.error = "Email is not valid!"
//            return
//        }

        val password = binding.passwordEdit.text.toString()
        if (password.isEmpty()) {
            binding.passwordInput.error = "Password is not valid!"
            return
        }
        Log.d("login", "email: $email")
        Log.d("login", "password: $password")
        Log.d("login", "viewModel: 1")
        viewModel.onLogin(AddLoginDTO(email, password))
        Log.d("login", "viewModel: 2")
        loginFragmentActions.onLoginFieldsValidated("josue@gmail.com", "12345678")
        Log.d("login", "viewModel: 3")
    }
}