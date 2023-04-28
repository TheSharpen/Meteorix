package com.example.meteorix

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.meteorix.databinding.MeteoriteDetailFragmentBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MeteoriteDetailFragment : Fragment() {

    private lateinit var binding: MeteoriteDetailFragmentBinding

    private lateinit var mvMeteoriteLocation: MapView
    private lateinit var googleMap: GoogleMap
    private val markerOptions = MarkerOptions()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = MeteoriteDetailFragmentBinding.inflate(inflater, container, false)

        Log.d("XLOG", "$arguments")
        val reclat = arguments?.getString("meteoriteReclat") ?: "48.1482"
        val reclong = arguments?.getString("meteoriteReclong") ?: "17.1077"

        mvMeteoriteLocation = binding.mvMeteoriteLocation
        mvMeteoriteLocation.onCreate(savedInstanceState)
        mvMeteoriteLocation.getMapAsync { map ->
            googleMap = map
            googleMap.uiSettings.isZoomControlsEnabled = true
            val location = LatLng(reclat.toDouble(), reclong.toDouble())
            markerOptions.position(location)
            googleMap.addMarker(markerOptions)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
        }



        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val meteoriteName = arguments?.getString("meteoriteName")
        val meteoriteFell = arguments?.getString("meteoriteFell")
        val meteoriteYear = arguments?.getString("meteoriteYear")
        val meteoriteMass = arguments?.getString("meteoriteMass")
        val meteoriteNametype = arguments?.getString("meteoriteNametype")

        binding.tvMeteoriteName.text = meteoriteName
        binding.tvMeteoriteFell.text = "Fell/Found: $meteoriteFell"

        if (binding.tvMeteoriteYear != null) {
            binding.tvMeteoriteYear.text = "Discovered: ${meteoriteYear?.substring(0, 4)}"
        } else {
            binding.tvMeteoriteYear.text = "Discovered: Unknown"
        }

        binding.tvMeteoriteMass.text = "Weight (g): $meteoriteMass"
        binding.tvMeteoriteOfficial.text = "Record type: $meteoriteNametype"


    }


    override fun onResume() {
        super.onResume()
        mvMeteoriteLocation.onResume()
    }

    override fun onPause() {
        super.onPause()
        mvMeteoriteLocation.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mvMeteoriteLocation.onDestroy()
    }

    companion object {

        fun newInstance(meteoriteName: String): MeteoriteDetailFragment {
            val args = Bundle().apply {
                putString("meteoriteName", meteoriteName)
            }
            return MeteoriteDetailFragment().apply {
                arguments = args
            }
        }
    }

}