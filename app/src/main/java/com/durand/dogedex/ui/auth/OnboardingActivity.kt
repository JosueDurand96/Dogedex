package com.durand.dogedex.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.viewpager2.widget.ViewPager2
import com.durand.dogedex.R
import com.durand.dogedex.databinding.ActivityOnboardingBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {
    private lateinit var mViewPager: ViewPager2
    private lateinit var btnCreateAccount: Button
    private lateinit var pageIndicator: TabLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        btnCreateAccount = findViewById(R.id.btn_create_account)
        pageIndicator = findViewById(R.id.pageIndicator)
        mViewPager = findViewById(R.id.viewPager)

        btnCreateAccount.setOnClickListener {
            // Guardar que ya se mostró el onboarding
            val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putBoolean("has_seen_onboarding", true)
            editor.apply()
            
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        mViewPager.adapter = OnboardingViewPagerAdapter(this, this)
        TabLayoutMediator(pageIndicator, mViewPager) { _, _ -> }.attach()
        mViewPager.offscreenPageLimit = 1
    }
}