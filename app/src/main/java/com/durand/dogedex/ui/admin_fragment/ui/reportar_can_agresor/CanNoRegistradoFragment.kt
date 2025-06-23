package com.durand.dogedex.ui.admin_fragment.ui.reportar_can_agresor

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.durand.dogedex.data.request.AddAgressionPetRequest
import com.durand.dogedex.databinding.FragmentCanNoRegistradoBinding
import android.app.Activity
import android.graphics.Bitmap
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import coil.load
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

class CanNoRegistradoFragment : Fragment() {
    private var _binding: FragmentCanNoRegistradoBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddCanAgresorViewModel

    override fun onStart() {
        super.onStart()
        binding.secondLinearLayout.visibility = View.GONE
        binding.firstLinearLayout.visibility = View.VISIBLE
    }

    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(AddCanAgresorViewModel::class.java)
        _binding = FragmentCanNoRegistradoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val itemsTamano = listOf("Pequeño", "Grande", "Mediano")
        val adapterTamano = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, itemsTamano)
        _binding!!.tamanoAutoCompleteTextView.setAdapter(adapterTamano)

        val itemsColorDog = listOf("Negro", "Blanco", "Caramelo", "Mostaza", "Otro")
        val adapterColorDog = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, itemsColorDog)
        _binding!!.colorCanAutoCompleteTextView.setAdapter(adapterColorDog)

        val itemsRaza = listOf("Labrador", "Pugs", "Pitbull", "Mestizo")
        val adapterRaza = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, itemsRaza)
        _binding!!.razaCaninaAutoCompleteTextView.setAdapter(adapterRaza)


        binding.registrarAppCompatButton.setOnClickListener {
            viewModel.startReportCanesRegistrados(AddAgressionPetRequest(
                "",
                "",
                "",
                "",
                "",
                "",
                "","",0,1,"","","",0,""
            ))
        }
        binding.photoImageView.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(requireContext())
            pictureDialog.setTitle("Select Action")
            val pictureDialogItem = arrayOf("Select photo from Gallery",
                "Capture photo from Camera")
            pictureDialog.setItems(pictureDialogItem) { dialog, which ->

                when (which) {
                    0 -> gallery()
                    1 -> camera()
                }
            }

            pictureDialog.show()
        }

        viewModel.addCanNoRegistrado.observe(requireActivity()){
            Log.d("josue", "SE REGISTRO CORRECTAMENTE!")
            binding.secondLinearLayout.visibility = View.VISIBLE
            binding.firstLinearLayout.visibility = View.GONE
            binding.codAgressionAppCompatTextView.text = "#AGRE2023-"+it.idAgresion
        }


        return root
    }


    private fun galleryCheckPermission() {

        Dexter.withContext(requireContext()).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(
                    requireContext(),
                    "You have denied the storage permission to select image",
                    Toast.LENGTH_SHORT
                ).show()
                showRotationalDialogForPermission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?, p1: PermissionToken?) {
                showRotationalDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun cameraCheckPermission() {

        Dexter.withContext(requireContext())
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA).withListener(

                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {

                            if (report.areAllPermissionsGranted()) {
                                camera()
                            }

                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?) {
                        showRotationalDialogForPermission()
                    }

                }
            ).onSameThread().check()
    }


    private fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {

                CAMERA_REQUEST_CODE -> {

                    val bitmap = data?.extras?.get("data") as Bitmap

                    //we are using coroutine image loader (coil)
                    binding.photoImageView.load(bitmap) {
                        crossfade(false)
                        crossfade(1000)
                    }
                }

                GALLERY_REQUEST_CODE -> {

                    binding.photoImageView.load(data?.data) {
                        crossfade(false)
                        crossfade(1000)
                    }

                }
            }

        }

    }

    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(requireContext())
            .setMessage("It looks like you have turned off permissions"
                    + "required for this feature. It can be enable under App settings!!!")

            .setPositiveButton("Go TO SETTINGS") { _, _ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", requireActivity().packageName, null)
                    intent.data = uri
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }

            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}