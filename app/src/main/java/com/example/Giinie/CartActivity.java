package com.example.Giinie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCartItems;
    private CartAdapter cartAdapter;
    private TextView noCartItemsTextView;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
        Button proceedToPaymentButton = findViewById(R.id.proceedToPaymentButton);
        databaseHelper = new DatabaseHelper(this);

        noCartItemsTextView = findViewById(R.id.noCartItemsTextView);
        recyclerViewCartItems = findViewById(R.id.recyclerViewCartItems);

        // Retrieve the skipLogin flag from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        boolean skipLogin = sharedPreferences.getBoolean("skipLogin", false);

        if (!skipLogin) {
            // Retrieve the current user's email from SharedPreferences
            String userEmail = sharedPreferences.getString("userEmail", "");

            // Get the current user's ID based on the email
            long currentUserId = databaseHelper.getUserIdByEmail(userEmail);

            if (currentUserId != -1) {
                // Fetch the cart items for the current user from the database
                List<CartItem> cartItems = databaseHelper.getCartItemsForUser(currentUserId);

                recyclerViewCartItems.setLayoutManager(new LinearLayoutManager(this));
                cartAdapter = new CartAdapter();
                recyclerViewCartItems.setAdapter(cartAdapter);
                cartAdapter.updateCartItems(cartItems);

                // Hide the "no cart items" text view and show the cart items
                noCartItemsTextView.setVisibility(View.GONE);
                proceedToPaymentButton.setVisibility(View.VISIBLE);
                recyclerViewCartItems.setVisibility(View.VISIBLE);
            } else {
                // Display a message indicating that the user needs to log in
                noCartItemsTextView.setVisibility(View.VISIBLE);
                proceedToPaymentButton.setVisibility(View.GONE);
                recyclerViewCartItems.setVisibility(View.GONE);
            }
        } else {
            // Display a message indicating that the user needs to log in
            noCartItemsTextView.setVisibility(View.VISIBLE);
            proceedToPaymentButton.setVisibility(View.GONE);
            recyclerViewCartItems.setVisibility(View.GONE);
        }


        proceedToPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start PaymentActivity with cartItems as an intent extra
                Intent paymentIntent = new Intent(CartActivity.this, PaymentActivity.class);
                paymentIntent.putExtra("cartItems", new ArrayList<>(cartAdapter.getCartItems()));
                startActivity(paymentIntent);
            }
        });
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
