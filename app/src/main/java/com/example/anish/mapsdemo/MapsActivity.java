package com.example.anish.mapsdemo;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.anish.mapsdemo.helper.AppConstants;
import com.example.anish.mapsdemo.models.MainPlacesResponse;
import com.example.anish.mapsdemo.models.Result;
import com.example.anish.mapsdemo.services.NearByPlacesService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText edtPlace, edtType;
    private NearByPlacesService placesService = AppApplication.getRetrofit().create(NearByPlacesService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        init();
    }

    private void init() {
        //init views

        edtPlace = (EditText) findViewById(R.id.edtPlace);
        edtType = (EditText) findViewById(R.id.edtType);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(22.3100187, 73.1732515);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    // after filling the edit text for place, clicking on the search button will
    //call onSearch method
    public void onSearch(View v) {
        String location = edtPlace.getText().toString();
        String type = edtType.getText().toString();
        List<Address> addressList = null;
        if (location != null && !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
               /* mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));*/
                    moveToCurrentLocation(latLng, type);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void moveToCurrentLocation(LatLng latLong, String type) {
        mMap.clear();
        //addMarker
        mMap.addMarker(new MarkerOptions().position(latLong).title("Marker"));
        //Build camera position
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLong)
                .zoom(17).build();
        //Zoom in and animate the camera.
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        placesService.getPlaces("22.3100187,73.1732515"
                , AppConstants.GOOGLE_PLACES_SEARCH_RADIUS, type
                , AppConstants.GOOGLE_PLACES_SEARCH_DEFAULT_SENSOR
                , "AIzaSyDC05eOvQIlvFqwmJB7xXi3-ey78xlTpn4").enqueue(new Callback<MainPlacesResponse>() {


            @Override
            public void onResponse(Call<MainPlacesResponse> call, Response<MainPlacesResponse> response) {
                if (response.isSuccessful())
                    if (response.body().getResults().size() > 0 && response.body().getResults() != null) {
                        List<Result> results = response.body().getResults();
                        for (Result resultsModel : results) {
                            plotPharmacyOnMap(new LatLng(resultsModel.geometry.location.lat, resultsModel.geometry.location.lng), resultsModel.name);
                        }
                    } else {
                        Toast.makeText(MapsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
            }


            @Override
            public void onFailure(Call<MainPlacesResponse> call, Throwable t) {
                Toast.makeText(MapsActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void plotPharmacyOnMap(LatLng latLng, String title) {
        // Creating a marker
        /**
         * https://developers.google.com/places/supported_types
         * it should be from the list mentioned on the site
         * otherwise it will just return the name of hotels  ;)
         */
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(title);
        Log.e("place", title);
        mMap.addMarker(markerOptions);
    }

}

/*
                            //\\
                           ((***))     ___o.0___
 _/""""""""""""""""\__       ||           ||
 -@-----------------@-       ||          /  \
/////////////////////////////////////////////////////


¶▅c●▄███████||▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅▅||█~ ::~ :~ :►
▄██ ▲  █ █ ██▅▄▃▂
███▲ ▲ █ █ ███████
███████████████████████►
◥☼▲⊙▲⊙▲⊙▲⊙▲⊙▲⊙▲⊙☼◤

*/
