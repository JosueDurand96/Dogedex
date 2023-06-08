package com.durand.projecttesisupc.di.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.durand.projecttesisupc.R
import com.durand.projecttesisupc.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()

    }

    private fun initViews() {
        val itemsTypeDocument = listOf("Dni","RUC","Carne de extranjeria")
        val adapterTypeDocument = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itemsTypeDocument)
        binding.typeDocumentAutoCompleteTextView.setAdapter(adapterTypeDocument)

        val itemsDistrict = listOf("Lima","San Isidro","Miraflores", "San Miguel", "San Borja", "Surco", "Jesus Maria", "Rimac","SMP")
        val adapterDistrict = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itemsDistrict)
        binding.districtAutoCompleteTextView.setAdapter(adapterDistrict)

        val itemHome = listOf("Casa","Departamento","Otros")
        val adapterHome = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itemHome)
        binding.typeHomeAutoCompleteTextView.setAdapter(adapterHome)

    }
}