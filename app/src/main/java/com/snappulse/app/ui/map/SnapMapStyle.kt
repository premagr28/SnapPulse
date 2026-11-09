package com.snappulse.app.ui.map

object SnapMapStyle {
    // Premium Midnight Dark style customized for Snapchat Snap Map
    val JSON_STYLE = """
    [
      {
        "elementType": "geometry",
        "stylers": [
          { "color": "#181926" }
        ]
      },
      {
        "elementType": "labels.icon",
        "stylers": [
          { "visibility": "off" }
        ]
      },
      {
        "elementType": "labels.text.fill",
        "stylers": [
          { "color": "#757887" }
        ]
      },
      {
        "elementType": "labels.text.stroke",
        "stylers": [
          { "color": "#181926" }
        ]
      },
      {
        "featureType": "administrative",
        "elementType": "geometry",
        "stylers": [
          { "color": "#4b4d5e" }
        ]
      },
      {
        "featureType": "poi",
        "elementType": "labels.text.fill",
        "stylers": [
          { "color": "#63677b" }
        ]
      },
      {
        "featureType": "poi.park",
        "elementType": "geometry",
        "stylers": [
          { "color": "#1e2c2d" }
        ]
      },
      {
        "featureType": "poi.park",
        "elementType": "labels.text.fill",
        "stylers": [
          { "color": "#44755d" }
        ]
      },
      {
        "featureType": "road",
        "elementType": "geometry.fill",
        "stylers": [
          { "color": "#242636" }
        ]
      },
      {
        "featureType": "road",
        "elementType": "labels.text.fill",
        "stylers": [
          { "color": "#8a8ea4" }
        ]
      },
      {
        "featureType": "road.highway",
        "elementType": "geometry",
        "stylers": [
          { "color": "#2e3248" }
        ]
      },
      {
        "featureType": "transit",
        "elementType": "geometry",
        "stylers": [
          { "color": "#222538" }
        ]
      },
      {
        "featureType": "water",
        "elementType": "geometry",
        "stylers": [
          { "color": "#0d111c" }
        ]
      },
      {
        "featureType": "water",
        "elementType": "labels.text.fill",
        "stylers": [
          { "color": "#3d4b68" }
        ]
      }
    ]
    """.trimIndent()
}
