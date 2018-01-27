package com.github.eis.myandroidapp.map

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.eis.myandroidapp.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment




class MapsFragment : Fragment(), OnMapReadyCallback {

    var onMapReadyFunc:((GoogleMap) -> Unit)? = null

    override fun onMapReady(googleMap: GoogleMap) {
        onMapReadyFunc?.invoke(googleMap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onResume() {
        super.onResume()

        val smf = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        smf.getMapAsync(this)
    }

    companion object {
        fun newInstance(): MapsFragment {
            return MapsFragment()
        }
    }
}