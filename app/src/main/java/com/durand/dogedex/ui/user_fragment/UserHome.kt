package com.durand.dogedex.ui.user_fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.durand.dogedex.R
import com.durand.dogedex.databinding.ActivityHomeBinding
import com.durand.dogedex.ui.auth.LoginActivity
import com.google.android.material.navigation.NavigationView

class UserHome : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHome.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_register_can,R.id.nav_register_hocio, R.id.nav_my_can_lost, R.id.nav_my_can_register, R.id.nav_can_report_lost, R.id.nav_close
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    private var doubleBackToExitPressedOnce = false
    private val mHandler = Handler()


    private val mRunnable = Runnable { doubleBackToExitPressedOnce = false }


    override fun onDestroy() {
        super.onDestroy()
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable)
        }
    }


    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("¿Desea volver al Inicio?")
            builder.setTitle("Patitas seguras!")
            builder.setPositiveButton(
                "Si"
            ) { dialog, which ->
                startActivity(
                    Intent(
                        this@UserHome, LoginActivity::class.java
                    )
                )
                finish()
            }
            builder.setNegativeButton(
                "No"
            ) { dialog, which -> dialog.cancel() }
            val dialog = builder.create()
            dialog.show()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Por favor presione dos veces para salir", Toast.LENGTH_SHORT).show()
        mHandler.postDelayed(mRunnable, 2000)
    }
}