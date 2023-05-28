package com.durand.dogedex.ui.admin_fragment.ui.propietarios_can_peligroso

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.durand.dogedex.R
import com.durand.dogedex.data.response.can_perdido.ListCanPerdido
import com.durand.dogedex.databinding.FragmentCanPerdidosGeoBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CanPeligrosoGeoFragment : Fragment() , OnMapReadyCallback{

    private var _binding: FragmentCanPerdidosGeoBinding? = null
    private lateinit var viewModel: ListCanPeligrosoPerdidasViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private var locationArrayList: ArrayList<LatLng>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ListCanPeligrosoPerdidasViewModel::class.java)

        _binding = FragmentCanPerdidosGeoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val map = childFragmentManager!!.findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync(this)

        viewModel.startReportCanesRegistrados()
        viewModel.listCanes.observe(requireActivity()){
           mapGoogleApi(it)
        }


        return root
    }

    private fun mapGoogleApi(list: List<ListCanPerdido>){
        Log.d("mapGoogleApi", "mapGoogleApi: $list")
        locationArrayList = ArrayList()
        for (i in list.indices){
            val user = LatLng(list[i].latitud.toDouble(),list[i].longitud.toDouble())
            Log.d("mapGoogleApi", "mapGoogleApi : $user")
            locationArrayList!!.add(user)
        }
        Log.d("mapGoogleApi", "mapGoogleApi : $locationArrayList")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Handler(Looper.getMainLooper()).postDelayed({
                // This method will be executed once the timer is over
            try {
                mMap = googleMap
                for (i in locationArrayList!!.indices){
                    mMap.addMarker(MarkerOptions().position(locationArrayList!![i]).title("Can Peligroso!"))
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(20.0f))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(locationArrayList!![1]))
                }
            }catch (e:Exception){

            }
            },
            2000 // value in milliseconds
        )


    }


}