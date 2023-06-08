package com.durand.projecttesisupc.di.fragment.user.register_hocico

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.durand.projecttesisupc.databinding.FragmentRegisterHocicoBinding

class RegisterHocicoFragment : Fragment() {

    private var _binding: FragmentRegisterHocicoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val REQUEST_CODE = 200
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val registerHocicoViewModel =
            ViewModelProvider(this).get(RegisterHocicoViewModel::class.java)

        _binding = FragmentRegisterHocicoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        _binding!!.hocicoImageView.setOnClickListener {
            capturePhoto()
        }
        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null){
            _binding!!.hocicoImageView.setImageBitmap(data.extras!!.get("data") as Bitmap)
        }
    }


    fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CODE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}