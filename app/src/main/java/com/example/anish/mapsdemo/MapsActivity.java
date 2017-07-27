package com.example.anish.mapsdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.anish.mapsdemo.helper.AppConstants;
import com.example.anish.mapsdemo.helper.DataParser;
import com.example.anish.mapsdemo.helper.Functions;
import com.example.anish.mapsdemo.models.MainPlacesResponse;
import com.example.anish.mapsdemo.models.Result;
import com.example.anish.mapsdemo.services.NearByPlacesService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.gun0912.tedpermission.PermissionListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap, mSDMark;
    private EditText edtPlace, edtType;
    private Context context;
    private NearByPlacesService placesService = AppApplication.getRetrofit().create(NearByPlacesService.class);
    private LatLng ivory;
    private int count = 0;
    private LatLng destination;
    private float dist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_maps);
        init();
    }

    private void init() {
        //init views
        edtPlace = (EditText) findViewById(R.id.edtPlace);
        edtType = (EditText) findViewById(R.id.edtType);


        Functions.setPermission(context, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.INTERNET
                , Manifest.permission.ACCESS_COARSE_LOCATION}, new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(MapsActivity.this);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(context, "Sorry action not available", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


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
        mSDMark = googleMap;

        // Add a marker in Sydney and move the camera
        ivory = new LatLng(22.3100187, 73.1732515);
        mMap.addMarker(new MarkerOptions().position(ivory).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ivory));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Sorry action not available", Toast.LENGTH_SHORT).show();
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    // after filling the edit text for place, clicking on the search button will
    //call onSearch method
    public void onSearch(View v) {
        count = 0;
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
                            plotTypesOnMap(new LatLng(resultsModel.geometry.location.lat, resultsModel.geometry.location.lng), resultsModel.name);
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

    private void plotTypesOnMap(LatLng latLng, String title) {
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
        if (count == 0) {
            destination = latLng;
        }
        count++;
    }

    private void plotRoute(LatLng src, LatLng dest) {


        ArrayList<LatLng> markerPoints = new ArrayList<>();

        markerPoints.add(src);
        markerPoints.add(dest);

        // Creating MarkerOptions
        MarkerOptions source = new MarkerOptions();
        MarkerOptions goal = new MarkerOptions();

        source.position(src);
        source.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        goal.position(dest);
        goal.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));


        mSDMark.addMarker(source);
        mSDMark.addMarker(goal);

        String url = getUrl(markerPoints.get(0), markerPoints.get(1));

        Log.d("getUrl:", url.toString());
        FetchUrl fetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        fetchUrl.execute(url);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ivory));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

    }

    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    public void clickRoute(View view) {
        plotRoute(ivory, destination);
        Toast.makeText(context, "Distance:" + calculateDistance(), Toast.LENGTH_SHORT).show();
    }

    private float calculateDistance() {
        Location locationA = new Location("point A");
        locationA.setLatitude(ivory.latitude);
        locationA.setLongitude(ivory.longitude);

        Location locationB = new Location("point B");
        locationB.setLatitude(destination.latitude);
        locationB.setLongitude(destination.longitude);

        dist = locationA.distanceTo(locationB);
        return dist;
    }


    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }
}
