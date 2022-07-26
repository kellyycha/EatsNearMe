package com.example.eatsnearme.googleMaps

import com.google.android.gms.maps.model.LatLng

data class DirectionsResponse (
    val geocoded_waypoints: List<DirectionsGeocodedWaypoint>,
    val routes: List<DirectionsRoute>
)

data class DirectionsGeocodedWaypoint(
    val geocoder_status: String,
    val place_id: String,
)

data class DirectionsRoute(
    val bounds: Bounds,
    val copyrights: String,
    val legs: List<DirectionsLeg>,
    val overview_polyline: DirectionsPolyline,
    val summary: String,
    val warnings: List<String>,
    val waypoint_order: List<Int>,
    val overview_path: ArrayList<LatLng>,
)

data class DirectionsLeg (
    val end_address: String,
    val end_location: LatLngLiteral,
    val start_address: String,
    val start_location: LatLngLiteral,
    val steps: List<DirectionsStep>,
    val traffic_speed_entry: List<DirectionsTrafficSpeedEntry>,
    val via_waypoint: List<DirectionsViaWaypoint>
)

data class DirectionsStep(
    val duration: TextValueObject,
    val end_location: LatLngLiteral,
    val html_instructions: String,
    val polyline: DirectionsPolyline,
    val start_location: LatLngLiteral,
)

data class TextValueObject(
    val text: String,
    val value: Number
)

data class DirectionsTrafficSpeedEntry(
    val offset_meters: Number,
    val speed_category: String
)

data class DirectionsViaWaypoint(
    val location: LatLngLiteral
)

data class DirectionsPolyline(
    val points: String
)

data class Bounds(
    val northeast: 	LatLngLiteral,
    val southwest: 	LatLngLiteral
)

data class LatLngLiteral(
    val lat: Number,
    val lng: Number
)
