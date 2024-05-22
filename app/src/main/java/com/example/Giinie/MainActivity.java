package com.example.Giinie;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private Button loginButton;
    private Button skipButton;
    private Button signUpButton;
    private Button signInButton;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private VideoView backgroundVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        backgroundVideoView = findViewById(R.id.backgroundVideoView);

        // Set the path to the GIF video file in the res/raw directory
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.backgroundvideo;

        // Play the GIF video
        backgroundVideoView.setVideoURI(Uri.parse(videoPath));
        backgroundVideoView.start();
        backgroundVideoView.setOnPreparedListener(mp -> mp.setLooping(true));

        loginButton = findViewById(R.id.loginButton);
        skipButton = findViewById(R.id.skipButton);

        loginButton.setOnClickListener(v -> openLoginScreen());

        skipButton.setOnClickListener(v -> openHomeScreen());

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                // Do something with the retrieved latitude and longitude
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        // Check and request location permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        try {
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied. GPS functionality will not work.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void openHomeScreen() {
        // Set the skipLogin flag in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("skipLogin", true);
        editor.apply();

        // Start HomeActivity
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }





}
