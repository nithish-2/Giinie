package com.example.Giinie;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {
    RadioGroup paymentMethodGroup;
    RadioButton selectedPaymentMethod;
    Button paymentButton;
    TextView edtName, edtPhone, edtAddress, edtEmail;
    RadioButton radioUseLocation, radioEnterAddress;

    DatabaseHelper databaseHelper;
    LocationTracker locationTracker;

    private String userName;
    private String userPhone;
    private String userAddress;
    private String userEmail;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);



        databaseHelper = new DatabaseHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button on the toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back); // Set your back icon here
        }

        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        edtEmail = findViewById(R.id.edtEmail);
        radioUseLocation = findViewById(R.id.radioUseLocation);
        radioEnterAddress = findViewById(R.id.radioEnterAddress);
        userName = edtName.getText().toString();
        userPhone = edtPhone.getText().toString();
        userAddress = edtAddress.getText().toString();
        userEmail = edtEmail.getText().toString();

        locationTracker = new LocationTracker(this);

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        String userEmail = sharedPreferences.getString("userEmail", "");
        String userName = databaseHelper.getUserNameByEmail(userEmail);

        edtName.setText(userName);
        edtEmail.setText(userEmail);

        paymentMethodGroup = findViewById(R.id.paymentMethodGroup);
        paymentButton = findViewById(R.id.paymentButton);

        long currentUserId = databaseHelper.getUserIdByEmail(userEmail);
        double totalAmount = calculateTotalAmount(currentUserId);
        TextView totalAmountTextView = findViewById(R.id.totalAmountTextView);
        totalAmountTextView.setText("Total Amount (incl. 13% Tax): $" + String.format("%.2f", totalAmount));



        radioUseLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // Get user's current location and set it in the address field
                    Location location = locationTracker.getLocation();
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        String address = getAddressFromLocation(latitude, longitude);
                        edtAddress.setText(address);
                    }
                }
            }
        });

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePayment();
            }
        });
    }
// ... (rest of your imports and class declaration)

    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void handlePayment() {
        int selectedId = paymentMethodGroup.getCheckedRadioButtonId();
        selectedPaymentMethod = findViewById(selectedId);

        if (selectedPaymentMethod == null) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate payment details (card number, expiry date, CVC, etc.)
        boolean isValid = validatePaymentDetails();

        if (isValid) {
            // Display a success alert and provide an option to go back to the home page
            showSuccessAlert();
        } else {
            Toast.makeText(this, "Payment details are not valid", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validatePaymentDetails() {
        // Implement your validation logic here
        // Return true if payment details are valid, otherwise false
        return true; // Placeholder value
    }

    private void showSuccessAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Payment Successful")
                .setMessage("Your payment was successful!")
                .setPositiveButton("Go to Home", (dialog, which) -> {

                    insertOrderDetails();
                    // Start the HomeActivity and finish the PaymentActivity
                    Intent homeIntent = new Intent(PaymentActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void insertOrderDetails() {
        // Get the current user's email from SharedPreferences
        String userEmail = edtEmail.getText().toString();

        // Get the current user's ID based on the email
        long currentUserId = databaseHelper.getUserIdByEmail(userEmail);

        if (currentUserId != -1) {
            // Retrieve the cart items for the current user from the database
            List<CartItem> cartItems = databaseHelper.getCartItemsForUser(currentUserId);

            // Insert order details into the order table
            for (CartItem cartItem : cartItems) {
                long orderId = databaseHelper.insertOrder(
                        currentUserId,
                        cartItem.getServiceName(),
                        cartItem.getServicePlan(),
                        cartItem.getDate(),
                        userName,
                        userPhone,
                        userAddress
                );if (orderId != -1) {
                    //Toast.makeText(this, "Order-Success", Toast.LENGTH_SHORT).show();

                    // Clear the cart table for the specific user
                    databaseHelper.clearCartForUser(currentUserId);
                } else {
                    //Toast.makeText(this, "Order-Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private double calculateTotalAmount(long userId) {
        List<CartItem> cartItems = databaseHelper.getCartItemsForUser(userId);
        double totalAmount = 0;

        for (CartItem cartItem : cartItems) {
            long serviceId = databaseHelper.getServiceIdByName(cartItem.getServiceName());
            double itemPrice = databaseHelper.getItemPriceFromDatabase(serviceId);
            totalAmount += itemPrice;
        }

        // Calculate tax (13%)
        double taxAmount = totalAmount * 0.13;

        // Add tax to total amount
        totalAmount += taxAmount;

        return totalAmount;
    }





}
