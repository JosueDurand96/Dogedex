package com.durand.projecttesisupc.di.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.durand.projecttesisupc.R

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var sendPasswordAppCompatButton: AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        sendPasswordAppCompatButton = findViewById(R.id.sendPasswordAppCompatButton)
        sendPasswordAppCompatButton.setOnClickListener {
            finish()
        }
    }
}