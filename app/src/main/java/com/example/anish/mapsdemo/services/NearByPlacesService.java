package com.example.anish.mapsdemo.services;

import com.example.anish.mapsdemo.helper.AppConstants;
import com.example.anish.mapsdemo.models.MainPlacesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by anish on 26-07-2017.
 */

public interface NearByPlacesService {

    @GET(AppConstants.GOOGLE_PLACES_FETCH_URL)
    Call<MainPlacesResponse> getPlaces(@Query("location") String location, @Query("radius") String radius, @Query("types") String type, @Query("sensor") String isSensor, @Query("key") String key);

}
