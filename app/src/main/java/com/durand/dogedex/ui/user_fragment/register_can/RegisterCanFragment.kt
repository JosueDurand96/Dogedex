package com.durand.dogedex.ui.user_fragment.register_can

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.util.Size
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.request.oficial.RegisterCanRequest
import com.durand.dogedex.data.response.Dog
import com.durand.dogedex.databinding.FragmentRegisterCanBinding
import com.durand.dogedex.domain.Classifier
import com.durand.dogedex.domain.DogRecognition
import com.durand.dogedex.ui.auth.LoginActivity
import com.durand.dogedex.ui.dogdetail.DogDetailActivity
import com.durand.dogedex.ui.forget_password.MainViewModel
import com.durand.dogedex.util.LABEL_PATH
import com.durand.dogedex.util.MODEL_PATH
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.common.FileUtil
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class RegisterCanFragment : Fragment() {

    private var _binding: FragmentRegisterCanBinding? = null
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private var isCameraReady = false
    private lateinit var classifier: Classifier
    private val viewModel: MainViewModel by viewModels()
    private lateinit var registerCanViewModel: RegisterCanViewModel
    private var lastLocation: Location? = null
    private lateinit var especie: String
    private lateinit var genero: String
    private lateinit var raza: String
    private lateinit var tamano: String
    private lateinit var caracter: String
    private lateinit var color: String

    private lateinit var pelaje: String
    private lateinit var esterelizado: String
    private lateinit var distrito: String
    private lateinit var modoObtencion: String
    private lateinit var razonTenencia: String
    private lateinit var photo: Bitmap
    private lateinit var imageCan: String
    private lateinit var dogCan: Dog
    private var isRegistrationComplete = false

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                setupCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    com.durand.dogedex.R.string.camera_permission,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // This property is only valid between onCreateView and
    private val binding get() = _binding!!
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var idUsuario: Long? = 0

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        registerCanViewModel = ViewModelProvider(this).get(RegisterCanViewModel::class.java)
        _binding = FragmentRegisterCanBinding.inflate(inflater, container, false)
        registerCanViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            // Deshabilitar el botón mientras se procesa la petición
            if (isLoading) {
                binding.confirmAppCompatButton.isEnabled = false
            } else {
                // Cuando termine la petición, verificar si el formulario está completo para habilitar el botón
                checkFormAndEnableButton()
            }
        }
        // Obtener idUsuario guardado en LoginFragment
        val sharedPref = activity?.getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
        idUsuario = sharedPref?.getLong("idUsuario", -1) // -1 es el valor por defecto si no existe
        Log.d("RegisterCanFragment", "idUsuario obtenido de SharedPreferences: $idUsuario")
        
        // Validar que el idUsuario sea válido
        if (idUsuario == null || idUsuario == -1L) {
            Log.e("RegisterCanFragment", "ERROR: idUsuario no válido o no encontrado en SharedPreferences")
            Toast.makeText(
                requireContext(),
                "Error: Usuario no autenticado. Por favor, inicia sesión nuevamente.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Log.d("RegisterCanFragment", "idUsuario válido: $idUsuario (será convertido a Int: ${idUsuario!!.toInt()})")
        }

        viewModel.dog.observe(requireActivity()) { dog ->
            if (dog != null) {
            dogCan = dog
                Log.d("RegisterCanFragment", "viewModel.dog recibido - isRegistrationComplete: $isRegistrationComplete, imageCan inicializado: ${::imageCan.isInitialized}")
                
                // Si ya se registró exitosamente, navegar a DogDetailActivity
                // Similar a RegisterHocicoFragment
                if (isRegistrationComplete) {
                    if (::imageCan.isInitialized) {
                        Log.d("RegisterCanFragment", "Navegando a DogDetailActivity después del registro exitoso desde observer de viewModel.dog")
                        // Usar post para asegurar que se ejecute en el hilo principal
                        binding.root.post {
                            openDetailActivity(dog)
                        }
                    } else {
                        Log.e("RegisterCanFragment", "Error: imageCan no está inicializado cuando se intenta navegar")
                    }
                } else {
                    Log.d("RegisterCanFragment", "No se navega aún - isRegistrationComplete: $isRegistrationComplete (guardando dogCan para uso posterior)")
                }
            }
        }
        
        // Observar también el status para detectar cuando se obtiene el perro exitosamente
        viewModel.status.observe(requireActivity()) { status ->
            Log.d("RegisterCanFragment", "viewModel.status recibido: ${status.javaClass.simpleName}, isRegistrationComplete: $isRegistrationComplete")
            if (status is ApiResponseStatus.Success && isRegistrationComplete) {
                // Si el status es Success y ya se registró, verificar si tenemos el perro
                if (::dogCan.isInitialized && ::imageCan.isInitialized) {
                    Log.d("RegisterCanFragment", "Status Success después del registro, navegando a DogDetailActivity desde observer de status")
                    binding.root.post {
                        openDetailActivity(dogCan)
                    }
                } else {
                    Log.e("RegisterCanFragment", "Status Success pero dogCan o imageCan no están inicializados - dogCan: ${::dogCan.isInitialized}, imageCan: ${::imageCan.isInitialized}")
                }
            }
        }

        registerCanViewModel.list.observe(viewLifecycleOwner) { response ->
            Log.d("RegisterCanFragment", "Respuesta recibida - Código: '${response.codigo}' (tipo: ${response.codigo.javaClass.simpleName}), Mensaje: '${response.mensaje}'")
            
            // Verificar que la respuesta sea exitosa (código 201 o 200)
            // El código puede venir como string "201" o como número 201
            val codigoStr = response.codigo.trim()
            val codigoInt = codigoStr.toIntOrNull()
            val isSuccess = codigoStr == "201" || codigoStr == "200" || 
                           codigoInt == 201 || codigoInt == 200 ||
                           response.mensaje.contains("registrado correctamente", ignoreCase = true) ||
                           response.mensaje.contains("exitoso", ignoreCase = true)
            
            if (isSuccess) {
                Log.d("RegisterCanFragment", "Registro exitoso - Código: ${response.codigo}, Mensaje: ${response.mensaje}")
                isRegistrationComplete = true
                Log.d("RegisterCanFragment", "isRegistrationComplete establecido a true")
                
                // Después de registrar exitosamente, obtener el perro usando el reconocimiento
                // Similar a RegisterHocicoFragment
                if (lastDogRecognition != null && lastDogRecognition!!.confidence > 80.0) {
                    Log.d("RegisterCanFragment", "Obteniendo perro con ID: ${lastDogRecognition!!.id}")
                    
                    // Si ya tenemos el perro guardado (dogCan), navegar directamente
                    if (::dogCan.isInitialized && ::imageCan.isInitialized) {
                        Log.d("RegisterCanFragment", "Ya tenemos dogCan, navegando directamente a DogDetailActivity")
                        binding.root.post {
                            openDetailActivity(dogCan)
                        }
                    } else {
                        // Si no tenemos el perro, obtenerlo usando el ID del reconocimiento
                        // El observer de viewModel.dog se activará cuando se obtenga el perro
                        viewModel.getDogByMlId(lastDogRecognition!!.id)
                    }
                } else {
                    Log.d("RegisterCanFragment", "No hay reconocimiento válido (lastDogRecognition: $lastDogRecognition), creando Dog básico")
                    // Si no hay reconocimiento válido, usar la raza del reconocimiento si existe, sino la del formulario
                    val razaParaDog = if (lastDogRecognition != null) {
                        val razaReconocida = extraerRazaDelReconocimiento(lastDogRecognition!!)
                        Log.d("RegisterCanFragment", "Usando raza del reconocimiento: $razaReconocida (confianza: ${lastDogRecognition!!.confidence}%)")
                        razaReconocida
                    } else {
                        Log.d("RegisterCanFragment", "No hay reconocimiento, usando raza del formulario: $raza")
                        raza
                    }
                    
                    // Si no hay reconocimiento, crear un Dog básico y navegar directamente
                    if (::imageCan.isInitialized) {
                        val dogToShow = Dog(
                            id = 0L,
                            index = 0,
                            name = razaParaDog,
                            type = especie,
                            heightFemale = "",
                            heightMale = "",
                            imageUrl = "",
                            lifeExpectancy = "",
                            temperament = caracter,
                            weightFemale = "",
                            weightMale = "",
                            inCollection = true
                        )
                        Log.d("RegisterCanFragment", "Navegando a DogDetailActivity con Dog básico - raza: $razaParaDog")
                        binding.root.post {
                            openDetailActivity(dogToShow)
                        }
                    } else {
                        Log.e("RegisterCanFragment", "Error: No se puede navegar - imageCan no está inicializado")
                        Toast.makeText(
                            requireContext(),
                            "Error: No se pudo capturar la imagen",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                // Si el código no es 201/200, mostrar error
                Log.e("RegisterCanFragment", "Error en el registro - Código: '${response.codigo}', Mensaje: '${response.mensaje}'")
                Toast.makeText(
                    requireContext(),
                    "Error al registrar: ${response.mensaje}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        //buttonRegistrarCan()
        requestCameraPermission()
        val itemsEspecie = listOf("Perro", "Gato", "Otro")
        val adapterEspecie =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, itemsEspecie)
        _binding!!.especieAutoCompleteTextView.setAdapter(adapterEspecie)
        val itemsGenero = listOf("Macho", "Hembra")
        val adapterGenero =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, itemsGenero)
        _binding!!.generoAutoCompleteTextView.setAdapter(adapterGenero)

        val itemsRazaCanina = listOf(
            "Labrador",
            "Pugs",
            "Pitbull",
            "Mestizo",
            "Doberman",
            "Boxer",
            "Rottweiler",
            "staffordshire bullterrier",
            "gran danes",
            "Bullmastiff",
            "American Staffordshire Terrier"
        )
        val adapterRazaCanina =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, itemsRazaCanina)
        _binding!!.razaCaninaAutoCompleteTextView.setAdapter(adapterRazaCanina)


        val itemsTamano = listOf("Pequeño", "Mediano", "Grande")
        val adapterTamano =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, itemsTamano)
        _binding!!.tamanoAutoCompleteTextView.setAdapter(adapterTamano)

        val itemsCaracter = listOf("Tímido", "Sociable", "Pasivo", "Agresivo", "Independiente")
        val adapterCaracter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, itemsCaracter)
        _binding!!.caracterAutoCompleteTextView.setAdapter(adapterCaracter)

        val itemsColorDog = listOf("Negro", "Blanco", "Caramelo", "Mostaza", "Otro")
        val adapterColorDog =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, itemsColorDog)
        _binding!!.colorCanAutoCompleteTextView.setAdapter(adapterColorDog)

        val itemsPelaje = listOf("Duro", "Rizado", "Corto", "Largo")
        val adapterPelaje =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, itemsPelaje)
        _binding!!.pelajeAutoCompleteTextView.setAdapter(adapterPelaje)

        val itemsEsterelizado = listOf("Si", "No")
        val adapterEsterelizado =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, itemsEsterelizado)
        _binding!!.esterilizadoAutoCompleteTextView.setAdapter(adapterEsterelizado)

        val itemsDistritoDondeReside = listOf(
            "Lima",
            "San Isidro",
            "Miraflores",
            "San Miguel",
            "San Borja",
            "Surco",
            "Jesus Maria",
            "Rimac",
            "SMP"
        )
        val adapterDistritoDondeReside = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_dropdown_item,
            itemsDistritoDondeReside
        )
        _binding!!.distritoDondeResideAutoCompleteTextView.setAdapter(adapterDistritoDondeReside)

        val itemsmodoDeObtencion =
            listOf("Recogido", "Reubicado", "Regalo", "Nacido en casa", "Compra")
        val adaptermodoDeObtencion = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_dropdown_item,
            itemsmodoDeObtencion
        )
        _binding!!.modoDeObtencionAutoCompleteTextView.setAdapter(adaptermodoDeObtencion)

        val itemsrazonDeTenencia = listOf(
            "Compañía",
            "Asistencia",
            "Terapia",
            "Trabajo",
            "Seguridad",
            "Deporte",
            "Exposición",
            "Reproducción",
            "Caza"
        )
        val adapterrazonDeTenencia = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_dropdown_item,
            itemsrazonDeTenencia
        )
        binding.lifecycleOwner = viewLifecycleOwner
        _binding!!.razonDeTenenciaAutoCompleteTextView.setAdapter(adapterrazonDeTenencia)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Configurar DatePickerDialog para el campo de fecha
        setupDatePicker()

        // Asegurar que el botón esté deshabilitado inicialmente
        binding.confirmAppCompatButton.isEnabled = false

        binding.especieAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            especie = selectedItem.toString()
            checkFormAndEnableButton()
        }

        binding.generoAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            genero = selectedItem.toString()
            checkFormAndEnableButton()
        }

        binding.razaCaninaAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            raza = selectedItem.toString()
            checkFormAndEnableButton()
        }

        binding.tamanoAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            tamano = selectedItem.toString()
            checkFormAndEnableButton()
        }

        binding.caracterAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            caracter = selectedItem.toString()
            checkFormAndEnableButton()
        }

        binding.colorCanAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            color = selectedItem.toString()
            checkFormAndEnableButton()
        }

        binding.pelajeAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            pelaje = selectedItem.toString()
            checkFormAndEnableButton()
        }

        binding.esterilizadoAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            esterelizado = selectedItem.toString()
            checkFormAndEnableButton()
        }

        binding.distritoDondeResideAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            distrito = selectedItem.toString()
            checkFormAndEnableButton()
        }

        binding.modoDeObtencionAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            modoObtencion = selectedItem.toString()
            checkFormAndEnableButton()
        }

        binding.razonDeTenenciaAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            razonTenencia = selectedItem.toString()
            checkFormAndEnableButton()
        }

        // Configurar listener inicial del botón
        binding.confirmAppCompatButton.setOnClickListener {
            // Validar formulario primero
            if (!validateForm()) {
                // Mostrar mensaje específico sobre qué campos faltan
                val missingFields = getMissingFields()
                val message = if (missingFields.isNotEmpty()) {
                    "Faltan completar los siguientes datos: $missingFields"
                } else {
                    "Por favor, completa todos los campos requeridos"
                }
                Toast.makeText(
                    requireContext(),
                    message,
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            
            // Validar que se haya capturado una imagen
            if (!::imageCan.isInitialized) {
                Toast.makeText(
                    requireContext(),
                    "Por favor, asegúrate de que se haya capturado una imagen del perro",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            
            // Si todo está completo, proceder enviando el formulario
            // El botón se deshabilitará automáticamente cuando isLoading sea true
            sendFormData()
        }

        // Validar formulario cuando cambien los campos
        setupFormValidation()

        return binding.root
    }

    private fun checkFormAndEnableButton() {
        // Asegurar que las modificaciones de UI se ejecuten en el hilo principal
        // Esto es necesario porque esta función puede ser llamada desde hilos de fondo (ej: analizador de cámara)
        lifecycleScope.launch(Dispatchers.Main) {
            val isValid = validateForm() && ::imageCan.isInitialized
            binding.confirmAppCompatButton.isEnabled = isValid
        }
    }

    private fun validateForm(): Boolean {
        return binding.namePetTextInputEditText.text.toString().trim().isNotEmpty()
                && binding.fechaTextInputEditText.text.toString().trim().isNotEmpty()
                && ::especie.isInitialized && especie.isNotEmpty()
                && ::genero.isInitialized && genero.isNotEmpty()
                && ::raza.isInitialized && raza.isNotEmpty()
                && ::tamano.isInitialized && tamano.isNotEmpty()
                && ::caracter.isInitialized && caracter.isNotEmpty()
                && ::color.isInitialized && color.isNotEmpty()
                && ::pelaje.isInitialized && pelaje.isNotEmpty()
                && ::esterelizado.isInitialized && esterelizado.isNotEmpty()
                && ::distrito.isInitialized && distrito.isNotEmpty()
                && ::modoObtencion.isInitialized && modoObtencion.isNotEmpty()
                && ::razonTenencia.isInitialized && razonTenencia.isNotEmpty()
    }

    private fun getMissingFields(): String {
        val missingFields = mutableListOf<String>()
        
        if (binding.namePetTextInputEditText.text.toString().trim().isEmpty()) {
            missingFields.add("Nombre de mascota")
        }
        if (binding.fechaTextInputEditText.text.toString().trim().isEmpty()) {
            missingFields.add("Fecha de nacimiento")
        }
        if (!::especie.isInitialized || especie.isEmpty()) {
            missingFields.add("Especie")
        }
        if (!::genero.isInitialized || genero.isEmpty()) {
            missingFields.add("Género")
        }
        if (!::raza.isInitialized || raza.isEmpty()) {
            missingFields.add("Raza Canina")
        }
        if (!::tamano.isInitialized || tamano.isEmpty()) {
            missingFields.add("Tamaño")
        }
        if (!::caracter.isInitialized || caracter.isEmpty()) {
            missingFields.add("Carácter")
        }
        if (!::color.isInitialized || color.isEmpty()) {
            missingFields.add("Color del can")
        }
        if (!::pelaje.isInitialized || pelaje.isEmpty()) {
            missingFields.add("Pelaje del can")
        }
        if (!::esterelizado.isInitialized || esterelizado.isEmpty()) {
            missingFields.add("Esterilizado")
        }
        if (!::distrito.isInitialized || distrito.isEmpty()) {
            missingFields.add("Distrito donde reside")
        }
        if (!::modoObtencion.isInitialized || modoObtencion.isEmpty()) {
            missingFields.add("Modo de Obtención")
        }
        if (!::razonTenencia.isInitialized || razonTenencia.isEmpty()) {
            missingFields.add("Razón de Tenencia")
        }
        
        return if (missingFields.isEmpty()) {
            ""
        } else {
            missingFields.joinToString(", ")
        }
    }

    private fun setupFormValidation() {
        // Agregar listeners a los campos para validar en tiempo real
        binding.namePetTextInputEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkFormAndEnableButton()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        binding.fechaTextInputEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkFormAndEnableButton()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun sendFormData() {
        if (::imageCan.isInitialized && validateForm()) {
            // Validar que el idUsuario sea válido antes de enviar
            if (idUsuario == null || idUsuario == -1L) {
                Log.e("RegisterCanFragment", "ERROR: idUsuario no válido antes de enviar")
                Toast.makeText(
                    requireContext(),
                    "Error: Usuario no autenticado. Por favor, inicia sesión nuevamente.",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            
            // Obtener el idUsuario guardado en LoginFragment desde SharedPreferences
            val sharedPref = activity?.getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
            val currentIdUsuario = sharedPref?.getLong("idUsuario", -1) ?: -1
            
            if (currentIdUsuario == -1L) {
                Log.e("RegisterCanFragment", "ERROR: No se pudo obtener idUsuario de SharedPreferences (guardado en LoginFragment)")
                Toast.makeText(
                    requireContext(),
                    "Error: Usuario no autenticado. Por favor, inicia sesión nuevamente.",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            
            Log.d("RegisterCanFragment", "Enviando formulario - idUsuario (Long): $currentIdUsuario, idUsuario (Int): ${currentIdUsuario.toInt()}")
            
            registerCanViewModel.listar(
                RegisterCanRequest(
                    nombre = binding.namePetTextInputEditText.text.toString().trim(),
                    fechaNacimiento = binding.fechaTextInputEditText.text.toString().trim(),
                    especie = especie,
                    genero = genero,
                    raza = raza,
                    tamanio = tamano,
                    caracter = caracter,
                    color = color,
                    pelaje = pelaje,
                    esterilizado = esterelizado,
                    distrito = distrito,
                    modoObtencion = modoObtencion,
                    razonTenencia = razonTenencia,
                    foto = "imageCan",
                    idUsuario = currentIdUsuario
                )
            )
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun convertImageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val image = imageProxy.image ?: return null
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        val nv21 = ByteArray(ySize + uSize + vSize)
        //U and V are swapped
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(
            Rect(0, 0, yuvImage.width, yuvImage.height), 100, out
        )
        val imageBytes = out.toByteArray()

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                setupCamera()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Aceptame por favor")
                    .setMessage("Acepta la camara o me da ansiedad xD")
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)

                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                    }
                    .show()
            }

            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    private fun setupCamera() {
        binding.cameraPreview.post {
            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(640, 480)) // reducir resolución
                .setTargetRotation(binding.cameraPreview.display.rotation)
                .build()
            cameraExecutor = Executors.newSingleThreadExecutor()
            startCamera()
            isCameraReady = true
        }
    }

    override fun onStart() {
        super.onStart()
        if (checkPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions()
            }
        } else {
            getLastLocation()
        }
        classifier = Classifier(
            FileUtil.loadMappedFile(requireContext(), MODEL_PATH),
            FileUtil.loadLabels(requireContext(), LABEL_PATH)
        )
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun getLastLocation() {
        Log.d("josue", "getLastLocation")
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient?.lastLocation!!.addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful && task.result != null) {
                Log.d("josue", "latitude" + lastLocation!!.latitude.toString())
                Log.d("josue", "longitude" + lastLocation!!.longitude.toString())
                lastLocation = task.result
            } else {
                Log.w(TAG, "getLastLocation:exception", task.exception)
            }
        }
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
        } else {
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> {
                    // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    Log.i(TAG, "User interaction was cancelled.")
                }

                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    // Permission granted.
                    getLastLocation()
                }

                else -> {
                }
            }
        }
    }

    companion object {
        private val TAG = "LocationProvider"
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val preview = Preview.Builder()
                .setTargetResolution(Size(640, 480))
                .build()
                .apply {
                    setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(640, 480))
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(640, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .apply {
                    setAnalyzer(cameraExecutor) { imageProxy ->
                        val bitmap = convertImageProxyToBitmap(imageProxy)
                        if (bitmap != null) {
                            getPhotoBitmap(bitmap)
                            val dogRecognition = classifier.recognizeImage(bitmap).first()
                            Log.d("RegisterCanFragment", "Reconocimiento: id=${dogRecognition.id}, confianza=${dogRecognition.confidence}%")
                            enableTakePhotoButton(dogRecognition)
                        }
                        imageProxy.close()
                    }
                }

            try {
                // MUY IMPORTANTE
                cameraProvider.unbindAll()

                // Solo enlazamos preview + analysis
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )

                // Guarda el provider para tomar fotos más adelante
                this.imageCapture = imageCapture

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private var lastDogRecognition: DogRecognition? = null

    /**
     * Extrae el nombre de la raza desde el DogRecognition.id
     * Ejemplo: "n02106550-rottweiler" -> "Rottweiler"
     */
    private fun extraerRazaDelReconocimiento(dogRecognition: DogRecognition): String {
        val id = dogRecognition.id
        if (id.isBlank()) return "Mestizo"
        
        // Extraer la parte después del último '-' o '_'
        val raw = id.substringAfterLast('-').substringAfterLast('_')
        if (raw.isBlank()) return "Mestizo"
        
        // Convertir a formato title case (primera letra mayúscula)
        return raw.split('-', '_', ' ')
            .filter { it.isNotBlank() }
            .joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.titlecase() } }
    }

    private fun enableTakePhotoButton(dogRecognition: DogRecognition) {
        lifecycleScope.launch(Dispatchers.Main) {
            lastDogRecognition = dogRecognition
            
            // Si la confianza es alta, actualizar la raza del formulario con la raza reconocida
            if (dogRecognition.confidence > 80.0) {
                val razaReconocida = extraerRazaDelReconocimiento(dogRecognition)
                Log.d("RegisterCanFragment", "Raza reconocida: $razaReconocida (confianza: ${dogRecognition.confidence}%)")
                
                // Actualizar el campo de raza en el formulario si está vacío o es diferente
                if (!::raza.isInitialized || raza.isEmpty() || raza == "Mestizo") {
                    raza = razaReconocida
                    binding.razaCaninaAutoCompleteTextView.setText(razaReconocida, false)
                    Log.d("RegisterCanFragment", "Raza actualizada en el formulario: $razaReconocida")
                }
            }
            
            // Siempre verificar si se puede habilitar el botón cuando hay reconocimiento
            checkFormAndEnableButton()
        }
    }
    
    private fun getPhotoBitmap(imageBytes: Bitmap) {
        photo = imageBytes
        val byteArrayOutputStream = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        imageCan = Base64.encodeToString(byteArray, Base64.DEFAULT)
        // Verificar formulario cuando se capture la imagen
        checkFormAndEnableButton()
    }


//    private fun enableTakePhotoButton(dogRecognition: DogRecognition) {
//        if (dogRecognition.confidence > 80.0) {
//            //binding.takePhotoFab.alpha = 1f
//            binding.confirmAppCompatButton.isEnabled = true
//            binding.confirmAppCompatButton.setOnClickListener {
//                binding.confirmAppCompatButton.isEnabled = false
//                viewModel.getDogByMlId(dogRecognition.id)
//            }
//        } else {
//
//            binding.confirmAppCompatButton.isEnabled = false
//            binding.confirmAppCompatButton.setOnClickListener(null)
//        }
//    }

    private fun openDetailActivity(dog: Dog) {
        try {
            Log.d("RegisterCanFragment", "=== openDetailActivity INICIADO ===")
            Log.d("RegisterCanFragment", "openDetailActivity llamado con dog: ${dog.name}, id: ${dog.id}")
            
            // Verificar que estamos en el hilo principal
            if (android.os.Looper.myLooper() != android.os.Looper.getMainLooper()) {
                Log.d("RegisterCanFragment", "No estamos en el hilo principal, usando post")
                binding.root.post {
                    openDetailActivity(dog)
                }
                return
            }
            
            // Guardar la imagen en SharedPreferences (igual que RegisterHocicoFragment)
            if (::imageCan.isInitialized) {
                val sharedPref = activity?.getSharedPreferences("fotoKey", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPref!!.edit()
                editor.putString("foto", imageCan)
                editor.apply()
                editor.commit()
                Log.d("RegisterCanFragment", "Imagen guardada en SharedPreferences - longitud: ${imageCan.length}")
            } else {
                Log.e("RegisterCanFragment", "Error: imageCan no está inicializado en openDetailActivity")
                Toast.makeText(requireContext(), "Error: No se pudo capturar la imagen", Toast.LENGTH_SHORT).show()
                return
            }

            // Navegar a DogDetailActivity con todos los datos del formulario
            val intent = Intent(requireContext(), DogDetailActivity::class.java)
            intent.putExtra(DogDetailActivity.DOG_KEY, dog)
            intent.putExtra(DogDetailActivity.IS_RECOGNITION_KEY, true)
            intent.putExtra("nombreMacota", binding.namePetTextInputEditText.text.toString().trim())
            intent.putExtra("fechaNacimiento", binding.fechaTextInputEditText.text.toString().trim())
            intent.putExtra("especie", especie)
            intent.putExtra("genero", genero)
            intent.putExtra("raza", raza)
            intent.putExtra("tamano", tamano)
            intent.putExtra("caracter", caracter)
            intent.putExtra("color", color)
            intent.putExtra("pelaje", pelaje)
            intent.putExtra("esterelizado", esterelizado)
            intent.putExtra("distrito", distrito)
            intent.putExtra("modoObtencion", modoObtencion)
            intent.putExtra("razonTenencia", razonTenencia)
            
            Log.d("RegisterCanFragment", "Iniciando DogDetailActivity con Intent")
            Log.d("RegisterCanFragment", "Datos enviados - nombreMacota: ${binding.namePetTextInputEditText.text.toString().trim()}, raza: $raza")
            
            startActivity(intent)
            Log.d("RegisterCanFragment", "DogDetailActivity iniciado exitosamente")
            Log.d("RegisterCanFragment", "=== openDetailActivity COMPLETADO ===")
        } catch (e: Exception) {
            Log.e("RegisterCanFragment", "Error al abrir DogDetailActivity: ${e.message}", e)
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        
        // Deshabilitar la edición manual del campo
        binding.fechaTextInputEditText.isFocusable = false
        binding.fechaTextInputEditText.isClickable = true
        
        binding.fechaTextInputEditText.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            
            val datePickerDialog = android.app.DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                    
                    // Formatear la fecha en el formato que espera la API: YYYY-MM-DD
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedCalendar.time)
                    
                    binding.fechaTextInputEditText.setText(formattedDate)
                },
                year,
                month,
                day
            )
            
            // Establecer la fecha máxima como hoy (no se puede seleccionar fechas futuras)
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            
            datePickerDialog.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }
    }


}