package com.durand.dogedex.ui.user_fragment.list_my_can_register

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
import com.durand.dogedex.data.response.oficial.ListarCanResponse
import com.durand.dogedex.databinding.FragmentMyCanRegisterDetailBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MyCanRegisterDetailFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMyCanRegisterDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var can: ListarCanResponse
    private var mMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCanRegisterDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Obtener los argumentos
        can = arguments?.getSerializable("can") as? ListarCanResponse
            ?: run {
                Log.e("MyCanRegisterDetailFragment", "Error: No se recibió can")
                findNavController().navigateUp()
                return root
            }

        setupUI()
        setupMap()

        return root
    }

    private fun setupUI() {
        // Cargar imagen
        loadImage()

        // Configurar textos con formato
        setupTexts()
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Primero intentar usar coordenadas exactas si están disponibles
        if (can.latitud != null && can.longitud != null && 
            can.latitud != 0.0 && can.longitud != 0.0) {
            val exactLocation = LatLng(can.latitud!!, can.longitud!!)
            mMap?.addMarker(
                MarkerOptions()
                    .position(exactLocation)
                    .title("Ubicación del can: ${can.nombre}")
            )
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(exactLocation, 16f))
            Log.d("MyCanRegisterDetailFragment", "Usando coordenadas exactas: lat=${can.latitud}, lng=${can.longitud}")
        } else {
            // Si no hay coordenadas exactas, usar distrito
            setLocationOnMap(can.distrito)
        }
    }

    private fun setLocationOnMap(distrito: String) {
        if (distrito.isBlank()) {
            // Si no hay distrito, centrar en Lima, Perú por defecto
            val defaultLocation = LatLng(-12.0464, -77.0428) // Lima, Perú
            mMap?.addMarker(MarkerOptions().position(defaultLocation).title("Ubicación del can"))
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))
            return
        }

        // Primero intentar usar coordenadas conocidas de los distritos de Lima
        val knownLocation = getKnownDistrictCoordinates(distrito)
        if (knownLocation != null) {
            mMap?.addMarker(
                MarkerOptions()
                    .position(knownLocation)
                    .title("Distrito: $distrito")
            )
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(knownLocation, 15f))
            Log.d("MyCanRegisterDetailFragment", "Usando coordenadas conocidas para: $distrito")
            return
        }

        // Si no hay coordenadas conocidas, intentar geocodificar
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            // Buscar usando el distrito en Lima, Perú
            val searchQuery = "$distrito, Lima, Perú"
            val addresses = geocoder.getFromLocationName(searchQuery, 1)
            
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val location = LatLng(address.latitude, address.longitude)
                
                mMap?.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title("Distrito: $distrito")
                )
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                Log.d("MyCanRegisterDetailFragment", "Geocodificado: $distrito -> ${location.latitude}, ${location.longitude}")
            } else {
                // Si no se encuentra la ubicación, centrar en Lima, Perú
                val defaultLocation = LatLng(-12.0464, -77.0428) // Lima, Perú
                mMap?.addMarker(
                    MarkerOptions()
                        .position(defaultLocation)
                        .title("Ubicación aproximada")
                )
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
                Log.w("MyCanRegisterDetailFragment", "No se pudo geocodificar: $distrito")
            }
        } catch (e: Exception) {
            Log.e("MyCanRegisterDetailFragment", "Error al geocodificar ubicación: ${e.message}", e)
            // En caso de error, centrar en Lima, Perú
            val defaultLocation = LatLng(-12.0464, -77.0428) // Lima, Perú
            mMap?.addMarker(
                MarkerOptions()
                    .position(defaultLocation)
                    .title("Ubicación aproximada")
            )
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
        }
    }

    private fun getKnownDistrictCoordinates(distrito: String): LatLng? {
        // Mapa de coordenadas conocidas de distritos de Lima
        val distritoLower = distrito.lowercase().trim()
        val districtCoordinates = mapOf(
            "rímac" to LatLng(-12.0386, -77.0244),
            "rimac" to LatLng(-12.0386, -77.0244),
            "miraflores" to LatLng(-12.1198, -77.0303),
            "san isidro" to LatLng(-12.0989, -77.0361),
            "san borja" to LatLng(-12.0935, -77.0087),
            "santiago de surco" to LatLng(-12.1490, -76.9938),
            "la molina" to LatLng(-12.0750, -76.9311),
            "san miguel" to LatLng(-12.0775, -77.0936),
            "magdalena del mar" to LatLng(-12.0953, -77.0700),
            "pueblo libre" to LatLng(-12.0719, -77.0625),
            "jesús maría" to LatLng(-12.0844, -77.0486),
            "lince" to LatLng(-12.0864, -77.0389),
            "la victoria" to LatLng(-12.0728, -77.0228),
            "breña" to LatLng(-12.0575, -77.0464),
            "cercado de lima" to LatLng(-12.0464, -77.0428),
            "lima" to LatLng(-12.0464, -77.0428),
            "san juan de lurigancho" to LatLng(-11.9833, -76.9983),
            "san martín de porres" to LatLng(-11.9817, -77.0744),
            "comas" to LatLng(-11.9319, -77.0547),
            "carabayllo" to LatLng(-11.8550, -77.0250),
            "los olivos" to LatLng(-11.9636, -77.0711),
            "independencia" to LatLng(-11.9911, -77.0486),
            "pachacamac" to LatLng(-12.2375, -76.8547),
            "villa el salvador" to LatLng(-12.2014, -76.9569),
            "san juan de miraflores" to LatLng(-12.1686, -76.9917),
            "villa maría del triunfo" to LatLng(-12.1492, -76.9539),
            "chorrillos" to LatLng(-12.1681, -77.0147),
            "barranco" to LatLng(-12.1431, -77.0219),
            "surquillo" to LatLng(-12.1242, -77.0250),
            "barranco" to LatLng(-12.1431, -77.0219),
            "chaclacayo" to LatLng(-11.9761, -76.7817),
            "ate" to LatLng(-12.0181, -76.8614),
            "san luis" to LatLng(-12.0744, -77.0069),
            "el agustino" to LatLng(-12.0217, -76.9883)
        )
        return districtCoordinates[distritoLower]
    }

    private fun loadImage() {
        if (can.foto.isNotEmpty()) {
            try {
                val imageBytes = Base64.decode(can.foto, Base64.DEFAULT)
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
                Log.e("MyCanRegisterDetailFragment", "Error al decodificar imagen: ${e.message}", e)
                binding.canImageView.setImageResource(R.drawable.dog_logo)
            }
        } else {
            binding.canImageView.setImageResource(R.drawable.dog_logo)
        }
    }

    private fun setupTexts() {
        // Nombre
        binding.nombreTextView.text = formatBoldText("Nombre: ", can.nombre)

        // Datos del can
        binding.razaTextView.text = formatBoldText("Raza: ", can.raza)
        binding.especieTextView.text = formatBoldText("Especie: ", can.especie)
        binding.generoTextView.text = formatBoldText("Género: ", can.genero)
        binding.tamanioTextView.text = formatBoldText("Tamaño: ", can.tamanio)
        binding.colorTextView.text = formatBoldText("Color: ", can.color)
        binding.pelajeTextView.text = formatBoldText("Pelaje: ", can.pelaje)
        binding.caracterTextView.text = formatBoldText("Carácter: ", can.caracter)
        binding.esterilizadoTextView.text = formatBoldText("Esterilizado: ", can.esterelizado)
        binding.distritoTextView.text = formatBoldText("Distrito: ", can.distrito)
        binding.obtencionTextView.text = formatBoldText("Obtención: ", can.obtencion)
        binding.tenenciaTextView.text = formatBoldText("Tenencia: ", can.tenencia)
        binding.fechaNacimientoTextView.text = formatBoldText("Fecha de nacimiento: ", can.fechaNacimiento)

        // Datos del propietario
        val nombreCompleto = "${can.nombreUsuario} ${can.apellidoUsuario}".trim()
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


