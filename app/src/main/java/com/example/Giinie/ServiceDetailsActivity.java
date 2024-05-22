package com.example.Giinie;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServiceDetailsActivity extends AppCompatActivity implements ServicePlansAdapter.OnPlanSelectedListener {

    private TextView serviceNameTextView;
    private RecyclerView recyclerViewServicePlans;
    private ServicePlansAdapter servicePlansAdapter;
    private Button selectDateTimeButton;
    private Button addToCartButton;
    private List<CartItem> cartItems;

    private DatabaseHelper dbHelper;
    private Calendar selectedDateTime;

    private EditText commentsEditText;
    private Button uploadPhotoButton;
    private static final int REQUEST_IMAGE_CAPTURE = 1002;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 1001;



    private ImageView capturedImageView;
    private Uri imageUri;
    private Uri cameraPhotoUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button on the toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back); // Set your back icon here
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }


        serviceNameTextView = findViewById(R.id.serviceNameTextView);
        recyclerViewServicePlans = findViewById(R.id.recyclerViewServicePlans);
        selectDateTimeButton = findViewById(R.id.selectDateTimeButton);
        addToCartButton = findViewById(R.id.addToCartButton);
        // Initialize views
        commentsEditText = findViewById(R.id.commentsEditText);
        uploadPhotoButton = findViewById(R.id.uploadPhotoButton);
        capturedImageView = findViewById(R.id.capturedImageView);

        Button uploadPhotoButton = findViewById(R.id.uploadPhotoButton);


        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show an options dialog for the user to choose between camera and gallery
                AlertDialog.Builder builder = new AlertDialog.Builder(ServiceDetailsActivity.this);
                builder.setTitle("Upload Photo")
                        .setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        // Launch the camera to capture a photo
                                        dispatchTakePictureIntent();
                                        break;
                                    case 1:
                                        // Open the gallery to select an image
                                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });


        dbHelper = new DatabaseHelper(this);
        cartItems = new ArrayList<>();

        // Get the service name from the intent
        String serviceName = getIntent().getStringExtra("service_name");

        // Set the service name in the TextView
        serviceNameTextView.setText(serviceName);

        // Retrieve the list of services and their plans from the database
        List<Service> allServices = dbHelper.getAllServices();
        Service selectedService = null;
        for (Service service : allServices) {
            if (service.getName().equals(serviceName)) {
                selectedService = service;
                break;
            }
        }

        // Get the plans for the selected service
        List<ServicePlan> servicePlans = dbHelper.getServicePlansByService(selectedService.getName());

        // Set prices for Basic, Standard, and Premium plans
        double basicPrice = selectedService.getPriceBasic();
        double standardPrice = selectedService.getPriceStandard();
        double premiumPrice = selectedService.getPricePremium();

// Update the plans with the prices
        for (ServicePlan plan : servicePlans) {
            double price;
            if (plan.getName().contains("Basic")) {
                price = basicPrice;
            } else if (plan.getName().contains("Standard")) {
                price = standardPrice;
            } else if (plan.getName().contains("Premium")) {
                price = premiumPrice;
            } else {
                // Handle other plan names or set a default price
                price = 0.0;
            }

            plan.setPrice(price); // Set the calculated price for the plan
        }



        // Create and set up the ServicePlansAdapter
        servicePlansAdapter = new ServicePlansAdapter(servicePlans, this);
        recyclerViewServicePlans.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewServicePlans.setAdapter(servicePlansAdapter);

        // Handle selectDateTimeButton click
        selectDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePickerDialog();
            }
        });

        // Handle addToCartButton click
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDateTime == null) {
                    // Show a message or handle the case where a date/time isn't selected
                    Toast.makeText(ServiceDetailsActivity.this, "Please select a date and time.", Toast.LENGTH_SHORT).show();
                    return;
                }

                ServicePlan selectedPlan = servicePlansAdapter.getSelectedPlan();
                if (selectedPlan == null) {
                    // Show a message or handle the case where a plan isn't selected
                    Toast.makeText(ServiceDetailsActivity.this, "Please select a plan.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check for other validations here
                // For example, validate commentsEditText or other fields

                // All validations passed, add to cart
                String selectedPlanName = selectedPlan.getName();
                CartItem cartItem = new CartItem(serviceName, selectedPlanName, selectedDateTime.getTime());

                // Get the user's ID (replace with the actual method)
                long userId = getUserID();

                // Insert the cart item into the database
                long insertedRowId = dbHelper.insertCartItem(cartItem, userId);

                if (insertedRowId != -1) {
                    // Handle the addition to the cart
                    // You can update the cart UI or perform any other action here
                    AlertDialog.Builder builder = new AlertDialog.Builder(ServiceDetailsActivity.this);
                    builder.setTitle("Service Added to Cart")
                            .setMessage("The service has been added to your cart.")
                            .setPositiveButton("Go to Cart", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Navigate to the CartActivity
                                    Intent cartIntent = new Intent(ServiceDetailsActivity.this, CartActivity.class);
                                    startActivity(cartIntent);
                                }
                            })
                            .setNegativeButton("Continue Scheduling", null)
                            .show();
                } else {
                    // Handle the case where insertion failed
                    // For example, show an error message
                    Toast.makeText(ServiceDetailsActivity.this, "Failed to add to cart. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    // Method to retrieve user ID (replace with your actual implementation)
// Method to retrieve user ID using DatabaseHelper
    private long getUserID() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", "");

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        return dbHelper.getUserIdByEmail(userEmail);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, launch camera
                dispatchTakePictureIntent();
            } else {
                // Permission denied, handle accordingly
                // For example, show a message or disable the "Take Photo" button
            }
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Handle exception if necessary
            }

            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this, "com.example.myapplication.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }




    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Display the captured photo in the ImageView
            capturedImageView.setImageURI(imageUri);
            capturedImageView.setVisibility(View.VISIBLE);
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            // Handle selected image from gallery
            Uri selectedImage = data.getData();
            capturedImageView.setImageURI(selectedImage);
            capturedImageView.setVisibility(View.VISIBLE);
        }
    }


    private void showDateTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ServiceDetailsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selectedDateTime = Calendar.getInstance();
                                selectedDateTime.set(Calendar.YEAR, year);
                                selectedDateTime.set(Calendar.MONTH, monthOfYear);
                                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDateTime.set(Calendar.MINUTE, minute);

                                // Handle the selected date and time
                                // You can update a TextView or perform any other action here
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onPlanSelected(ServicePlan plan) {
        // Handle plan selection if needed
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
