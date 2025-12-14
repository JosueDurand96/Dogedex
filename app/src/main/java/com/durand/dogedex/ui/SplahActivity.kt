package com.durand.dogedex.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.durand.dogedex.databinding.ActivitySplahBinding
import com.durand.dogedex.ui.auth.LoginActivity
import com.durand.dogedex.ui.auth.OnboardingActivity

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
        
        // Verificar si ya se mostró el onboarding
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val hasSeenOnboarding = sharedPref.getBoolean("has_seen_onboarding", false)
        
        val intent = if (hasSeenOnboarding) {
            Intent(this, LoginActivity::class.java)
        } else {
            Intent(this, OnboardingActivity::class.java)
        }
        
        startActivity(intent)
        finish()
    }
}