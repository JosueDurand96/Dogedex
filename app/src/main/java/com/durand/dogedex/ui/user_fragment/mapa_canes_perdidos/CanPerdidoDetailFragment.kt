package com.durand.dogedex.ui.user_fragment.mapa_canes_perdidos

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.location.Geocoder
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.durand.dogedex.R
import com.durand.dogedex.data.response.oficial.ListarCanPerdidoResponse
import com.durand.dogedex.databinding.FragmentCanPerdidoDetailBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class CanPerdidoDetailFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentCanPerdidoDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var canPerdido: ListarCanPerdidoResponse
    private var mMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCanPerdidoDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Obtener los argumentos
        canPerdido = arguments?.getSerializable("canPerdido") as? ListarCanPerdidoResponse
            ?: run {
                Log.e("CanPerdidoDetailFragment", "Error: No se recibió canPerdido")
                findNavController().navigateUp()
                return root
            }

        setupUI()
        setupBackButton()
        setupMap()

        return root
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        
        // Intentar usar coordenadas directas primero
        val latitud = canPerdido.latitud
        val longitud = canPerdido.longitud
        
        if (latitud != null && longitud != null && latitud != 0.0 && longitud != 0.0) {
            val location = LatLng(latitud, longitud)
            mMap?.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Lugar de pérdida: ${canPerdido.lugarPerdida}")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            )
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        } else {
            // Si no hay coordenadas, usar Geocoder
            setLocationOnMap(canPerdido.lugarPerdida)
        }
    }

    private fun setLocationOnMap(lugarPerdida: String) {
        if (lugarPerdida.isBlank()) {
            // Si no hay lugar de pérdida, centrar en Lima, Perú por defecto
            val defaultLocation = LatLng(-12.0464, -77.0428) // Lima, Perú
            mMap?.addMarker(
                MarkerOptions()
                    .position(defaultLocation)
                    .title("Ubicación de pérdida")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            )
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))
            return
        }

        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocationName("$lugarPerdida, Lima, Peru", 1)
            
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val location = LatLng(address.latitude, address.longitude)
                
                mMap?.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title("Lugar de pérdida: $lugarPerdida")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                )
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            } else {
                // Si no se encuentra la ubicación, centrar en Lima, Perú
                val defaultLocation = LatLng(-12.0464, -77.0428) // Lima, Perú
                mMap?.addMarker(
                    MarkerOptions()
                        .position(defaultLocation)
                        .title("Ubicación aproximada")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                )
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
                Log.w("CanPerdidoDetailFragment", "No se pudo geocodificar: $lugarPerdida")
            }
        } catch (e: Exception) {
            Log.e("CanPerdidoDetailFragment", "Error al geocodificar ubicación: ${e.message}", e)
            // En caso de error, centrar en Lima, Perú
            val defaultLocation = LatLng(-12.0464, -77.0428) // Lima, Perú
            mMap?.addMarker(
                MarkerOptions()
                    .position(defaultLocation)
                    .title("Ubicación aproximada")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            )
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupUI() {
        // Cargar imagen
        loadImage()

        // Configurar textos con formato
        setupTexts()
    }

    private fun loadImage() {
        if (canPerdido.foto.isNotEmpty()) {
            try {
                val imageBytes = Base64.decode(canPerdido.foto, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                if (bitmap != null) {
                    binding.canImageView.load(bitmap) {
                        crossfade(true)
                        placeholder(R.drawable.dog_logo)
                        error(R.drawable.dog_logo)
                    }
                } else {
                    binding.canImageView.setImageResource(R.drawable.dog_logo)
                }
            } catch (e: Exception) {
                Log.e("CanPerdidoDetailFragment", "Error al decodificar imagen: ${e.message}", e)
                binding.canImageView.setImageResource(R.drawable.dog_logo)
            }
        } else {
            binding.canImageView.setImageResource(R.drawable.dog_logo)
        }
    }

    private fun setupTexts() {
        // Nombre
        binding.nombreTextView.text = formatBoldText("Nombre: ", canPerdido.nombre)

        // Datos del can
        binding.razaTextView.text = formatBoldText("Raza: ", canPerdido.raza)
        binding.especieTextView.text = formatBoldText("Especie: ", canPerdido.especie)
        binding.generoTextView.text = formatBoldText("Género: ", canPerdido.genero)
        binding.tamanioTextView.text = formatBoldText("Tamaño: ", canPerdido.tamanio)
        binding.colorTextView.text = formatBoldText("Color: ", canPerdido.color)
        binding.pelajeTextView.text = formatBoldText("Pelaje: ", canPerdido.pelaje)
        binding.caracterTextView.text = formatBoldText("Carácter: ", canPerdido.caracter)
        binding.esterilizadoTextView.text = formatBoldText("Esterilizado: ", canPerdido.esterelizado)
        binding.distritoTextView.text = formatBoldText("Distrito: ", canPerdido.distrito)
        binding.obtencionTextView.text = formatBoldText("Obtención: ", canPerdido.obtencion)
        binding.tenenciaTextView.text = formatBoldText("Tenencia: ", canPerdido.tenencia)
        binding.fechaNacimientoTextView.text = formatBoldText("Fecha de nacimiento: ", canPerdido.fechaNacimiento)

        // Datos de la pérdida
        val fechaPerdida = try {
            val date = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(canPerdido.fechaPerdida)
                ?: java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(canPerdido.fechaPerdida)
            java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date ?: java.util.Date())
        } catch (e: Exception) {
            canPerdido.fechaPerdida
        }
        binding.fechaPerdidaTextView.text = formatBoldText("Fecha de pérdida: ", fechaPerdida)
        binding.lugarPerdidaTextView.text = formatBoldText("Lugar de pérdida: ", canPerdido.lugarPerdida)
        binding.comentarioTextView.text = formatBoldText("Comentarios: ", canPerdido.comentario)

        // Datos del propietario
        val nombreCompleto = "${canPerdido.nombreUsuario} ${canPerdido.apellidoUsuario}".trim()
        binding.propietarioTextView.text = formatBoldText("Propietario: ", nombreCompleto)
    }

    private fun formatBoldText(prefix: String, value: String): SpannableString {
        val fullText = prefix + value
        val spannableString = SpannableString(fullText)
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            prefix.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

