package com.durand.projecttesisupc.di.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.durand.projecttesisupc.R
import com.durand.projecttesisupc.di.drawer.MainActivity
import com.durand.projecttesisupc.di.fragment.user.HomeUserActivity
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var loginAppCompatButton: AppCompatButton
    private lateinit var registerAppCompatButton: AppCompatButton
    private lateinit var forgotPassword: TextView
    private lateinit var userTextInputEditText: TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        forgotPassword = findViewById(R.id.forgotPassword)
        loginAppCompatButton = findViewById(R.id.loginAppCompatButton)
        registerAppCompatButton = findViewById(R.id.registerAppCompatButton)
        userTextInputEditText = findViewById(R.id.userTextInputEditText)



        registerAppCompatButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        loginAppCompatButton.setOnClickListener {
            val intent = Intent(this, HomeUserActivity::class.java)
            startActivity(intent)
        }
        forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }
        registerAppCompatButton

    }
}