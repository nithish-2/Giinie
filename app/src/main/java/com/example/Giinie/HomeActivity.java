package com.example.Giinie;

import android.Manifest;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageButton;
import android.view.View;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.EditText;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewServices;
    private HomeServiceAdapter serviceAdapter;
    private List<Service> allHomeServices;


    private TextView currentLocationTextView;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Geocoder geocoder;

    private boolean isLocationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        long userId = getIntent().getLongExtra("userId", 0);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button on the toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back); // Set your back icon here
        }

        // Load the default fragment (HomeFragment) when the activity is created
        loadFragment(new HomeFragment());

        recyclerViewServices = findViewById(R.id.recyclerViewServices);
        recyclerViewServices.setLayoutManager(new GridLayoutManager(this, 3));



        // Initialize the database helper
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Fetch services from the database
        allHomeServices = dbHelper.getAllServices(); // Initialize the class-level allHomeServices list

        // Create the HomeServiceAdapter and set it to the RecyclerView
        serviceAdapter = new HomeServiceAdapter(allHomeServices, new HomeServiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Service service) {
                openServiceDetails(service.getName());
            }
        });
        recyclerViewServices.setAdapter(serviceAdapter);


        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("Search", "Search query: " + charSequence.toString());
                filterServices(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });





        // Initialize the currentLocationTextView
        currentLocationTextView = findViewById(R.id.currentLocationTextView);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateCurrentLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        // Create the Geocoder instance
        geocoder = new Geocoder(this, Locale.getDefault());

        // Request location updates
        if (isLocationPermissionGranted()) {
            startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }

        // Initialize the bottom navigation view and set the item selection listener
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.menu_cart) {
                // Navigate to CartActivity
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
                return true;
            }
            else if (itemId == R.id.menu_orders) {
                // Navigate to OrdersActivity
                Intent ordersIntent = new Intent(HomeActivity.this, OrdersActivity.class);
                startActivity(ordersIntent);
                return true;
            } else if (itemId == R.id.menu_settings) {
                // Navigate to SettingsActivity
                Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            }
            return false;
        });

        // Find the chat button
        ImageButton chatButton = findViewById(R.id.chatFab);

        // Set a click listener for the chat button
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChatScreen();
            }
        });

        // Show "Need help?" text after a delay
        showNeedHelpText();


    }

    private void filterServices(String query) {
        List<Service> filteredServices = new ArrayList<>();

        for (Service service : allHomeServices) {
            if (service.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredServices.add(service);
            }
        }

        serviceAdapter.updateServices(filteredServices);
    }


    // Method to show "Need help?" text after a delay
    private void showNeedHelpText() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView needHelpTextView = findViewById(R.id.needHelpTextView);
                needHelpTextView.setVisibility(View.VISIBLE);
            }
        }, 3000); // Delay in milliseconds (3 seconds)
    }

    // Inflate the menu to add items to the action bar (top toolbar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_navigation_menu, menu);
        return true;
    }

    // Handle actions when items in the action bar (top toolbar) are clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_cart) {
            // Navigate to CartActivity
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isLocationPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            isLocationPermissionGranted = true;
        } catch (SecurityException e) {
            // Handle the case when the location permission is not available
            showPermissionDeniedMessage();
        }
    }




    private void updateCurrentLocation(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            // Get the address from the latitude and longitude
            String address = getAddressFromLocation(latitude, longitude);
            currentLocationTextView.setText("Current Location:\n" + address);
            currentLocationTextView.setGravity(Gravity.LEFT); // Set gravity to left
        } else {
            // Handle the case when the location is not available
            currentLocationTextView.setText("Current Location: N/A");
            currentLocationTextView.setGravity(Gravity.LEFT); // Set gravity to left
        }
    }



    private String getAddressFromLocation(double latitude, double longitude) {
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i));
                    if (i < address.getMaxAddressLineIndex()) {
                        sb.append(", ");
                    }
                }
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Address not found";
    }

    // Handle the result of location permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                startLocationUpdates();
            } else {
                // Permission denied, show a message to the user
                showPermissionDeniedMessage();
            }
        }
    }

    private void showPermissionDeniedMessage() {
        // Use Snackbar to show the message
        Snackbar.make(findViewById(android.R.id.content), "Location permission is required to use this app", Snackbar.LENGTH_INDEFINITE)
                .setAction("Grant Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Request location permission again
                        ActivityCompat.requestPermissions(HomeActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                1);
                    }
                })
                .show();
    }

    private void openServiceDetails(String serviceName) {
        Intent intent = new Intent(this, ServiceDetailsActivity.class);
        intent.putExtra("service_name", serviceName);
        startActivity(intent);
    }

    private void loadFragment(Fragment fragment) {
        // Replace the current fragment with the given fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openChatScreen() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

    // Stop location updates when the activity is stopped
    @Override
    protected void onStop() {
        super.onStop();
        if (isLocationPermissionGranted) {
            locationManager.removeUpdates(locationListener);
        }
    }

}
