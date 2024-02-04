package com.example.travelapp.api;

import com.example.travelapp.model.GooglePlaceList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlaceApi {


    // 구글 nearbySearch api 호출

    @GET("maps/api/place/nearbysearch/json")
    Call<GooglePlaceList> getPlaceList(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("language") String language,
            @Query("keyword") String keyword,
            @Query("key") String apikey);



    @GET("maps/api/place/nearbysearch/json")
    Call<GooglePlaceList> getPlaceList(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("language") String language,
            @Query("keyword") String keyword,
            @Query("pagetoken") String pageToken,
            @Query("key") String apikey);


}
