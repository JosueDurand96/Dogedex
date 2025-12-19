package com.durand.dogedex.ui.user_fragment.register_can

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import coil.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.durand.dogedex.R
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.User
import com.durand.dogedex.data.response.Dog
import com.durand.dogedex.databinding.FragmentRegisterCanDangerousBinding
import com.durand.dogedex.domain.Classifier
import com.durand.dogedex.domain.DogRecognition
import com.durand.dogedex.ui.ApiServiceInterceptor
import com.durand.dogedex.ui.auth.LoginActivity
import com.durand.dogedex.ui.dogdetail.DogDetailActivity
import com.durand.dogedex.ui.forget_password.MainViewModel
import com.durand.dogedex.ui.settings.SettingsActivity
import com.durand.dogedex.util.LABEL_PATH
import com.durand.dogedex.util.MODEL_PATH
import org.tensorflow.lite.support.common.FileUtil
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class RegisterCanDangerousFragment : Fragment() {

    private var _binding: FragmentRegisterCanDangerousBinding? = null
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private var isCameraReady = false
    private lateinit var classifier: Classifier
    private var selectedImageUri: Uri? = null

    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                setupCamera()
            } else {
                Toast.makeText(requireContext(), com.durand.dogedex.R.string.camera_permission, Toast.LENGTH_SHORT).show()
            }
        }
    
    // Launcher para seleccionar imagen de la galería
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            // Procesar la imagen para reconocimiento (esto también mostrará la imagen)
            processGalleryImage(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        _binding = FragmentRegisterCanDangerousBinding.inflate(inflater, container, false)
//        val user = User.getLoggedInUser(requireActivity())
//        if (user == null) {
//            openLoginActivity()
//        } else {
//            Log.d("josue", "authenticationToken: " + user.authenticationToken)
//            ApiServiceInterceptor.setSessionToken(user.authenticationToken)
//        }
        viewModel.status.observe(viewLifecycleOwner) { status ->
            when (status) {
                is ApiResponseStatus.Error -> {
                    binding.loadingWheel.visibility = View.GONE
                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
                is ApiResponseStatus.Loading -> binding.loadingWheel.visibility = View.VISIBLE
                is ApiResponseStatus.Success -> {
                    binding.loadingWheel.visibility = View.GONE
                    // No mostrar toast aquí, ya que se abrirá DogDetailActivity
                }
            }
        }


        viewModel.dog.observe(viewLifecycleOwner) { dog ->
            if (dog != null) {
                Log.d("RegisterCanDangerousFragment", "Perro obtenido: ${dog.name}, abriendo DogDetailActivity")
                openDetailActivity(dog)
            }
        }
        
        // Configurar botón de galería
        binding.galleryButton.setOnClickListener {
            openGallery()
        }
        
        requestCameraPermission()


        val root: View = binding.root

        return root
    }
    
    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }
    
    private fun processGalleryImage(uri: Uri) {
        var inputStream: InputStream? = null
        try {
            inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            
            if (bitmap == null) {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.loadingWheel.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: No se pudo cargar la imagen", Toast.LENGTH_SHORT).show()
                }
                return
            }
            
            // Mostrar imagen de galería INMEDIATAMENTE (en hilo principal)
            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    Log.d("RegisterCanDangerousFragment", "Mostrando imagen de galería - Bitmap: ${bitmap.width}x${bitmap.height}")
                    
                    // Asegurar que el contenedor esté visible (igual que RegisterCanFragment)
                    binding.imageContainer.visibility = View.VISIBLE
                    
                    // Primero ocultar la cámara
                    binding.cameraPreview.visibility = View.GONE
                    
                    // Luego mostrar y cargar la imagen usando coil (igual que RegisterCanFragment)
                    binding.galleryImageView.visibility = View.VISIBLE
                    binding.galleryImageView.load(bitmap) {
                        crossfade(true)
                    }
                    
                    Log.d("RegisterCanDangerousFragment", "ImageView visibility: ${binding.galleryImageView.visibility}")
                    Log.d("RegisterCanDangerousFragment", "ImageContainer visibility: ${binding.imageContainer.visibility}")
                    
                    // Mostrar indicador de carga
                    binding.loadingWheel.visibility = View.VISIBLE
                } catch (e: Exception) {
                    Log.e("RegisterCanDangerousFragment", "Error al mostrar imagen: ${e.message}", e)
                    Toast.makeText(requireContext(), "Error al mostrar la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            
            if (::classifier.isInitialized) {
                // Procesar reconocimiento en hilo de fondo
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val dogRecognition = classifier.recognizeImage(bitmap).first()
                        
                        // Actualizar UI en hilo principal
                        lifecycleScope.launch(Dispatchers.Main) {
                            binding.loadingWheel.visibility = View.GONE
                            
                            // Habilitar botón siempre que haya una imagen (sin importar la confianza)
                            binding.identifyRaceButton.isEnabled = true
                            binding.identifyRaceButton.text = "Identificar raza"
                            binding.identifyRaceButton.setOnClickListener {
                                Log.d("RegisterCanDangerousFragment", "Click en botón - llamando getDogByMlId con id: ${dogRecognition.id}")
                                binding.loadingWheel.visibility = View.VISIBLE
                                viewModel.getDogByMlId(dogRecognition.id)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("RegisterCanDangerousFragment", "Error al procesar reconocimiento: ${e.message}", e)
                        lifecycleScope.launch(Dispatchers.Main) {
                            binding.loadingWheel.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                "Error al procesar el reconocimiento: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.loadingWheel.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Error: Classifier no inicializado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            Log.e("RegisterCanDangerousFragment", "Error al procesar imagen de galería: ${e.message}", e)
            lifecycleScope.launch(Dispatchers.Main) {
                binding.loadingWheel.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Error al procesar la imagen: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } finally {
            try {
                inputStream?.close()
            } catch (e: Exception) {
                Log.e("RegisterCanDangerousFragment", "Error al cerrar inputStream: ${e.message}", e)
            }
        }
    }


    private fun openLoginActivity() {
        startActivity(Intent(requireContext(), LoginActivity::class.java))
    }

    private fun openDetailActivity(dog: Dog) {
        val intent = Intent(requireContext(), DogDetailActivity::class.java)
        intent.putExtra(DogDetailActivity.DOG_KEY, dog)
        intent.putExtra(DogDetailActivity.IS_RECOGNITION_KEY, true)
        startActivity(intent)
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
            Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
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
                .setTargetRotation(binding.cameraPreview.display.rotation)
                .build()
            cameraExecutor = Executors.newSingleThreadExecutor()
            startCamera()
            isCameraReady = true
        }
    }

    override fun onStart() {
        super.onStart()
        classifier = Classifier(
            FileUtil.loadMappedFile(requireContext(), MODEL_PATH),
            FileUtil.loadLabels(requireContext(), LABEL_PATH)
        )
    }

    private fun takePhoto() {
        // Ocultar imagen de galería y mostrar preview de cámara
        binding.galleryImageView.visibility = View.GONE
        binding.cameraPreview.visibility = View.VISIBLE
        selectedImageUri = null
        
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
        val mediaDir =requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name) + ".jpg").apply {
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
                    if (bitmap != null){
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
        // Habilitar botón siempre que haya reconocimiento (sin importar la confianza)
        binding.identifyRaceButton.isEnabled = true
        binding.identifyRaceButton.text = "Identificar raza"
        binding.identifyRaceButton.setOnClickListener {
            Log.d("RegisterCanDangerousFragment", "Click en botón - llamando getDogByMlId con id: ${dogRecognition.id}")
            binding.loadingWheel.visibility = View.VISIBLE
            viewModel.getDogByMlId(dogRecognition.id)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }
    }
}