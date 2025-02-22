package ru.sicampus.bootcamp2025.ui.mainscreen.allcenters

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ru.sicampus.bootcamp2025.R
import ru.sicampus.bootcamp2025.domain.entities.CenterMapEntity
import ru.sicampus.bootcamp2025.ui.utils.bitmapDescriptorFromVector

class MapService(
    private var centers: List<CenterMapEntity>?,
    private val context: Context,
    private val onClick: (centerId: Int) -> Unit
) : OnMapReadyCallback, OnMarkerClickListener {
    private val markers: HashMap<String, Int> = HashMap()

    fun submitCenters(centers: List<CenterMapEntity>) {
        this.centers = centers
    }

    override fun onMapReady(map: GoogleMap) {
        map.setOnMarkerClickListener(this)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        map.isMyLocationEnabled = true

        centers?.forEach { center ->
            val marker = map.addMarker(
                MarkerOptions()
                    .title(center.name)
                    .position(LatLng(center.latitude, center.longitude))
                    .icon(bitmapDescriptorFromVector(context, R.drawable.marks))
            )
            if (marker != null)
                markers[marker.id] = center.id
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val clickedCenter = markers[marker.id]
        if (clickedCenter != null)
            onClick(clickedCenter)
        return false
    }
}