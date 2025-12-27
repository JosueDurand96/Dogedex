package com.durand.dogedex.ui.user_fragment.my_can_lost

import android.Manifest
import android.R
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
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
import androidx.navigation.fragment.findNavController
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
import com.google.android.gms.maps.model.Marker
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
    private var selectedMarker: Marker? = null
    private var selectedLocation: LatLng? = null

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

        // Configurar adapters para dropdowns
        setupDropdowns()
        
        // Configurar botones de foto
        binding.btnTomarFoto.setOnClickListener {
            openCamera()
        }
        binding.btnSubirFoto.setOnClickListener {
            openGallery()
        }
        
        // Configurar botones de ubicación
        binding.btnUsarMiUbicacion.setOnClickListener {
            getCurrentPosition()
        }
        binding.btnMarcarEnMapa.setOnClickListener {
            // El mapa ya permite hacer clic, solo mostrar instrucciones
            Toast.makeText(
                requireContext(),
                "Toca en el mapa para seleccionar la ubicación",
                Toast.LENGTH_SHORT
            ).show()
        }
        
        // Configurar click listener para el ImageView de foto (alternativa)
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
            
            // Av. República de Panamá 3055
            val idUsuarioValue = idUsuario ?: -1L
            if (idUsuarioValue == -1L) {
                Toast.makeText(
                    requireContext(),
                    "Error: No se pudo obtener el ID de usuario",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Obtener tamaño seleccionado de RadioButtons
            val checkedId = binding.tamanioRadioGroup.checkedRadioButtonId
            val tamanio = when (checkedId) {
                com.durand.dogedex.R.id.radioPequeño -> "Pequeño"
                com.durand.dogedex.R.id.radioMediano -> "Mediano"
                com.durand.dogedex.R.id.radioGrande -> "Grande"
                else -> ""
            }
            
            // Obtener coordenadas si hay una ubicación seleccionada
            val latitud = selectedLocation?.latitude
            val longitud = selectedLocation?.longitude
            
            // Crear el request con todos los campos
            val request = RegisterCanPerdidoRequest(
                nombre = binding.nombreTextInputEditText.text.toString().trim(),
                especie = binding.especieAutoCompleteTextView.text.toString().trim(),
                genero = binding.generoAutoCompleteTextView.text.toString().trim(),
                raza = binding.razaAutoCompleteTextView.text.toString().trim(),
                tamanio = tamanio,
                caracter = binding.caracterAutoCompleteTextView.text.toString().trim(),
                color = binding.colorAutoCompleteTextView.text.toString().trim(),
                pelaje = binding.pelajeAutoCompleteTextView.text.toString().trim(),
                foto = fotoBase64,
                idUsuario = idUsuarioValue.toInt(),
                nombreUsuario = binding.nombreUsuarioTextInputEditText.text.toString().trim(),
                apellidoUsuario = binding.apellidoUsuarioTextInputEditText.text.toString().trim(),
                fechaPerdida = date,
                lugarPerdida = binding.lugarPerdidaTextInputEditText.text.toString().trim(),
                comentario = binding.comentarioTextInputEditText.text.toString().trim(),
                latitud = latitud,
                longitud = longitud
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
                // Redirigir a la pantalla de mascotas perdidas
                findNavController().navigate(com.durand.dogedex.R.id.action_nav_my_can_lost_to_nav_can_report_lost)
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
        
        // Habilitar controles de zoom y desplazamiento
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isScrollGesturesEnabled = true
        mMap.uiSettings.isRotateGesturesEnabled = true
        mMap.uiSettings.isTiltGesturesEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        
        // Habilitar el botón de ubicación actual
        try {
            mMap.isMyLocationEnabled = true
        } catch (e: SecurityException) {
            Log.e("CanPerdidoFragment", "Error al habilitar ubicación: ${e.message}")
        }
        
        // Configurar ubicación inicial (Lima, Perú)
        val defaultLocation = LatLng(-12.0464, -77.0428)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 13f))
        
        // Permitir al usuario hacer clic en el mapa para seleccionar ubicación
        mMap.setOnMapClickListener { latLng ->
            // Eliminar marcador anterior si existe
            selectedMarker?.remove()
            
            // Agregar nuevo marcador en la ubicación seleccionada
            selectedMarker = mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Ubicación seleccionada")
                    .draggable(true) // Permitir arrastrar el marcador
            )
            
            selectedLocation = latLng
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            
            // Actualizar el campo de texto con la dirección
            updateLocationText(latLng)
        }
        
        // Permitir arrastrar el marcador
        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {}
            
            override fun onMarkerDrag(marker: Marker) {}
            
            override fun onMarkerDragEnd(marker: Marker) {
                selectedLocation = marker.position
                updateLocationText(marker.position)
            }
        })
        
        // Obtener ubicación actual si hay permisos
        checkPermissions()
    }
    
    private fun updateLocationText(latLng: LatLng) {
        try {
            if (Geocoder.isPresent()) {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                
                // Usar getFromLocation de forma asíncrona si es necesario
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                
                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val addressText = address.getAddressLine(0) ?: "${latLng.latitude}, ${latLng.longitude}"
                    binding.lugarPerdidaTextInputEditText.setText(addressText)
                } else {
                    // Si no se puede obtener la dirección, mostrar coordenadas
                    binding.lugarPerdidaTextInputEditText.setText("${latLng.latitude}, ${latLng.longitude}")
                }
            } else {
                // Si Geocoder no está disponible, mostrar coordenadas
                binding.lugarPerdidaTextInputEditText.setText("${latLng.latitude}, ${latLng.longitude}")
            }
        } catch (e: Exception) {
            Log.e("CanPerdidoFragment", "Error al obtener dirección: ${e.message}")
            // Si hay error, mostrar coordenadas
            binding.lugarPerdidaTextInputEditText.setText("${latLng.latitude}, ${latLng.longitude}")
        }
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
            // Ocultar el placeholder cuando hay una imagen
            hidePlaceholder()
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
            // Ocultar el placeholder cuando hay una imagen
            hidePlaceholder()
        }
    }
    
    private fun hidePlaceholder() {
        binding.root.findViewById<android.widget.ImageView>(com.durand.dogedex.R.id.placeholderIcon)?.visibility = android.view.View.GONE
        binding.root.findViewById<android.widget.TextView>(com.durand.dogedex.R.id.placeholderText)?.visibility = android.view.View.GONE
    }
    
    private fun showPlaceholder() {
        binding.root.findViewById<android.widget.ImageView>(com.durand.dogedex.R.id.placeholderIcon)?.visibility = android.view.View.VISIBLE
        binding.root.findViewById<android.widget.TextView>(com.durand.dogedex.R.id.placeholderText)?.visibility = android.view.View.VISIBLE
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

            // Eliminar marcador anterior si existe
            selectedMarker?.remove()
            
            // Agregar marcador en la ubicación actual
            selectedMarker = mMap.addMarker(
                MarkerOptions()
                    .position(currentLatLng)
                    .title("Tu ubicación actual")
                    .draggable(true) // Permitir arrastrar el marcador
            )
            
            selectedLocation = currentLatLng
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
            
            // Actualizar el campo de texto con la dirección
            updateLocationText(currentLatLng)
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

    private fun setupDropdowns() {
        // Adapter para Especie
        val itemsEspecie = listOf("Perro", "Gato", "Otro")
        val adapterEspecie = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            itemsEspecie
        )
        binding.especieAutoCompleteTextView.setAdapter(adapterEspecie)
        
        // Adapter para Género
        val itemsGenero = listOf("Macho", "Hembra")
        val adapterGenero = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            itemsGenero
        )
        binding.generoAutoCompleteTextView.setAdapter(adapterGenero)
        
        // Adapter para Raza
        val itemsRaza = listOf(
            "Labrador",
            "Pugs",
            "Pitbull",
            "Mestizo",
            "Doberman",
            "Boxer",
            "Rottweiler",
            "Staffordshire Bullterrier",
            "Gran Danés",
            "Bullmastiff",
            "American Staffordshire Terrier",
            "Pastor Alemán",
            "Chihuahua",
            "Bulldog",
            "Otro"
        )
        val adapterRaza = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            itemsRaza
        )
        binding.razaAutoCompleteTextView.setAdapter(adapterRaza)
        
        // Adapter para Color
        val itemsColor = listOf("Negro", "Blanco", "Caramelo", "Mostaza", "Marrón", "Gris", "Multicolor", "Otro")
        val adapterColor = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            itemsColor
        )
        binding.colorAutoCompleteTextView.setAdapter(adapterColor)
        
        // Adapter para Carácter
        val itemsCaracter = listOf("Tímido", "Sociable", "Pasivo", "Agresivo", "Independiente")
        val adapterCaracter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            itemsCaracter
        )
        binding.caracterAutoCompleteTextView.setAdapter(adapterCaracter)
        
        // Adapter para Pelaje
        val itemsPelaje = listOf("Duro", "Rizado", "Corto", "Largo")
        val adapterPelaje = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            itemsPelaje
        )
        binding.pelajeAutoCompleteTextView.setAdapter(adapterPelaje)
    }
    
    private fun validateForm(): Boolean {
        val tamanioSelected = binding.tamanioRadioGroup.checkedRadioButtonId != -1
        return binding.nombreTextInputEditText.text.toString().trim().isNotEmpty() &&
                binding.especieAutoCompleteTextView.text.toString().trim().isNotEmpty() &&
                binding.generoAutoCompleteTextView.text.toString().trim().isNotEmpty() &&
                binding.razaAutoCompleteTextView.text.toString().trim().isNotEmpty() &&
                tamanioSelected &&
                binding.caracterAutoCompleteTextView.text.toString().trim().isNotEmpty() &&
                binding.colorAutoCompleteTextView.text.toString().trim().isNotEmpty() &&
                binding.pelajeAutoCompleteTextView.text.toString().trim().isNotEmpty() &&
                binding.nombreUsuarioTextInputEditText.text.toString().trim().isNotEmpty() &&
                binding.apellidoUsuarioTextInputEditText.text.toString().trim().isNotEmpty() &&
                binding.lugarPerdidaTextInputEditText.text.toString().trim().isNotEmpty()
                // Comentario es opcional, no se valida
    }

}

inline fun Fragment.safeCollectInViews(crossinline c: suspend CoroutineScope.() -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            this.c()
        }
    }
}