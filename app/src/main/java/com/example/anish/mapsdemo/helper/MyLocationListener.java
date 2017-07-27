package com.example.anish.mapsdemo.helper;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

/* Class My Location Listener */
public class MyLocationListener implements LocationListener {
    private Context context;
    private LocationListener locationListener;

    public MyLocationListener(Context context) {
        this.context = context;
    }

    public void getLocation(Context context, LocationListener locationListener) {
        this.context = context;
        this.locationListener = locationListener;
    }

    @Override
    public void onLocationChanged(Location loc) {

        locationListener.getCurrentLocation(loc);

        loc.getLatitude();
        loc.getLongitude();

        String Text = "My current location is: " +
                "Latitud = " + loc.getLatitude() +
                "Longitud = " + loc.getLongitude();

        Toast.makeText(context, Text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(context, "Gps Disabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(context, "Gps Enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public interface LocationListener {
        void getCurrentLocation(Location loc);
    }
}