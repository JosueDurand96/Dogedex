package com.durand.dogedex.ui.forget_password

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.durand.dogedex.databinding.ActivityWholeImageBinding
import java.io.File

class WholeImageActivity : AppCompatActivity() {

    companion object{
        const val PHOTO_URI_KEY = "photo_uri"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWholeImageBinding.inflate(layoutInflater)
        setContentView(binding.root )

        val uri = intent.extras?.getString(PHOTO_URI_KEY) ?: ""
        val photoUri = Uri.parse(uri)

        if (photoUri == null){
            Toast.makeText(this, "Error al mostrar la imagen",Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        binding.wholeImage.load(File(photoUri.path))

    }
}