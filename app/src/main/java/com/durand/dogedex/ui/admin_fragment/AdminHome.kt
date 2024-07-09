package com.durand.dogedex.ui.admin_fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.durand.dogedex.R
import com.durand.dogedex.databinding.ActivityAdminBinding
import com.durand.dogedex.ui.admin_fragment.ui.propietarios_can_peligroso.ListCanPeligrosoPerdidasViewModel
import com.durand.dogedex.ui.auth.LoginActivity
import com.google.android.gms.maps.model.LatLng

class AdminHome : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityAdminBinding
    private lateinit var viewModel: ListCanPeligrosoPerdidasViewModel
    private var locationArrayList: ArrayList<LatLng>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ListCanPeligrosoPerdidasViewModel::class.java)

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)



//

        setSupportActionBar(binding.appBarAdmin.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_admin)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_report_register_can, R.id.nav_propietario_can_peligroso, R.id.nav_report_can_agresor,
                R.id.nav_consult_incidencias_can, R.id.nav_edit_roles_sistema, R.id.nav_close
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }




    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_admin)
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
                        this@AdminHome, LoginActivity::class.java
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