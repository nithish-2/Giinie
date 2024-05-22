package com.example.Giinie;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrderItems;
    private TextView noOrdersItemsTextView;
    private Button goToHomeButton;
    private DatabaseHelper databaseHelper;
    private OrdersAdapter ordersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        recyclerViewOrderItems = findViewById(R.id.recyclerViewOrderItems);
        noOrdersItemsTextView = findViewById(R.id.noOrdersItemsTextView);
        goToHomeButton = findViewById(R.id.goToHomeButton);
        databaseHelper = new DatabaseHelper(this);

        // Retrieve the skipLogin flag from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        boolean skipLogin = sharedPreferences.getBoolean("skipLogin", false);

        if (!skipLogin) {
            // Retrieve the current user's email from SharedPreferences
            String userEmail = sharedPreferences.getString("userEmail", "");

            // Get the current user's ID based on the email
            long currentUserId = databaseHelper.getUserIdByEmail(userEmail);

            if (currentUserId != -1) {
                // Fetch the order details for the current user from the database
                List<Order> orders = databaseHelper.getOrdersForUser(currentUserId);

                if (!orders.isEmpty()) {
                    recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(this));
                    ordersAdapter = new OrdersAdapter(orders);
                    recyclerViewOrderItems.setAdapter(ordersAdapter);

                    // Hide the "no orders items" text view and show the order items
                    noOrdersItemsTextView.setVisibility(View.GONE);
                    recyclerViewOrderItems.setVisibility(View.VISIBLE);
                } else {
                    // Display a message indicating no orders available
                    noOrdersItemsTextView.setVisibility(View.VISIBLE);
                    recyclerViewOrderItems.setVisibility(View.GONE);
                }
            } else {
                // Display a message indicating that the user needs to log in
                noOrdersItemsTextView.setVisibility(View.VISIBLE);
                recyclerViewOrderItems.setVisibility(View.GONE);
            }
        } else {
            // Display a message indicating that the user needs to log in
            noOrdersItemsTextView.setVisibility(View.VISIBLE);
            recyclerViewOrderItems.setVisibility(View.GONE);
        }

        goToHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the home activity
                finish();
            }
        });
    }
}
