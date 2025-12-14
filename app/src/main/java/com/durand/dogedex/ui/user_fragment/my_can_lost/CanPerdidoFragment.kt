package com.durand.dogedex.ui.user_fragment.my_can_lost

import android.Manifest
import android.R
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.InputStream
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.durand.dogedex.data.request.oficial.RegisterCanPerdidoRequest
import com.durand.dogedex.databinding.FragmentCanPerdidoBinding
import com.durand.dogedex.util.LocationsUtils
import com.durand.dogedex.util.LocationsUtils.Companion.locationFlow
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CanPerdidoFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentCanPerdidoBinding? = null
    private lateinit var mMap: GoogleMap
    private val binding get() = _binding!!
    private lateinit var viewModel: CanPerdidoViewModel
    private var idUsuario: Long? = 0
    private var fotoUri: Uri? = null
    private var fotoBitmap: Bitmap? = null

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(CanPerdidoViewModel::class.java)
        _binding = FragmentCanPerdidoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            // Deshabilitar el botón mientras se procesa
            binding.confirmAppCompatButton.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessageResId ->
            errorMessageResId?.let {
                Toast.makeText(
                    requireContext(),
                    it,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        val sharedPref = activity?.getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
        idUsuario = sharedPref?.getLong("idUsuario", -1) // -1 es el valor por defecto si no existe

        // Configurar click listener para el ImageView de foto
        binding.fotoImageView.setOnClickListener {
            showImagePickerDialog()
        }

        binding.confirmAppCompatButton.setOnClickListener {
            // Validar que todos los campos estén completos
            if (!validateForm()) {
                Toast.makeText(
                    requireContext(),
                    "Por favor, completa todos los campos requeridos",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Validar que haya una foto
            if (fotoUri == null && fotoBitmap == null) {
                Toast.makeText(
                    requireContext(),
                    "Por favor, selecciona o toma una foto",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Convertir imagen a Base64
            val fotoBase64 = convertImageToBase64()
            if (fotoBase64 == null) {
                Toast.makeText(
                    requireContext(),
                    "Error al procesar la imagen",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val date: String = obtenerFechaHoraActual()
            
            // Obtener idUsuario de SharedPreferences
            val idUsuarioValue = idUsuario ?: -1L
            if (idUsuarioValue == -1L) {
                Toast.makeText(
                    requireContext(),
                    "Error: No se pudo obtener el ID de usuario",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Crear el request con todos los campos
            val request = RegisterCanPerdidoRequest(
                nombre = binding.nombreTextInputEditText.text.toString().trim(),
                especie = binding.especieTextInputEditText.text.toString().trim(),
                genero = binding.generoTextInputEditText.text.toString().trim(),
                raza = binding.razaTextInputEditText.text.toString().trim(),
                tamanio = binding.tamanioTextInputEditText.text.toString().trim(),
                caracter = binding.caracterTextInputEditText.text.toString().trim(),
                color = binding.colorTextInputEditText.text.toString().trim(),
                pelaje = binding.pelajeTextInputEditText.text.toString().trim(),
                foto = fotoBase64,
                idUsuario = idUsuarioValue.toInt(),
                nombreUsuario = binding.nombreUsuarioTextInputEditText.text.toString().trim(),
                apellidoUsuario = binding.apellidoUsuarioTextInputEditText.text.toString().trim(),
                fechaPerdida = date,
                lugarPerdida = binding.lugarPerdidaTextInputEditText.text.toString().trim(),
                comentario = binding.comentarioTextInputEditText.text.toString().trim()
            )

            viewModel.listar(request)
        }
        viewModel.list.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                Log.d("CanPerdidoFragment", "Registro exitoso: ${response}")
                Toast.makeText(
                    requireContext(),
                    "Mascota perdida registrada exitosamente",
                    Toast.LENGTH_SHORT
                ).show()
                // Opcional: limpiar el formulario o navegar a otra pantalla
            }
        }
        return root
    }
    fun obtenerFechaHoraActual(): String {
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return formato.format(Date())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
        val mapFragment = childFragmentManager.findFragmentById(com.durand.dogedex.R.id.map) as? com.google.android.gms.maps.SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-12.014190, -77.030687)
        mMap.addMarker(MarkerOptions().position(sydney).title("Hola"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Suppress("DEPRECATION")
    private val locationRequest: LocationRequest by lazy {
        LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 500
            fastestInterval = 250
            maxWaitTime = 500
            numUpdates = 3
        }
    }

    private val requestPermissionLocation = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: Map<String, Boolean> ->
        val permissionDenied = permissions.entries.find { !it.value }
        if (permissionDenied == null) {
            getCurrentPosition()
        } else {
            // NO PERMISSION
        }
    }

    private val requestActiveGPS = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                getCurrentPosition()
            } else {
                activeGPS()
            }
        }

    // Launcher para seleccionar imagen de la galería
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            fotoUri = it
            fotoBitmap = null
            binding.fotoImageView.load(it) {
                crossfade(true)
            }
        }
    }

    // URI temporal para la foto de la cámara
    private var cameraImageUri: Uri? = null

    // Launcher para tomar foto con la cámara
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && cameraImageUri != null) {
            fotoUri = cameraImageUri
            fotoBitmap = null
            binding.fotoImageView.load(cameraImageUri) {
                crossfade(true)
            }
        }
    }

    // Launcher para permisos de cámara
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Crear URI temporal para la foto
            val photoFile = File.createTempFile(
                "IMG_${System.currentTimeMillis()}",
                ".jpg",
                requireContext().cacheDir
            )
            cameraImageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )
            cameraLauncher.launch(cameraImageUri)
        } else {
            Toast.makeText(
                requireContext(),
                "Se necesita permiso de cámara para tomar fotos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun activeGPS() {
        LocationsUtils.activeGPS(
            locationRequest = locationRequest,
            activity = requireActivity(),
            launchIntentSenderRequest = {
                requestActiveGPS.launch(it)
            }
        )
    }

    private fun getCurrentPosition() {
        if (LocationsUtils.isGPSActive(requireActivity())) {
            callBackFlowCollect()
        } else {
            activeGPS()
        }
    }

    private fun callBackFlowCollect() = safeCollectInViews {
        val location: Location? = fusedLocationClient.locationFlow(locationRequest)
            .firstOrNull { it != null }

        location?.let {
            val currentLatLng = LatLng(it.latitude, it.longitude)

            mMap.clear() // Limpia otros marcadores
            mMap.addMarker(MarkerOptions().position(currentLatLng).title("Tu ubicación"))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
          //  vm.setCoordinates(latitude = it.latitude.toString(), longitude = it.longitude.toString())
        }
    }

    private fun checkPermissions() {
        val hasPermissions = LocationsUtils.isCurrentPositionAllowed(
            context = requireContext(),
            launchPermissions = {
                requestPermissionLocation.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        )
        if (hasPermissions) getCurrentPosition()
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Tomar foto", "Seleccionar de galería")
        AlertDialog.Builder(requireContext())
            .setTitle("Seleccionar imagen")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun openCamera() {
        // Verificar permiso de cámara
        val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            // Crear URI temporal para la foto
            val photoFile = File.createTempFile(
                "IMG_${System.currentTimeMillis()}",
                ".jpg",
                requireContext().cacheDir
            )
            cameraImageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )
            cameraLauncher.launch(cameraImageUri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun convertImageToBase64(): String? {
        return try {
            val bitmap: Bitmap? = when {
                fotoBitmap != null -> fotoBitmap
                fotoUri != null -> {
                    val inputStream: InputStream? = requireContext().contentResolver.openInputStream(fotoUri!!)
                    BitmapFactory.decodeStream(inputStream)
                }
                else -> null
            }

            bitmap?.let {
                val byteArrayOutputStream = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                Base64.encodeToString(byteArray, Base64.DEFAULT)
            }
        } catch (e: Exception) {
            Log.e("CanPerdidoFragment", "Error al convertir imagen a Base64: ${e.message}", e)
            null
        }
    }

    private fun validateForm(): Boolean {
        return binding.nombreTextInputEditText.text.toString().trim().isNotEmpty() &&
                binding.especieTextInputEditText.text.toString().trim().isNotEmpty() &&
                binding.generoTextInputEditText.text.toString().trim().isNotEmpty() &&
                binding.razaTextInputEditText.text.toString().trim().isNotEmpty() &&
                binding.tamanioTextInputEditText.text.toString().trim().isNotEmpty() &&
                binding.caracterTextInputEditText.text.toString().trim().isNotEmpty() &&
                binding.colorTextInputEditText.text.toString().trim().isNotEmpty() &&
                binding.pelajeTextInputEditText.text.toString().trim().isNotEmpty() &&
                binding.nombreUsuarioTextInputEditText.text.toString().trim().isNotEmpty() &&
                binding.apellidoUsuarioTextInputEditText.text.toString().trim().isNotEmpty() &&
                binding.lugarPerdidaTextInputEditText.text.toString().trim().isNotEmpty() &&
                binding.comentarioTextInputEditText.text.toString().trim().isNotEmpty()
    }

}

inline fun Fragment.safeCollectInViews(crossinline c: suspend CoroutineScope.() -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            this.c()
        }
    }
}