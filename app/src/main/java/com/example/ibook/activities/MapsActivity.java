package com.example.ibook.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ibook.R;
import com.example.ibook.fragment.NotificationsFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

/**
 * The Maps Activity uses the goole maps api to draw the map in the view and let a user select a location.
 * For simplicity there is no search functionality and the users current location is not used.
 * The user can simply move around, zoom in and out and long click or click to set a marker depending if
 * he/she is in edit or view mode.
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String MAP_TYPE = "MAP_TYPE";
    public static final String ADD_EDIT_LOCATION = "ADD_EDIT_LOCATION";
    public static final String VIEW_LOCATION = "VIEW_LOCATION";
    public static final int ADD_EDIT_LOCATION_REQUEST_CODE = 455;
    public static final int VIEW_LOCATION_REQUEST_CODE = 456;
    public static final int ADD_EDIT_LOCATION_RESULT_CODE = 457;
    public static final int VIEW_LOCATION_RESULT_CODE = 458;
    public static final LatLng defaultLocation = new LatLng(52.26815368056734, -113.81286688148975);
    public static final double DEFAULT_ZOOM = 7.0;
    public static final double MARKER_ZOOM = 15.0;

    private Button saveMarker;
    private Button goBack;
    private TextView mapInfo;
    private GoogleMap mMap;
    private Marker marker;
    private LatLng markerLoc = null;
    private String markerText = "Meeting Location";


    private String type = "none";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_maps);

        //Set content view for the map. Will probably be directly implemented in the view message part.
        //Check the type of action code:
        final Intent mapsIntent = getIntent();
        type = mapsIntent.getStringExtra(MAP_TYPE);
        if((boolean) mapsIntent.getBooleanExtra("locationIncluded", false)){
            markerLoc = (LatLng) mapsIntent.getExtras().getParcelable("markerLoc");
            markerText = mapsIntent.getStringExtra("markerText");
        }

        //Set the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFull);
        mapFragment.getMapAsync(this);

        mapInfo = findViewById(R.id.displayMapInfo);
        saveMarker = findViewById(R.id.saveButton);
        goBack = findViewById(R.id.goBackButton);
        //If user is owner then we can choose or edit a location
        if(type!=null) {
            if (type.equals(ADD_EDIT_LOCATION)) {
                saveMarker.setVisibility(View.VISIBLE);
                if(markerLoc!=null) {
                    mapInfo.setText("Edit the current meeting location by clicking a place or long clicking the map.");
                }else{
                    mapInfo.setText("Add the current meeting location by clicking a place or long clicking the map.");
                }
            } else {//Can only view a set location
                saveMarker.setVisibility(View.GONE);
                mapInfo.setText("View the current meeting location. Click on the marker to see the Google Maps options.");
            }
        }


        //Save the marker location specified the user clicking at a location on the map
        saveMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                //Save new marker in the other activity
                if(markerLoc!=null) {
                    intent.putExtra("markerLoc", markerLoc);
                    intent.putExtra("markerText", markerText);
                    intent.putExtra("locationIncluded", true);
                }else{
                    intent.putExtra("locationIncluded", false);
                }
                setResult(ADD_EDIT_LOCATION_RESULT_CODE, intent);
                finish();
            }
        });

        //Go back without adding a marker
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * Sets up the connection with google maps and gets the map ready.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addMarker();
        if(type!=null) {
            if (type.equals(ADD_EDIT_LOCATION)) {
                setMapLongClick();
                setPoiClick();
                setOnDrag();
            }
        }

    }

    /**
     * Adds a marker on the clicked maps location and zooms in on the marker.
     */
    private void addMarker() {
        mMap.clear();
        if(markerLoc!=null) {
            if(type.equals(ADD_EDIT_LOCATION)){
                marker = mMap.addMarker(new MarkerOptions().position(markerLoc).title(markerText).draggable(true));
            }else {
                marker = mMap.addMarker(new MarkerOptions().position(markerLoc).title(markerText));
            }
            marker.showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLoc,(float) MARKER_ZOOM));
        }else{
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation,(float) DEFAULT_ZOOM));

        }
    }

    /**
     * When the user long clicks it updates the map location
     */
    private void setMapLongClick(){
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                markerLoc = latLng;
                markerText = "Meeting Location";
                mapInfo.setText("Move the marker around by long pressing and dragging it.");
                addMarker();
            }
        });
    }

    /**
     * When the user clicks on a point of interes the marker gets updated to that location.
     */
    private void setPoiClick() {
        mMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {
                markerLoc = pointOfInterest.latLng;
                markerText = pointOfInterest.name;
                mapInfo.setText("Move the marker around by long pressing and dragging it.");
                addMarker();
            }
        });
    }

    /**
     * Allows the user to drag the map to move it around.
     */
    private void setOnDrag() {
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
//                markerLoc = marker.getPosition();
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(markerLoc));

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                markerLoc = marker.getPosition();
                markerText = "Meeting Location";
                addMarker();
            }
        });
    }

}


/*References

Android Notes for Professionals. Chapter 23. License(CC BY-SA).

Websites:
"Map Objects". Google Maps Platform. Google Developers.
https://developers.google.com/maps/documentation/android-sdk/map

"Advanced Android 09.1:Google Maps". Google Maps Platform. Google Developers. Feb 18, 2020.
https://developer.android.com/codelabs/advanced-android-training-google-maps?hl=fr#3

j3App. "How to send a LatLng instance to new intent". Stack Overflow. Stack Exchange Inc. June 29, 2018. License(CC BY-SA).
https://stackoverflow.com/questions/16134682/how-to-send-a-latlng-instance-to-new-intent

Juuse, Rene. "Google maps error: Marker's position is not updated after drag". Stack Overflow. Stack Exchange Inc. Feb 20,2015. License(CC BY-SA).
https://stackoverflow.com/questions/14829195/google-maps-error-markers-position-is-not-updated-after-drag

madhu527. "Android Google Maps: disable dragging in MapFragment". Stack Overflow. Stack Exchange Inc. Dec 23, 2014. License(CC BY-SA).
https://stackoverflow.com/questions/16979550/android-google-maps-disable-dragging-in-mapfragment

Mamo, Alex. "How to convert GeoPoint in Firestore to LatLng". Stack Overflow. Stack Exchange Inc. Dec 16, 2018. License(CC BY-SA).
https://stackoverflow.com/questions/53799346/how-to-convert-geopoint-in-firestore-to-latlng

Scarygami. "How to save GeoPoint in Firebase Cloud Firestore?". Stack Overflow. Stack Exchange Inc. Oct 06, 2017. License(CC BY-SA).
https://stackoverflow.com/questions/46603691/how-to-save-geopoint-in-firebase-cloud-firestore

 */