package com.durand.dogedex.ui.forget_password

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.durand.dogedex.R
import com.durand.dogedex.api.ApiResponseStatus
import com.durand.dogedex.api.User
import com.durand.dogedex.api.response.Dog
import com.durand.dogedex.databinding.ActivityMainBinding
import com.durand.dogedex.machinelearning.Classifier
import com.durand.dogedex.machinelearning.DogRecognition
import com.durand.dogedex.ui.ApiServiceInterceptor
import com.durand.dogedex.ui.auth.LoginActivity
import com.durand.dogedex.ui.dogdetail.DogDetailActivity
import com.durand.dogedex.ui.dogdetail.DogDetailActivity.Companion.DOG_KEY
import com.durand.dogedex.ui.dogdetail.DogDetailActivity.Companion.IS_RECOGNITION_KEY
import com.durand.dogedex.ui.doglist.DogListActivity
import com.durand.dogedex.ui.settings.SettingsActivity
import com.durand.dogedex.util.LABEL_PATH
import com.durand.dogedex.util.MODEL_PATH
import org.tensorflow.lite.support.common.FileUtil
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private var isCameraReady = false
    private val viewModel: MainViewModel by viewModels()
    private lateinit var classifier: Classifier

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                setupCamera()
            } else {
                Toast.makeText(this, R.string.camera_permission, Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = User.getLoggedInUser(this)
        if (user == null) {
            openLoginActivity()
            return
        } else {
            Log.d("josue", "authenticationToken: " + user.authenticationToken)
            ApiServiceInterceptor.setSessionToken(user.authenticationToken)
        }

        binding.settingsFab.setOnClickListener {
            openSettingsActivity()
        }
        binding.dogListFab.setOnClickListener {
            startActivity(Intent(this, DogListActivity::class.java))
        }
//        binding.takePhotoFab.setOnClickListener {
//            if (isCameraReady) {
//                takePhoto()
//            }
//        }

        viewModel.status.observe(this) { status ->
            when (status) {
                is ApiResponseStatus.Error -> {
                    binding.loadingWheel.visibility = View.GONE
                    Toast.makeText(this, status.message, Toast.LENGTH_SHORT).show()
                }
                is ApiResponseStatus.Loading -> binding.loadingWheel.visibility = View.VISIBLE
                is ApiResponseStatus.Success -> {
                    binding.loadingWheel.visibility = View.GONE
                    Toast.makeText(this, "Se cargaron los datos correctamente!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        viewModel.dog.observe(this) { dog ->
            if (dog != null) {
                openDetailActivity(dog)
            }
        }
        requestCameraPermission()
    }

    private fun openDetailActivity(dog: Dog) {
        val intent = Intent(this, DogDetailActivity::class.java)
        intent.putExtra(DOG_KEY, dog)
        intent.putExtra(IS_RECOGNITION_KEY, true)
        startActivity(intent)
    }

    private fun openSettingsActivity() {
        startActivity(Intent(this, SettingsActivity::class.java))
        finish()
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


    private fun openLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                setupCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                AlertDialog.Builder(this)
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

    override fun onDestroy() {
        super.onDestroy()
        if (::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
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
            FileUtil.loadMappedFile(this@MainActivity, MODEL_PATH),
            FileUtil.loadLabels(this@MainActivity, LABEL_PATH)
        )
    }

    private fun takePhoto() {
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(getOutputPhotoFile()).build()
        imageCapture.takePicture(outputFileOptions, cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@MainActivity,
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
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name) + ".jpg").apply {
                mkdirs()
            }
        }
        return if (mediaDir != null && mediaDir.exists()) {
            mediaDir
        } else {
            filesDir
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
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
            }, ContextCompat.getMainExecutor(this)
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

}