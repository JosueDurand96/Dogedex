package com.durand.dogedex

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.durand.dogedex.api.User
import com.durand.dogedex.databinding.ActivityMainBinding
import com.durand.dogedex.ui.auth.LoginActivity
import com.durand.dogedex.ui.settings.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = User.getLoggedInUser(this)
        if (user == null) {
            openLoginActivity()
            return
        }

        binding.settingsFab.setOnClickListener {
            openSettingsActivity()
        }
    }

    private fun openSettingsActivity() {
        startActivity(Intent(this, SettingsActivity::class.java))
        finish()
    }

    private fun openLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


}