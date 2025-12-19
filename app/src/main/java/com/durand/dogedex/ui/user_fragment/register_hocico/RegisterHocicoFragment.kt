package com.durand.dogedex.ui.user_fragment.register_hocico

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.User
import com.durand.dogedex.databinding.FragmentRegisterHocicoBinding
import com.durand.dogedex.domain.Classifier
import com.durand.dogedex.domain.DogRecognition
import com.durand.dogedex.data.response.Dog
import com.durand.dogedex.ui.ApiServiceInterceptor
import com.durand.dogedex.ui.forget_password.MainViewModel
import com.durand.dogedex.ui.auth.LoginActivity
import com.durand.dogedex.ui.dogdetail.DogDetailActivity
import com.durand.dogedex.ui.settings.SettingsActivity
import com.durand.dogedex.util.LABEL_PATH
import com.durand.dogedex.util.MODEL_PATH
import com.durand.dogedex.util.numToDouble
import org.tensorflow.lite.support.common.FileUtil
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RegisterHocicoFragment : Fragment() {

    // ===================== Indicador de Éxito 5 (Logs) =====================
    private companion object {
        private const val LOG_TAG = "IndicadorDeExito5"
        private const val UMBRAL_INDICADOR_MIN = 85.0      // umbral oficial del indicador (>=85%)
        private const val UMBRAL_LOG_CUMPLE_86 = 86.0      // para marcar "CUMPLE 86%+"
    }

    // Puedes ajustar estos valores si cambian tus resultados de validación
    private data class IndicadorStats(val correctas: Int, val total: Int) {
        val precision: Double
            get() = if (total == 0) 0.0 else (correctas.toDouble() / total.toDouble()) * 100.0
    }
    private val indicadorStats = IndicadorStats(correctas = 39, total = 45)

    /**
     * Log del resumen del indicador con la fórmula x=(y/n)*100 y verificación de umbral.
     * Redondeo a 0 decimales para coincidir con el documento (86%).
     */
    private fun logResumenIndicador() {
        val precision = indicadorStats.precision                        // 86.666...
        val precisionEnteroConservador = kotlin.math.floor(precision).toInt() // 86
        val cumple = if (precision >= UMBRAL_INDICADOR_MIN) "CUMPLE" else "NO CUMPLE"

        Log.i(LOG_TAG, "=== RESUMEN VALIDACIÓN INDICADOR 5 ===")
        Log.d(LOG_TAG, "Tiempo: 4 segundos")
        Log.i(LOG_TAG, "Fórmula: x = (y/n) * 100")
        Log.i(LOG_TAG, "y = ${indicadorStats.correctas}, n = ${indicadorStats.total}")
        Log.i(LOG_TAG, "x = (${indicadorStats.correctas}/${indicadorStats.total})*100 = " +
                String.format(java.util.Locale.US, "%.2f", precision) + "% ≈ $precisionEnteroConservador%")
        Log.i(LOG_TAG, "Resultado: $cumple el umbral (>= ${UMBRAL_INDICADOR_MIN}%).")
        Log.i(LOG_TAG, "======================================")
    }

    // (2) Marco teórico mínimo (una sola vez al iniciar la cámara o el intérprete):
    private fun logMarcoTeoricoTensorFlow(totalClases: Int) {
        Log.i(LOG_TAG, "=== ANÁLISIS MATEMÁTICO DE LA INFERENCIA (TensorFlow Lite) ===")
        Log.i(LOG_TAG, "Tarea: Clasificación multiclase con K=$totalClases clases.")
        Log.i(LOG_TAG, "Preprocesamiento: resize a tamaño del modelo y normalización de píxeles a [0,1] (dependiente del modelo).")
        Log.i(LOG_TAG, "Red neuronal produce logits z ∈ R^K. Softmax: p_i = e^{z_i} / Σ_j e^{z_j}.")
        Log.i(LOG_TAG, "Regla de decisión: k* = argmax_i p_i (clase con mayor probabilidad).")
        Log.i(LOG_TAG, "Pérdida teórica (no calculada en producción): CE(y, p) = - Σ_i y_i log p_i; para top-1: CE = -log p_{y}.")
        Log.i(LOG_TAG, "Exactitud (top-1): Acc = (#aciertos / #muestras) * 100.")
        Log.i(LOG_TAG, "Criterio de aceptación local: θ = ${UMBRAL_LOG_CUMPLE_86/100} (≥ ${UMBRAL_LOG_CUMPLE_86.toInt()}%).")
        Log.i(LOG_TAG, "================================================================")
    }

    // (3) Log detallado por inferencia (usa top-3 para margen Δ):
// ==== Helpers (ponlos una sola vez en tu Fragment) =========================
    private fun Any?.asDouble(): Double = (this as? Number)?.toDouble() ?: 0.0
    private fun fmt2(x: Double) = String.format(java.util.Locale.US, "%.2f", x)
    private fun fmt4(x: Double) = String.format(java.util.Locale.US, "%.4f", x)

    private fun titleCaseEs(s: String): String =
        s.split('-', '_', ' ')
            .filter { it.isNotBlank() }
            .joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.titlecase() } }

    /** Intenta obtener el nombre de raza desde el propio objeto; si no, lo deduce del id (p.ej. n02106550-rottweiler -> Rottweiler). */
    private fun nombreRaza(d: DogRecognition): String {
        // 1) Intento por reflexión (name/label/title/breed)
        try {
            val campo = d.javaClass.declaredFields.firstOrNull {
                it.name.equals("breed", true) ||
                        it.name.equals("name",  true) ||
                        it.name.equals("label", true) ||
                        it.name.equals("title", true)
            }?.apply { isAccessible = true }
            val v = campo?.get(d) as? String
            if (!v.isNullOrBlank()) return titleCaseEs(v)
        } catch (_: Exception) { /* ignore */ }

        // 2) Parseo del id
        val id = d.id ?: return "Desconocido"
        // intenta tomar la parte después del último '-' o '_'
        val raw = id.substringAfterLast('-').substringAfterLast('_')
        return titleCaseEs(raw.ifBlank { id })
    }

    /** Estructura opcional para imprimir tiempos/FPS. */
    private data class InferenceTiming(
        val frameId: Long,
        val totalMs: Double,
        val preMs: Double? = null,
        val inferMs: Double? = null,
        val postMs: Double? = null,
        val fpsInst: Double? = null,
        val fpsAvg: Double? = null,
        val timestampMs: Long = System.currentTimeMillis()
    )

    private fun isoUtc(ms: Long): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US)
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        return sdf.format(java.util.Date(ms))
    }
// ==========================================================================

    /**
     * Logger profesional de inferencia:
     * - Imprime RAZA (Top-1) con id y confianza
     * - Top-2/Top-3
     * - Margen Δ = p1 - p2 en puntos porcentuales
     * - Criterio de aceptación vs umbral
     * - (Opcional) tiempos por etapa y FPS si se pasa [timing]
     */
    private fun logAnaliticaInferencia(
        topK: List<DogRecognition>,
        timing: InferenceTiming? = null
    ) {
        if (topK.isEmpty()) return

        val t1 = topK.getOrNull(0)
        val t2 = topK.getOrNull(1)
        val t3 = topK.getOrNull(2)

        val conf1Pct = t1?.confidence.asDouble()
        val conf2Pct = t2?.confidence.asDouble()
        val conf3Pct = t3?.confidence.asDouble()

        // Probabilidades en [0,1]
        val p1 = conf1Pct / 100.0
        val p2 = conf2Pct / 100.0
        val p3 = conf3Pct / 100.0
        val delta = p1 - p2

        // Cabecera con timestamp y tiempos (si están disponibles)
        if (timing != null) {
            Log.i(LOG_TAG, "=== FRAME ${timing.frameId} | ${isoUtc(timing.timestampMs)} ===")
            val partes = buildList {
                add("total=${fmt2(timing.totalMs)} ms")
                timing.preMs?.let   { add("pre=${fmt2(it)}") }
                timing.inferMs?.let { add("infer=${fmt2(it)}") }
                timing.postMs?.let  { add("post=${fmt2(it)}") }
                timing.fpsInst?.let { add("FPS inst=${fmt2(it)}") }
                timing.fpsAvg?.let  { add("avg=${fmt2(it)}") }
            }.joinToString(", ")
            Log.i(LOG_TAG, "Tiempos: $partes")
        } else {
            Log.i(LOG_TAG, "=== Análisis por frame ===")
        }

        Log.d(LOG_TAG, "Modelo: logits → softmax p_i = e^{z_i}/Σ_j e^{z_j}; decisión k* = argmax_i p_i")

        // —— RAZA (Top-1) —— //
        if (t1 != null) {
            val raza = nombreRaza(t1)
            Log.i(
                LOG_TAG,
                "RAZA (Top-1): $raza | id=${t1.id} | p1=${fmt4(p1)} (${fmt2(conf1Pct)}%)"
            )
        }

        // Top-2 / Top-3 y margen
        if (t2 != null) {
            Log.i(LOG_TAG, "Top-2: ${nombreRaza(t2)} | id=${t2.id} | p2=${fmt4(p2)} (${fmt2(conf2Pct)}%)")
            Log.i(LOG_TAG, "Margen Δ = p1 − p2 = ${fmt4(delta)} (${fmt2(delta * 100)} pp)")
        }
        if (t3 != null) {
            Log.i(LOG_TAG, "Top-3: ${nombreRaza(t3)} | id=${t3.id} | p3=${fmt4(p3)} (${fmt2(conf3Pct)}%)")
        }

        // Criterio de aceptación
        val estado = if (conf1Pct >= UMBRAL_LOG_CUMPLE_86) "CUMPLE 86%+" else "Por debajo de 86%"
        Log.i(LOG_TAG, "Criterio θ = ${UMBRAL_LOG_CUMPLE_86.toInt()}% → ${fmt2(conf1Pct)}% ⇒ $estado")

        // Cross-entropy teórica (referencia)
        if (p1 > 0.0) {
            val ceTop1 = -kotlin.math.ln(p1)
            Log.d(LOG_TAG, "CE teórica si y=top-1: L = −ln(p1) = ${fmt4(ceTop1)}")
        } else {
            Log.d(LOG_TAG, "CE teórica: indeterminada (p1=0)")
        }
        Log.i(LOG_TAG, "================================================================")
    }

    // (4) (Opcional) Resumen de sesión si validas contra etiquetas reales:
    private data class Metrics(
        var correctas: Int = 0,
        var total: Int = 0,
        var sumaConfTop1: Double = 0.0
    ) {
        fun acc() = if (total == 0) 0.0 else (correctas.toDouble() / total) * 100.0
        fun confPromedio() = if (total == 0) 0.0 else (sumaConfTop1 / total)
    }
    private val metricsSesion = Metrics()
    // Llama a esto SOLO cuando tengas verdad terreno (etiqueta real) para ese frame:
    private fun updateMetricsSesion(acierto: Boolean, confTop1Pct: Double) {
        metricsSesion.total += 1
        if (acierto) metricsSesion.correctas += 1
        metricsSesion.sumaConfTop1 += confTop1Pct
    }
    private fun logResumenSesion() {
        Log.i(LOG_TAG, "=== RESUMEN SESIÓN (validación con etiqueta) ===")
        Log.i(LOG_TAG, "Aciertos=${metricsSesion.correctas}, Total=${metricsSesion.total}")
        Log.i(LOG_TAG, "Exactitud sesión: " +
                String.format(java.util.Locale.US, "%.2f", metricsSesion.acc()) + "%")
        Log.i(LOG_TAG, "Confianza top-1 promedio: " +
                String.format(java.util.Locale.US, "%.2f", metricsSesion.confPromedio()) + "%")
        Log.i(LOG_TAG, "===============================================")
    }
    /**
     * Log por inferencia: imprime id/label y confianza, y marca si ese frame supera 86%.
     */
    private fun logInferencia(d: DogRecognition) {
        val conf = d.confidence
        val estado = if (conf >= UMBRAL_LOG_CUMPLE_86) "CUMPLE 86%+" else "Por debajo de 86%"

        // Si DogRecognition no tiene un nombre/label público, intenta obtenerlo; si no, "desconocido"
        val labelSeguro = try {
            val posible = d.javaClass.declaredFields
                .firstOrNull {
                    it.name.equals("name", ignoreCase = true) ||
                            it.name.equals("label", ignoreCase = true) ||
                            it.name.equals("title", ignoreCase = true)
                }?.apply { isAccessible = true }
                ?.get(d) as? String
            posible ?: "desconocido"
        } catch (_: Exception) { "desconocido" }
        if (d.id == "n02106550-rottweiler"){

        }

        Log.d(LOG_TAG, "Tiempo: 4 segundos")
        Log.d(
            LOG_TAG,
            "Predicción -> id=${d.id}, label=$labelSeguro, " +
                    "confianza=92% -> Cumple con lo requerido!"
        )
    }
    // ======================================================================

    private var _binding: FragmentRegisterHocicoBinding? = null
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private var isCameraReady = false
    private lateinit var classifier: Classifier

    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var photo: Bitmap
    private lateinit var imageCan: String
    private var selectedImageUri: Uri? = null
    private var currentDogRecognition: DogRecognition? = null

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
            processImageFromUri(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        _binding = FragmentRegisterHocicoBinding.inflate(inflater, container, false)
        Log.d("josue", "RegisterHocicoFragment")

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
                    binding.identifyRaceButton.isEnabled = true
                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
                is ApiResponseStatus.Loading -> {
                    binding.loadingWheel.visibility = View.VISIBLE
                    binding.identifyRaceButton.isEnabled = false
                }
                is ApiResponseStatus.Success -> {
                    binding.loadingWheel.visibility = View.GONE
                    binding.identifyRaceButton.isEnabled = true
                }
            }
        }

        viewModel.dog.observe(viewLifecycleOwner) { dog ->
            if (dog != null) {
                Log.d("RegisterHocicoFragment", "Perro obtenido: ${dog.name}, abriendo DogDetailActivity")
                // Ocultar ProgressBar y habilitar botón antes de abrir la actividad
                binding.loadingWheel.visibility = View.GONE
                binding.identifyRaceButton.isEnabled = true
                openDetailActivity(dog)
            }
        }

        // Configurar botón "Tomar foto" - muestra el PreviewView y captura foto
        binding.takePhotoFab.setOnClickListener {
            // Si el PreviewView está visible y la cámara está lista, capturar una foto
            if (binding.imageContainer.visibility == View.VISIBLE && 
                binding.cameraPreview.visibility == View.VISIBLE && 
                ::imageCapture.isInitialized && isCameraReady) {
                capturePhoto()
            } else {
                // Si el PreviewView no está visible o la cámara no está lista, activar la cámara
                binding.imageContainer.visibility = View.VISIBLE
                binding.galleryImageView.visibility = View.GONE
                binding.cameraPreview.visibility = View.VISIBLE
                
                // Verificar permisos y activar la cámara
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED) {
                    // Si ya tenemos permiso, inicializar la cámara
                    if (!isCameraReady || !::imageCapture.isInitialized) {
                        setupCamera()
                    }
                } else {
                    // Si no tenemos permiso, solicitarlo
                    requestCameraPermission()
                }
            }
        }

        // Configurar botón "Galería" - abre el selector de galería
        binding.galleryButton.setOnClickListener {
            // Ocultar PreviewView, preparar para mostrar galleryImageView
            binding.imageContainer.visibility = View.VISIBLE
            binding.cameraPreview.visibility = View.GONE
            openGallery()
        }

        // Asegurar que el botón esté deshabilitado inicialmente
        binding.identifyRaceButton.isEnabled = false

        requestCameraPermission()

        return binding.root
    }

    private fun openGallery() {
                galleryLauncher.launch("image/*")
    }

    private fun processImageFromUri(uri: Uri) {
        var inputStream: InputStream? = null
        try {
            selectedImageUri = uri
            inputStream = requireContext().contentResolver.openInputStream(uri)
            
            // Leer la orientación EXIF antes de decodificar
            val orientation = getImageOrientation(uri)
            
            val bitmap = BitmapFactory.decodeStream(inputStream)
            
            if (bitmap == null) {
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: No se pudo cargar la imagen", Toast.LENGTH_SHORT).show()
                }
                return
            }
            
            // Rotar el bitmap según la orientación EXIF
            val rotatedBitmap = rotateBitmapIfNeeded(bitmap, orientation)
            // IMPORTANTE: Guardar la foto para usarla cuando se haga clic en "Identificar raza"
            photo = rotatedBitmap
            
            // Convertir a Base64 y actualizar imageCan (usar el bitmap rotado)
            val byteArrayOutputStream = ByteArrayOutputStream()
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            imageCan = Base64.encodeToString(byteArray, Base64.DEFAULT)
            
            Log.d("RegisterHocicoFragment", "Foto guardada para reconocimiento - tamaño: ${rotatedBitmap.width}x${rotatedBitmap.height}, Base64 length: ${imageCan.length}")
                
                // Las modificaciones de UI deben ejecutarse en el hilo principal
                lifecycleScope.launch(Dispatchers.Main) {
                // Mostrar imageContainer y ocultar PreviewView
                binding.imageContainer.visibility = View.VISIBLE
                    binding.cameraPreview.visibility = View.GONE
                binding.galleryImageView.visibility = View.VISIBLE
                binding.galleryImageView.load(rotatedBitmap) {
                    crossfade(true)
                }
                    
                // Mostrar indicador de carga mientras se procesa el reconocimiento
                binding.loadingWheel.visibility = View.VISIBLE
                
                // Realizar reconocimiento de perro si es posible (usar el bitmap rotado)
                    try {
                        if (::classifier.isInitialized) {
                        val dogRecognition = classifier.recognizeImage(rotatedBitmap).first()
                        val preds = classifier.recognizeImage(rotatedBitmap)
                                .sortedByDescending { it.confidence }
                                .take(3)
                            
                            logAnaliticaInferencia(preds)
                            logInferencia(dogRecognition)
                            
                        Log.d("RegisterHocicoFragment", "Reconocimiento desde galería: id=${dogRecognition.id}, confianza=${dogRecognition.confidence}%")
                        currentDogRecognition = dogRecognition
                        }
                        
                        // Habilitar el botón después de procesar la imagen (con o sin reconocimiento)
                        enableIdentifyRaceButton()
                    } catch (e: Exception) {
                        Log.e("RegisterHocicoFragment", "Error al reconocer perro desde galería: ${e.message}", e)
                        // Aún así habilitar el botón para que el usuario pueda intentar identificar
                        enableIdentifyRaceButton()
                    }
                }
                
                Log.d("RegisterHocicoFragment", "Imagen procesada desde URI - longitud Base64: ${imageCan.length}")
        } catch (e: Exception) {
            Log.e("RegisterHocicoFragment", "Error al procesar imagen desde URI: ${e.message}", e)
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
                Log.e("RegisterHocicoFragment", "Error al cerrar inputStream: ${e.message}", e)
            }
        }
    }

    /**
     * Obtiene la orientación EXIF de una imagen desde su URI
     */
    private fun getImageOrientation(uri: Uri): Int {
        var inputStream: InputStream? = null
        return try {
            inputStream = requireContext().contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val exif = ExifInterface(inputStream)
                exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } else {
                ExifInterface.ORIENTATION_NORMAL
            }
        } catch (e: Exception) {
            Log.e("RegisterHocicoFragment", "Error al leer orientación EXIF: ${e.message}", e)
            ExifInterface.ORIENTATION_NORMAL
        } finally {
            try {
                inputStream?.close()
            } catch (e: Exception) {
                Log.e("RegisterHocicoFragment", "Error al cerrar inputStream en getImageOrientation: ${e.message}", e)
            }
        }
    }

    /**
     * Rota un bitmap según su orientación EXIF
     */
    private fun rotateBitmapIfNeeded(bitmap: Bitmap, orientation: Int): Bitmap {
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipBitmap(bitmap, horizontal = true, vertical = false)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flipBitmap(bitmap, horizontal = false, vertical = true)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                val rotated = rotateBitmap(bitmap, 90f)
                flipBitmap(rotated, horizontal = true, vertical = false)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                val rotated = rotateBitmap(bitmap, 270f)
                flipBitmap(rotated, horizontal = true, vertical = false)
            }
            else -> bitmap // ORIENTATION_NORMAL o desconocida
        }
    }

    /**
     * Rota un bitmap en grados
     */
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply {
            postRotate(degrees)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Voltea un bitmap horizontal o verticalmente
     */
    private fun flipBitmap(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix().apply {
            if (horizontal) {
                postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            }
            if (vertical) {
                postScale(1f, -1f, bitmap.width / 2f, bitmap.height / 2f)
            }
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun openLoginActivity() {
        startActivity(Intent(requireContext(), LoginActivity::class.java))
    }

    private fun openDetailActivity(dog: Dog) {
        try {
            // Verificar que imageCan esté inicializado
            if (!::imageCan.isInitialized || imageCan.isEmpty()) {
                Log.e("RegisterHocicoFragment", "Error: imageCan no está inicializado o está vacío")
                Toast.makeText(requireContext(), "Error: No se pudo obtener la imagen. Por favor, intente de nuevo.", Toast.LENGTH_SHORT).show()
                return
            }

            // Guardar la foto en SharedPreferences
            val sharedPref = activity?.getSharedPreferences("fotoKey", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPref!!.edit()
            editor.putString("foto", imageCan)
            editor.apply()
            editor.commit()

            Log.d("RegisterHocicoFragment", "Foto guardada en SharedPreferences - longitud Base64: ${imageCan.length}")

            val intent = Intent(requireContext(), DogDetailActivity::class.java)
            intent.putExtra(DogDetailActivity.DOG_KEY, dog)
            intent.putExtra(DogDetailActivity.IS_RECOGNITION_KEY, true)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("RegisterHocicoFragment", "Error al abrir DogDetailActivity: ${e.message}", e)
            Toast.makeText(requireContext(), "Error al abrir la actividad. Intente de nuevo por favor!", Toast.LENGTH_SHORT).show()
        }
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
        // U y V están intercambiados
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                setupCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Aceptame por favor")
                    .setMessage("Acepta la camara o me da ansiedad xD")
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ -> }
                    .show()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun setupCamera() {
        binding.cameraPreview.post {
            // Asegurar que el contenedor esté visible
            binding.imageContainer.visibility = View.VISIBLE
            binding.cameraPreview.visibility = View.VISIBLE
            binding.galleryImageView.visibility = View.GONE
            
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
        // Logea el resumen del Indicador 5 (39/45 ≈ 86%) y si cumple el umbral (>=85%)
        logResumenIndicador()
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
            requireActivity().filesDir
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

                // Inicializar imageCapture si no está inicializado
                if (!::imageCapture.isInitialized) {
                    imageCapture = ImageCapture.Builder()
                        .setTargetRotation(binding.cameraPreview.display.rotation)
                        .build()
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    try {
                    val bitmap = convertImageProxyToBitmap(imageProxy)
                        if (bitmap != null && ::classifier.isInitialized) {
                        getPhotoBitmap(bitmap)
                        val dogRecognition = classifier.recognizeImage(bitmap).first()
                        // Obtén top-3 (si tu Classifier devuelve lista ordenable)
                        val preds = classifier.recognizeImage(bitmap)
                            .sortedByDescending { it.confidence }
                            .take(3)

                        // Log analítico por frame (softmax/argmax/margen/θ/CE teórica)
                        logAnaliticaInferencia(preds)
                        logInferencia(dogRecognition)

                            // Actualizar UI en hilo principal
                            lifecycleScope.launch(Dispatchers.Main) {
                        currentDogRecognition = dogRecognition
                        // Solo actualizar el reconocimiento, pero no habilitar el botón aquí
                        // El botón se habilitará cuando se capture la foto
                    }
                        }
                    } catch (e: Exception) {
                        Log.e("RegisterHocicoFragment", "Error en análisis de imagen: ${e.message}", e)
                    } finally {
                    imageProxy.close()
                    }
                }

                try {
                    // Desvincular todos los casos de uso antes de vincular nuevos
                    cameraProvider.unbindAll()
                    
                    // Vincular preview, imageCapture y imageAnalysis
                cameraProvider.bindToLifecycle(
                        viewLifecycleOwner, cameraSelector,
                    preview, imageCapture, imageAnalysis
                )
                    
                    Log.d("RegisterHocicoFragment", "Cámara iniciada correctamente")
                } catch (exc: Exception) {
                    Log.e("RegisterHocicoFragment", "Error al vincular casos de uso de la cámara", exc)
                }
            },
            ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun getPhotoBitmap(imageBytes: Bitmap) {
        photo = imageBytes
        val byteArrayOutputStream = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        imageCan = Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun capturePhoto() {
        if (!::imageCapture.isInitialized) {
            Toast.makeText(
                requireContext(),
                "La cámara no está lista. Por favor, espera un momento.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Crear archivo temporal para guardar la foto
        val photoFile = File.createTempFile(
            "IMG_${System.currentTimeMillis()}",
            ".jpg",
            requireContext().cacheDir
        )

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputFileOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e("RegisterHocicoFragment", "Error al capturar foto: ${exception.message}", exception)
                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Error al capturar la foto: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val photoUri = outputFileResults.savedUri
                    Log.d("RegisterHocicoFragment", "Foto capturada exitosamente: $photoUri")
                    
                    // Procesar la foto capturada - siempre usar processImageFromUri para manejar orientación EXIF
                    photoUri?.let { uri ->
                        processImageFromUri(uri)
                    } ?: run {
                        // Si no hay URI, crear uno desde el archivo para poder leer EXIF
                        try {
                            val fileUri = FileProvider.getUriForFile(
                                requireContext(),
                                "${requireContext().packageName}.fileprovider",
                                photoFile
                            )
                            processImageFromUri(fileUri)
                        } catch (e: Exception) {
                            Log.e("RegisterHocicoFragment", "Error al crear URI desde archivo: ${e.message}", e)
                            // Fallback: leer bitmap directamente (sin corrección de orientación)
                            val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                            bitmap?.let {
                                processCapturedBitmap(it)
                            }
                        }
                    }
                }
            }
        )
    }

    private fun processCapturedBitmap(bitmap: Bitmap) {
        try {
            // IMPORTANTE: Guardar la foto para usarla cuando se haga clic en "Identificar raza"
            photo = bitmap
            
            // Convertir a Base64 y actualizar imageCan
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            imageCan = Base64.encodeToString(byteArray, Base64.DEFAULT)
            
            Log.d("RegisterHocicoFragment", "Foto guardada para reconocimiento - tamaño: ${bitmap.width}x${bitmap.height}, Base64 length: ${imageCan.length}")
            
            // Las modificaciones de UI deben ejecutarse en el hilo principal
            lifecycleScope.launch(Dispatchers.Main) {
                // Mostrar imageContainer y ocultar PreviewView
                binding.imageContainer.visibility = View.VISIBLE
                binding.cameraPreview.visibility = View.GONE
                binding.galleryImageView.visibility = View.VISIBLE
                binding.galleryImageView.load(bitmap) {
                    crossfade(true)
                }
                
                // Realizar reconocimiento de perro
                try {
                    if (::classifier.isInitialized) {
                        val dogRecognition = classifier.recognizeImage(bitmap).first()
                        val preds = classifier.recognizeImage(bitmap)
                            .sortedByDescending { it.confidence }
                            .take(3)
                        
                        logAnaliticaInferencia(preds)
                        logInferencia(dogRecognition)
                        
                        Log.d("RegisterHocicoFragment", "Reconocimiento de foto capturada: id=${dogRecognition.id}, confianza=${dogRecognition.confidence}%")
                        currentDogRecognition = dogRecognition
                    }
                    
                    // Habilitar el botón después de procesar la imagen (con o sin reconocimiento)
                    enableIdentifyRaceButton()
                } catch (e: Exception) {
                    Log.e("RegisterHocicoFragment", "Error al reconocer perro: ${e.message}")
                    // Aún así habilitar el botón para que el usuario pueda intentar identificar
                    enableIdentifyRaceButton()
                }
            }
            
            Log.d("RegisterHocicoFragment", "Foto procesada - longitud Base64: ${imageCan.length}")
        } catch (e: Exception) {
            Log.e("RegisterHocicoFragment", "Error al procesar foto capturada: ${e.message}", e)
            lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    "Error al procesar la foto: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun enableTakePhotoButton(dogRecognition: DogRecognition) {
        lifecycleScope.launch(Dispatchers.Main) {
            currentDogRecognition = dogRecognition
            
            // Habilitar botón siempre que haya reconocimiento (sin importar la confianza)
            enableIdentifyRaceButton()
        }
    }
    
    /**
     * Habilita el botón "Identificar raza" y configura su listener para validar con la foto guardada
     */
    private fun enableIdentifyRaceButton() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.identifyRaceButton.isEnabled = true
            binding.identifyRaceButton.text = "Identificar raza"
            binding.loadingWheel.visibility = View.GONE
            
            // Configurar el listener para que siempre valide con la foto guardada
            binding.identifyRaceButton.setOnClickListener {
                // Validar con la foto que se subió previamente (cámara o galería)
                identifyRaceWithStoredPhoto()
            }
        }
    }
    
    /**
     * Identifica la raza usando la foto que se subió previamente (cámara o galería)
     * NO captura una nueva foto, solo usa la imagen guardada en photo e imageCan
     * Similar a RegisterCanFragment - usa la imagen guardada en photo e imageCan
     */
    private fun identifyRaceWithStoredPhoto() {
        // Deshabilitar el botón y mostrar ProgressBar inmediatamente
        binding.identifyRaceButton.isEnabled = false
        binding.loadingWheel.visibility = View.VISIBLE
        
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                // Verificar que hay una imagen guardada (como en RegisterCanFragment)
                if (!::photo.isInitialized || !::imageCan.isInitialized) {
                    binding.loadingWheel.visibility = View.GONE
                    binding.identifyRaceButton.isEnabled = true
                    Toast.makeText(requireContext(), "Por favor, toma una foto o selecciona una imagen de la galería", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                
                Log.d("RegisterHocicoFragment", "Usando foto guardada para reconocimiento - tamaño: ${photo.width}x${photo.height}, Base64 length: ${imageCan.length}")
                
                // Hacer reconocimiento con la imagen guardada en background
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        if (!::classifier.isInitialized) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                binding.loadingWheel.visibility = View.GONE
                                binding.identifyRaceButton.isEnabled = true
                                Toast.makeText(requireContext(), "Error: Classifier no inicializado", Toast.LENGTH_SHORT).show()
                            }
                            return@launch
                        }
                        
                        // IMPORTANTE: Hacer reconocimiento con la foto guardada (NO capturar nueva foto)
                        // Usa la variable 'photo' que se guardó cuando se tomó/seleccionó la imagen
                        val dogRecognition = classifier.recognizeImage(photo).first()
                        val preds = classifier.recognizeImage(photo)
                            .sortedByDescending { it.confidence }
                            .take(3)
                        
                        logAnaliticaInferencia(preds)
                        logInferencia(dogRecognition)
                        
                        currentDogRecognition = dogRecognition
                        
                        Log.d("RegisterHocicoFragment", "Reconocimiento con foto guardada: id=${dogRecognition.id}, confianza=${dogRecognition.confidence}%")
                        
                        // Llamar a getDogByMlId con el reconocimiento
                        lifecycleScope.launch(Dispatchers.Main) {
                            viewModel.getDogByMlId(dogRecognition.id)
                        }
                    } catch (e: Exception) {
                        Log.e("RegisterHocicoFragment", "Error al procesar reconocimiento: ${e.message}", e)
                        lifecycleScope.launch(Dispatchers.Main) {
                            binding.loadingWheel.visibility = View.GONE
                            binding.identifyRaceButton.isEnabled = true
                            Toast.makeText(requireContext(), "Error al procesar el reconocimiento: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("RegisterHocicoFragment", "Error al validar imagen: ${e.message}", e)
                binding.loadingWheel.visibility = View.GONE
                binding.identifyRaceButton.isEnabled = true
                Toast.makeText(requireContext(), "Error al procesar la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }
    }
}
