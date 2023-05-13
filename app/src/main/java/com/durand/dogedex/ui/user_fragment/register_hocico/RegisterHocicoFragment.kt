package com.durand.dogedex.ui.user_fragment.register_hocico

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.durand.dogedex.api.ApiResponseStatus
import com.durand.dogedex.api.User
import com.durand.dogedex.api.response.Dog
import com.durand.dogedex.databinding.FragmentRegisterHocicoBinding
import com.durand.dogedex.machinelearning.Classifier
import com.durand.dogedex.machinelearning.DogRecognition
import com.durand.dogedex.ui.ApiServiceInterceptor
import com.durand.dogedex.ui.forget_password.MainViewModel
import com.durand.dogedex.ui.auth.LoginActivity
import com.durand.dogedex.ui.dogdetail.DogDetailActivity
import com.durand.dogedex.ui.settings.SettingsActivity
import com.durand.dogedex.util.LABEL_PATH
import com.durand.dogedex.util.MODEL_PATH
import org.tensorflow.lite.support.common.FileUtil
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RegisterHocicoFragment : Fragment() {

    private var _binding: FragmentRegisterHocicoBinding? = null
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private var isCameraReady = false
    private lateinit var classifier: Classifier
    // This property is only valid between onCreateView and
    // onDestroyView.
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        _binding = FragmentRegisterHocicoBinding.inflate(inflater, container, false)


        val user = User.getLoggedInUser(requireActivity())
        if (user == null) {
            openLoginActivity()
        } else {
            Log.d("josue", "authenticationToken: " + user.authenticationToken)
            ApiServiceInterceptor.setSessionToken(user.authenticationToken)
        }
        viewModel.status.observe(requireActivity()) { status ->
            when (status) {
                is ApiResponseStatus.Error -> {
                    binding.loadingWheel.visibility = View.GONE
                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
                is ApiResponseStatus.Loading -> binding.loadingWheel.visibility = View.VISIBLE
                is ApiResponseStatus.Success -> {
                    binding.loadingWheel.visibility = View.GONE
                    Toast.makeText(requireContext(), "Se cargaron los datos correctamente!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        viewModel.dog.observe(requireActivity()) { dog ->
            if (dog != null) {
                openDetailActivity(dog)
            }
        }
        requestCameraPermission()


        val root: View = binding.root

        return root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        if (dogRecognition.confidence > 80.0){
            //binding.takePhotoFab.alpha = 1f
            binding.takePhotoFab.setOnClickListener {
                viewModel.getDogByMlId(dogRecognition.id)
            }
        }else{
            // binding.takePhotoFab.alpha = 0.2f
            binding.takePhotoFab.setOnClickListener(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }
    }
}