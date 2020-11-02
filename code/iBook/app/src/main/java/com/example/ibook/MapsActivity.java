package com.example.ibook;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set content view for the map. Will probably be directly implemented in the view message part.

        //Set the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng edmonton = new LatLng(53.532711, -113.474052);
        mMap.addMarker(new MarkerOptions().position(edmonton).title("Maker in Edmonton"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(edmonton));

    }
}


/*Resources

Android Notes for Professionals. Chapter 23. License(CC BY-SA).

Websites:
"Map Objects". Google Maps Platform. Google Developers.
https://developers.google.com/maps/documentation/android-sdk/map

 */