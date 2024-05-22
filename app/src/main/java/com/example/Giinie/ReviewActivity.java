package com.example.Giinie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ReviewActivity extends AppCompatActivity {

    private EditText reviewEditText;
    private RatingBar ratingBar;
    private Button submitButton;

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    private long orderId; // You need to retrieve this from the intent
    private String servicePlanName; // You need to retrieve this from the intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Initialize DatabaseHelper and SharedPreferences
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        // Retrieve data from the intent
        Intent intent = getIntent();
        orderId = intent.getLongExtra("order_id", -1);
        servicePlanName = intent.getStringExtra("service_plan_name");

        reviewEditText = findViewById(R.id.reviewsEditText);
        ratingBar = findViewById(R.id.ratingBar);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(view -> {
            // Retrieve review text and rating
            String reviewText = reviewEditText.getText().toString();
            float rating = ratingBar.getRating();

            // Retrieve the user's ID based on the email
            String userEmail = sharedPreferences.getString("userEmail", "");
            long userId = databaseHelper.getUserIdByEmail(userEmail);

            // Store the review details in the database
            long reviewId = databaseHelper.insertReview(userId, servicePlanName, reviewText, rating);

            if (reviewId != -1) {
                // Show a success message or perform other actions
                Toast.makeText(this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // Show an error message if the review couldn't be inserted
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Failed to submit review. Please try again.");
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        });
    }
}
