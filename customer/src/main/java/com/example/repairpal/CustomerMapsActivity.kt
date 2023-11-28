package com.example.repairpal

import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*

class CustomerMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var selectedIssues: ArrayList<String>
    private lateinit var mechanicsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_maps)

        selectedIssues = intent.getStringArrayListExtra("selectedIssues") ?: arrayListOf()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mechanicsRef = FirebaseDatabase.getInstance().reference.child("mechanics")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        displaySelectedIssuesMarkers()
        fetchMechanicsLocations()
    }

    private fun displaySelectedIssuesMarkers() {
        selectedIssues.forEach { issue ->
            val issueLatLng = getLatLngForIssue(issue)
            placeMarkerOnMap(issueLatLng, issue)
        }
    }

    private fun getLatLngForIssue(issue: String): LatLng {
        // Implement your logic to get LatLng for each issue
        return LatLng(0.0, 0.0) // Replace with actual LatLng
    }

    private fun placeMarkerOnMap(location: LatLng, issue: String) {
        val markerOptions = MarkerOptions().position(location).title("$issue location")
        mMap.addMarker(markerOptions)
    }

    private fun fetchMechanicsLocations() {
        mechanicsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (mechanicSnapshot in dataSnapshot.children) {
                    val mechanicLocation = LatLng(
                        mechanicSnapshot.child("latitude").value as Double,
                        mechanicSnapshot.child("longitude").value as Double
                    )
                    placeMechanicMarkerOnMap(mechanicLocation)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun placeMechanicMarkerOnMap(location: LatLng) {
        mMap.addMarker(MarkerOptions().position(location).title("Mechanic"))
    }

    // Other methods and overrides...
}

