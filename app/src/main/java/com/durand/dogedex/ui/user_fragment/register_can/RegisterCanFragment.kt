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
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
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
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.Request.AgregarMascotaRequest
import com.durand.dogedex.data.User
import com.durand.dogedex.data.response.Dog
import com.durand.dogedex.databinding.FragmentRegisterCanBinding
import com.durand.dogedex.domain.Classifier
import com.durand.dogedex.domain.DogRecognition
import com.durand.dogedex.ui.ApiServiceInterceptor
import com.durand.dogedex.ui.auth.LoginActivity
import com.durand.dogedex.ui.dogdetail.DogDetailActivity
import com.durand.dogedex.ui.forget_password.MainViewModel
import com.durand.dogedex.ui.settings.SettingsActivity
import com.durand.dogedex.util.LABEL_PATH
import com.durand.dogedex.util.MODEL_PATH
import com.durand.dogedex.util.createLoadingDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.tensorflow.lite.support.common.FileUtil
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class RegisterCanFragment : Fragment() {

    private var _binding: FragmentRegisterCanBinding? = null
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private var isCameraReady = false
    private lateinit var classifier: Classifier
    private val vm: RegisterCanViewModel by viewModels()
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var lastLocation: Location? = null
    private val loading by lazy {
        requireContext().createLoadingDialog()
    }


    private lateinit var nombreMacota: String
    private lateinit var fechaNacimiento: String
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
    private val mainViewModel: MainViewModel by viewModels()
    private var fusedLocationClient: FusedLocationProviderClient? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        _binding = FragmentRegisterCanBinding.inflate(inflater, container, false)
//        val user = User.getLoggedInUser(requireActivity())
//        if (user == null) {
//            openLoginActivity()
//        } else {
//            Log.d("josue", "authenticationToken: " + user.authenticationToken)
//            ApiServiceInterceptor.setSessionToken(user.authenticationToken)
//        }
        viewModel.status.observe(requireActivity()) { status ->
            when (status) {
                is ApiResponseStatus.Error -> {
                    binding.loadingWheel.visibility = View.GONE
                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
                is ApiResponseStatus.Loading -> binding.loadingWheel.visibility = View.VISIBLE
                is ApiResponseStatus.Success -> {
                    binding.loadingWheel.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Se cargaron los datos correctamente!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.dog.observe(requireActivity()) { dog ->
            if (dog != null) {
                dogCan = dog
                openDetailActivity(dog)
            }
        }
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
        binding.vm = vm
        binding.lifecycleOwner = viewLifecycleOwner
        _binding!!.razonDeTenenciaAutoCompleteTextView.setAdapter(adapterrazonDeTenencia)
        getUserProfile()
        initObservers()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.especieAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            especie = selectedItem.toString()
        }

        binding.generoAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            genero = selectedItem.toString()
        }

        binding.razaCaninaAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            raza = selectedItem.toString()
        }

        binding.tamanoAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            tamano = selectedItem.toString()
        }

        binding.caracterAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            caracter = selectedItem.toString()
        }

        binding.colorCanAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            color = selectedItem.toString()
        }

        binding.pelajeAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            pelaje = selectedItem.toString()
        }

        binding.esterilizadoAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            esterelizado = selectedItem.toString()
        }

        binding.distritoDondeResideAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            distrito = selectedItem.toString()
        }

        binding.modoDeObtencionAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            modoObtencion = selectedItem.toString()
        }

        binding.razonDeTenenciaAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            razonTenencia = selectedItem.toString()
        }

        vm.list.observe(requireActivity()) {
            binding.loadingWheel.visibility = View.GONE
            openDetailCan(dogCan)
        }

        return binding.root
    }

    private fun initObservers() {
        vm.event.observe(viewLifecycleOwner) {
            when (it) {
                is RegisterCanEvent.ShowError -> {
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                }

                RegisterCanEvent.Success -> {

                }
                RegisterCanEvent.Loading -> {
                    loading.show()
                }
                RegisterCanEvent.DismissLoading -> {
                    loading.dismiss()
                }

                else -> {}
            }
        }

    }

    private fun openDetailCan(dogCan: Dog) {
        val sharedPref = activity?.getSharedPreferences("fotoKey", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref!!.edit()
        editor.putString("foto", imageCan)
        editor.apply()
        editor.commit()


        val intent = Intent(requireContext(), DogDetailActivity::class.java)
        intent.putExtra(DogDetailActivity.DOG_KEY, dogCan)
        intent.putExtra(DogDetailActivity.IS_RECOGNITION_KEY, true)
        intent.putExtra("nombreMacota", binding.namePetTextInputEditText.text.toString())
        intent.putExtra("fechaNacimiento", binding.fechaTextInputEditText.text.toString())
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
        startActivity(intent)
    }


    private fun openLoginActivity() {
        startActivity(Intent(requireContext(), LoginActivity::class.java))
    }

    private fun openDetailActivity(dog: Dog) {

        binding.loadingWheel.visibility = View.VISIBLE
        vm.registerCan(
            AgregarMascotaRequest(
                caracter,
                color, distrito,
                especie, 1,
                esterelizado,
                binding.fechaTextInputEditText.text.toString(),
                imageCan,
                genero, 2,
                4,
                "9999", "9999",
                modoObtencion, binding.namePetTextInputEditText.text.toString(),
                pelaje, 0, razonTenencia,
                tamano
            )
        )

    }

    private fun openSettingsActivity() {
        startActivity(Intent(requireContext(), SettingsActivity::class.java))
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

    private fun getPhotoBitmap(imageBytes: Bitmap) {
        photo = imageBytes
        val byteArrayOutputStream = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        imageCan = Base64.encodeToString(byteArray, Base64.DEFAULT)
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
        requestCode: Int, permissions: Array<String>,
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


    private fun takePhoto() {
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(getOutputPhotoFile()).build()
        imageCapture.takePicture(outputFileOptions, cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    /*    val photoUri = outputFileResults.savedUri
                        val bitmap = BitmapFactory.decodeFile(photoUri?.path)
                        val dogRecognition = classifier.recognizeImage(bitmap).first()
                        viewModel.getDogByMlId(dogRecognition.id)*/
                }

            })
    }

    private fun getOutputPhotoFile(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(com.durand.dogedex.R.string.app_name) + ".jpg").apply {
                mkdirs()
            }
        }
        return if (mediaDir != null && mediaDir.exists()) {
            mediaDir
        } else {
            requireActivity()!!.filesDir
        }
    }

    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build()
                preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    //    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    val bitmap = convertImageProxyToBitmap(imageProxy)
                    if (bitmap != null) {
                        getPhotoBitmap(bitmap)
                        val dogRecognition = classifier.recognizeImage(bitmap).first()
                        enableTakePhotoButton(dogRecognition)

                    }

                    //    val photoUri = outputFileResults.savedUri
                    //    val bitmap = BitmapFactory.decodeFile(photoUri?.path)
                    //    val dogRecognition = classifier.recognizeImage(bitmap).first()
                    //    viewModel.getDogByMlId(dogRecognition.id)
                    imageProxy.close()
                }



                cameraProvider.bindToLifecycle(
                    this, cameraSelector,
                    preview, imageCapture, imageAnalysis
                )

            }, ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun enableTakePhotoButton(dogRecognition: DogRecognition) {
        if (dogRecognition.confidence > 80.0) {
            //binding.takePhotoFab.alpha = 1f
            binding.confirmAppCompatButton.setOnClickListener {
                mainViewModel.getDogByMlId(dogRecognition.id)
            }
        } else {

            // binding.takePhotoFab.alpha = 0.2f
            binding.confirmAppCompatButton.setOnClickListener(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }
    }

    private fun getUserProfile() {
       // val loggedInUser: User? = User.getLoggedInUser(requireActivity())
       // vm.setUserProfile(loggedInUser)
    }


}