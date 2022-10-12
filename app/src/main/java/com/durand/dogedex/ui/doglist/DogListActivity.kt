package com.durand.dogedex.ui.doglist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.durand.dogedex.api.ApiResponseStatus
import com.durand.dogedex.databinding.ActivityDogListBinding
import com.durand.dogedex.ui.dogdetail.DogDetailActivity
import com.durand.dogedex.ui.dogdetail.DogDetailActivity.Companion.DOG_KEY

class DogListActivity : AppCompatActivity() {

    private val dogListViewModel: DogListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loadingWheel = binding.loadingWheel
        val recycler = binding.dogRecycler
        recycler.layoutManager = LinearLayoutManager(this)
        val adapter = DogAdapter()
        recycler.adapter = adapter

        adapter.setOnClickListener {
            val intent = Intent(this, DogDetailActivity::class.java)
            intent.putExtra(DOG_KEY, it)
            startActivity(intent)
        }
        dogListViewModel.dogList.observe(this) { dogList ->
            adapter.submitList(dogList)
        }

        dogListViewModel.status.observe(this) {
                status ->
                when (status) {
                    ApiResponseStatus.LOADING -> {
                     binding.loadingWheel.visibility = View.VISIBLE
                    }
                    ApiResponseStatus.ERROR -> {
                        binding.loadingWheel.visibility = View.GONE
                        Toast.makeText(this,"Error al cargar los datos!", Toast.LENGTH_SHORT).show()
                    }
                    ApiResponseStatus.SUCCESS -> {
                        binding.loadingWheel.visibility = View.GONE
                        Toast.makeText(this,"Se cargaron los datos correctamente!", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        binding.loadingWheel.visibility = View.GONE
                        Toast.makeText(this,"Ocurrio un error!", Toast.LENGTH_SHORT).show()
                    }


                }

        }

    }

}