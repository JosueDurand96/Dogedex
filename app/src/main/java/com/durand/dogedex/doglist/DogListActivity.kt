package com.durand.dogedex.doglist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.durand.dogedex.api.response.Dog
import com.durand.dogedex.databinding.ActivityDogListBinding

class DogListActivity : AppCompatActivity() {

    private val dogListViewModel:DogListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogListBinding.inflate(layoutInflater)
        setContentView(binding.root)


       // val dogList = getFakeDogs()
        val recycler = binding.dogRecycler
        recycler.layoutManager = LinearLayoutManager(this)
        val adapter = DogAdapter()
        recycler.adapter = adapter

        dogListViewModel.dogList.observe(this){
             dogList->
            adapter.submitList(dogList)
        }

    }


//    private fun getFakeDogs(): MutableList<Dog> {
//        val dogList = mutableListOf<Dog>()
//        dogList.add(
//            Dog(
//                1, 1, "Chihuahua", "Toy", 5.4,
//                6.7, "", "12 - 15", "", 10.5,
//                12.3
//            )
//        )
//        dogList.add(
//            Dog(
//                2, 1, "Labrador", "Toy", 5.4,
//                6.7, "", "12 - 15", "", 10.5,
//                12.3
//            )
//        )
//        dogList.add(
//            Dog(
//                3, 1, "Retriever", "Toy", 5.4,
//                6.7, "", "12 - 15", "", 10.5,
//                12.3
//            )
//        )
//        dogList.add(
//            Dog(
//                4, 1, "San Bernardo", "Toy", 5.4,
//                6.7, "", "12 - 15", "", 10.5,
//                12.3
//            )
//        )
//        dogList.add(
//            Dog(
//                5, 1, "Husky", "Toy", 5.4,
//                6.7, "", "12 - 15", "", 10.5,
//                12.3
//            )
//        )
//        dogList.add(
//            Dog(
//                6, 1, "Xoloscuincle", "Toy", 5.4,
//                6.7, "", "12 - 15", "", 10.5,
//                12.3
//            )
//        )
//        return dogList
//    }
}