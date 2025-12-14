package com.durand.dogedex.ui.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.durand.dogedex.R
import com.durand.dogedex.ui.user_fragment.UserHome
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), LoginFragment.LoginFragmentActions, SignUpFragment.SignUpFragmentActions{

    private val viewModel: AuthViewModel by viewModels()
    
    companion object {
        private const val SESSION_DURATION_MS = 5 * 60 * 1000L // 5 minutos en milisegundos
        private const val PREFS_NAME = "idUsuario"
        private const val KEY_LOGIN_TIMESTAMP = "loginTimestamp"
        private const val KEY_ID_USUARIO = "idUsuario"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Verificar si hay una sesión válida antes de mostrar el login
        if (isSessionValid()) {
            Log.d("LoginActivity", "Sesión válida encontrada, redirigiendo a UserHome")
            val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val idUsuario = sharedPref.getLong(KEY_ID_USUARIO, -1)
            if (idUsuario != -1L) {
                val intent = Intent(this, UserHome::class.java)
                intent.putExtra("id", idUsuario)
                startActivity(intent)
                finish()
                return
            }
        } else {
            Log.d("LoginActivity", "No hay sesión válida o la sesión expiró")
        }
        
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.status.observe(this) { status ->

            when (status) {
                is ApiResponseStatus.Error -> {
                    binding.loadingWheel.visibility = View.GONE
                    showErrorDialog(status.message)
                }
                is ApiResponseStatus.Loading -> binding.loadingWheel.visibility = View.VISIBLE
                is ApiResponseStatus.Success -> {
                    binding.loadingWheel.visibility = View.GONE

                }
            }
        }

        viewModel.user.observe(this){
            user ->
            if(user != null){
                Log.d("josue","user: startMainActivity" )

                startMainActivity()
            }else{
                Log.d("josue","user: no" )
            }
        }
    }

    private fun startMainActivity(){
        startActivity(Intent(this, UserHome::class.java))
        finish()
    }

    private fun showErrorDialog(messageId: Int){
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(messageId)
            .setPositiveButton(android.R.string.ok) { _, _ ->}
            .create()
            .show()
    }

    override fun onRegisterButtonClick() {
        findNavController(R.id.nav_host_fragment).navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
    }

    override fun onLoginFieldsValidated(email: String, password: String) {
        viewModel.login(email,password)
    }

    override fun onSignUpFieldsValidated(
        email: String,
        password: String,
        passwordConfirmation: String
    ) {
        Log.d("josue","email: $email")
        Log.d("josue","password: $password")
        Log.d("josue","passwordConfirmation: $passwordConfirmation")
        viewModel.onSignUp(email, password, passwordConfirmation)
    }

    override fun onBackPressed() {
        finish()
    }
    
    /**
     * Verifica si hay una sesión válida (menos de 5 minutos desde el último login)
     * @return true si la sesión es válida, false en caso contrario
     */
    private fun isSessionValid(): Boolean {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val loginTimestamp = sharedPref.getLong(KEY_LOGIN_TIMESTAMP, -1)
        val idUsuario = sharedPref.getLong(KEY_ID_USUARIO, -1)
        
        // Si no hay timestamp o idUsuario, no hay sesión
        if (loginTimestamp == -1L || idUsuario == -1L) {
            return false
        }
        
        // Calcular el tiempo transcurrido desde el login
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - loginTimestamp
        
        // Verificar si han pasado menos de 5 minutos
        val isValid = elapsedTime < SESSION_DURATION_MS
        
        if (isValid) {
            Log.d("LoginActivity", "Sesión válida. Tiempo transcurrido: ${elapsedTime / 1000} segundos")
        } else {
            Log.d("LoginActivity", "Sesión expirada. Tiempo transcurrido: ${elapsedTime / 1000} segundos (máximo: ${SESSION_DURATION_MS / 1000} segundos)")
            // Limpiar la sesión expirada
            clearSession()
        }
        
        return isValid
    }
    
    /**
     * Limpia la sesión guardada
     */
    private fun clearSession() {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.remove(KEY_LOGIN_TIMESTAMP)
        // No eliminamos idUsuario porque puede ser útil para otras partes de la app
        editor.apply()
    }
}