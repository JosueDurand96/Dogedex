package com.durand.dogedex.ui.dogdetail

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.durand.dogedex.R
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.response.Dog
import com.durand.dogedex.databinding.ActivityDogDetailBinding


class DogDetailActivity : AppCompatActivity() {

    companion object {
        const val DOG_KEY = "dog"
        const val IS_RECOGNITION_KEY = "is_recognition"
    }

    private val viewModel: DogDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dog = intent?.extras?.getParcelable<Dog>(DOG_KEY)
        val isRecognition = intent?.extras?.getBoolean(IS_RECOGNITION_KEY, false) ?: false

        val nombreMacota = intent?.extras?.getString("nombreMacota")
        val fechaNacimiento = intent?.extras?.getString("fechaNacimiento")
        val especie = intent?.extras?.getString("especie")
        val genero = intent?.extras?.getString("genero")
        val raza = intent?.extras?.getString("raza")
        val tamano = intent?.extras?.getString("tamano")
        val caracter = intent?.extras?.getString("caracter")
        val color = intent?.extras?.getString("color")
        val pelaje = intent?.extras?.getString("pelaje")
        val esterelizado = intent?.extras?.getString("esterelizado")
        val distrito = intent?.extras?.getString("distrito")
        val modoObtencion = intent?.extras?.getString("modoObtencion")
        val razonTenencia = intent?.extras?.getString("razonTenencia")
//        Log.d("josue","name: "+dog!!.name)
 //       Log.d("josue","imageUrl: "+dog!!.imageUrl)

//        Log.d("josue", "nombreMacota: $nombreMacota")
//        Log.d("josue", "fechaNacimiento: $fechaNacimiento")
//        Log.d("josue", "especie: $especie")
//        Log.d("josue", "genero: $genero")
//
//        Log.d("josue", "raza: $raza")
//        Log.d("josue", "tamano: $tamano")
//        Log.d("josue", "caracter: $caracter")
//        Log.d("josue", "color: $color")
//        Log.d("josue", "pelaje: $pelaje")
//        Log.d("josue", "esterelizado: $esterelizado")
//        Log.d("josue", "distrito: $distrito")
//        Log.d("josue", "modoObtencion: $modoObtencion")
//        Log.d("josue", "razonTenencia: $razonTenencia")

        if (dog!!.name == "Doberman") {
            binding.canDangerousTextView.visibility = View.VISIBLE
            binding.canNoDangerousTextView.visibility = View.GONE
        } else if (dog!!.name == "Boxer") {
            binding.canDangerousTextView.visibility = View.VISIBLE
            binding.canNoDangerousTextView.visibility = View.GONE
        } else if (dog!!.name == "Rottweiler") {
            binding.canDangerousTextView.visibility = View.VISIBLE
            binding.canNoDangerousTextView.visibility = View.GONE
        } else if (dog!!.name == "staffordshire_bullterrier") {
            binding.canDangerousTextView.visibility = View.VISIBLE
            binding.canNoDangerousTextView.visibility = View.GONE
        } else if (dog!!.name == "great_dane") {
            binding.canDangerousTextView.visibility = View.VISIBLE
            binding.canNoDangerousTextView.visibility = View.GONE
        } else if (dog!!.name == "Bullmastiff") {
            binding.canDangerousTextView.visibility = View.VISIBLE
            binding.canNoDangerousTextView.visibility = View.GONE
        } else if(dog.name == "American Staffordshire Terrier") {
            binding.dogNameText.text = "American Staffordshire Terrier"
            binding.canDangerousTextView.visibility = View.VISIBLE
            binding.canNoDangerousTextView.visibility = View.GONE
        } else if(dog.name == "Weimaraner"){
            binding.dogNameText.text = "Gran danés"
            binding.canDangerousTextView.visibility = View.VISIBLE
            binding.canNoDangerousTextView.visibility = View.GONE
        }else if (dog.name == "Pinscher Miniatura"){
            binding.dogNameText.text = "Rottweiler"
            binding.canDangerousTextView.visibility = View.VISIBLE
            binding.canNoDangerousTextView.visibility = View.GONE
        } else {
            binding.canDangerousTextView.visibility = View.GONE
            binding.canNoDangerousTextView.visibility = View.VISIBLE
        }

        if (dog == null) {
            Toast.makeText(this, R.string.error_showing_dog_not_found, Toast.LENGTH_SHORT)
                .show()
            finish()
            return
        }

        viewModel.status.observe(this) { status ->

            when (status) {
                is ApiResponseStatus.Error -> {
                    binding.loadingWheel.visibility = View.GONE
                    Toast.makeText(this, status.message, Toast.LENGTH_SHORT).show()
                }
                is ApiResponseStatus.Loading -> binding.loadingWheel.visibility = View.VISIBLE
                is ApiResponseStatus.Success -> {
                    binding.loadingWheel.visibility = View.GONE
                    finish()
                }
            }
        }
        val sharedPreferences = getSharedPreferences("fotoKey", Context.MODE_PRIVATE)
        val photo = sharedPreferences.getString("foto","")
        Log.d("fotoDoG","DOG: $photo")
        val decodedString: ByteArray = Base64.decode(photo, Base64.DEFAULT)
        val decodedByte: Bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

        binding.dogIndex.text = getString(R.string.dog_index_format, dog.index)
        binding.fechaTextView.text = "Distrito: $distrito"
        binding.caracterTextView.text = "Caracter: $caracter"
        binding.esterelizadoTextView.text = esterelizado
        binding.generoTextView.text = genero
        binding.colorTextView.text = color
        binding.especieTextView.text = especie
        binding.nombreMacotaTextView.text = nombreMacota
        binding.dog = dog
        binding.dogImage.setImageBitmap(decodedByte)
        binding.closeButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            finish()
        }
    }
}