package com.durand.dogedex.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.durand.dogedex.ui.auth.OnboardingActivity
import com.durand.dogedex.ui.ui.theme.PatitasSegurasUPCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PatitasSegurasUPCTheme {
                navigateToOnboarding()
            }
        }
    }
    private fun navigateToOnboarding() {
        startActivity(Intent(this, OnboardingActivity::class.java))
        finish()
    }
}
