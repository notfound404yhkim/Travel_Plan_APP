package com.example.travelapp.model;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.app.Service;

public class LocationUpdate extends Service {

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // 위치 업데이트 처리
                saveLocationToSharedPreferences(location);
            }
        };

        // 위치 권한 체크 및 위치 업데이트 요청
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    3000,
                    -1,
                    locationListener
            );
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    3000,
                    -1,
                    locationListener
            );
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void saveLocationToSharedPreferences(Location location) {
        SharedPreferences sharedPreferences = getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("latitude", (float) location.getLatitude());
        editor.putFloat("longitude", (float) location.getLongitude());
        editor.apply();
    }
}