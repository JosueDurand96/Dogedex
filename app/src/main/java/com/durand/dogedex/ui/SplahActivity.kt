package com.durand.dogedex.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.durand.dogedex.databinding.ActivitySplahBinding
import com.durand.dogedex.ui.admin_fragment.AdminHome

class SplahActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        val binding = ActivitySplahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Thread.sleep(1500)
        screenSplash.setKeepOnScreenCondition {
            true
        }
        val intent = Intent(this, AdminHome::class.java)
        startActivity(intent)
        finish()
    }
}