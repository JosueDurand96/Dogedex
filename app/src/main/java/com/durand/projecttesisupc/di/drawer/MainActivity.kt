package com.durand.projecttesisupc.di.drawer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.durand.projecttesisupc.R

class MainActivity : AppCompatActivity(), TestRecyclerClickListener {

    private lateinit var recycler: RecyclerView
    private lateinit var button1: ImageView
    private var listItem = arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycler = findViewById(R.id.recycler)

        val data: String = intent.getStringExtra("data").toString()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (data == "admin") {
            listItem = arrayListOf(
                "Reporte canes registrados",
                "Identificar propietario de can peligros",
                "Reportar can agresor",
                "Consultar incidencias de can agresor",
                "Editar Roles del Sistema"
            )
        } else {
            listItem = arrayListOf(
                "Registrar can",
                "Registrar hocico",
                "Mi can perdido",
                "Mis canes registrados",
                "Canes reportados perdidos"
            )
        }



    }

    override fun onClick(name: String?) {
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show()
        if (name=="Registrar hocico"){
        }
    }
}