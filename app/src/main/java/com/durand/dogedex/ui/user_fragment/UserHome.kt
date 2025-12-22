package com.durand.dogedex.ui.user_fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
import android.widget.TextView

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
                R.id.nav_register_can,R.id.nav_register_hocio, R.id.nav_my_can_lost, R.id.nav_my_can_register, R.id.nav_can_report_lost
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        
        // Configurar navegación normal primero
        navView.setupWithNavController(navController)
        
        // Actualizar el nombre del usuario en el header del drawer
        updateUserNameInDrawer(navView)
        
        // Luego, sobrescribir el listener para manejar "Salir" manualmente
        navView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.nav_close) {
                // Cerrar sesión y redirigir al login
                drawerLayout.closeDrawers()
                logout()
                true // Indica que manejamos el evento y no queremos navegación
            } else {
                // Para otros items, usar la navegación normal
                // setupWithNavController ya configuró el listener, pero lo sobrescribimos
                // Necesitamos llamar manualmente a la navegación
                try {
                    navController.navigate(menuItem.itemId)
                    drawerLayout.closeDrawers()
                } catch (e: Exception) {
                    Log.e("UserHome", "Error al navegar: ${e.message}", e)
                }
                true
            }
        }
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
        super.onBackPressed()
        if (doubleBackToExitPressedOnce) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("¿Desea volver al Inicio?")
            builder.setTitle("Patitas seguras!")
            builder.setPositiveButton(
                "Si"
            ) { dialog, which ->
                logout()
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
    
    private fun logout() {
        try {
            // Limpiar SharedPreferences
            val sharedPref = getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.clear()
            editor.apply()
            editor.commit()
            
            // Limpiar también la sesión
            val sessionPref = getSharedPreferences("session", Context.MODE_PRIVATE)
            val sessionEditor: SharedPreferences.Editor = sessionPref.edit()
            sessionEditor.clear()
            sessionEditor.apply()
            sessionEditor.commit()
            
            Log.d("UserHome", "Sesión cerrada, redirigiendo a LoginActivity")
            
            // Redirigir al login
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e("UserHome", "Error al cerrar sesión: ${e.message}", e)
            Toast.makeText(this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun updateUserNameInDrawer(navView: NavigationView) {
        val sharedPref = getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
        val nombreUsuario = sharedPref.getString("nombreUsuario", null)
        
        if (nombreUsuario != null) {
            val headerView = navView.getHeaderView(0)
            val txtNombreUsuario = headerView.findViewById<TextView>(R.id.txtNombreUsuario)
            // Limpiar espacios en blanco del nombre y mostrar con mensaje de bienvenida
            val nombreLimpio = nombreUsuario.trim()
            txtNombreUsuario?.text = nombreLimpio
        }
    }
}