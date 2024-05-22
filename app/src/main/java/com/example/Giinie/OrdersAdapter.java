package com.example.Giinie;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private List<Order> ordersList;

    public OrdersAdapter(List<Order> ordersList) { // Remove the DatabaseHelper parameter
        this.ordersList = ordersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = ordersList.get(position);
        holder.bind(order);

        holder.reviewButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ReviewActivity.class);
            intent.putExtra("order_id", order.getId());
            intent.putExtra("service_plan_name", order.getPlanName());
            view.getContext().startActivity(intent);
        });

        // Retrieve user details directly from the order
        holder.userNameTextView.setText(order.getUserName());
        holder.userPhoneTextView.setText(order.getUserPhone());
        holder.userAddressTextView.setText(order.getUserAddress());
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceNameTextView;
        TextView planNameTextView;
        TextView dateTextView;
        TextView userNameTextView;
        TextView userPhoneTextView;
        TextView userAddressTextView;
        Button reviewButton;

        public ViewHolder(View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.serviceNameTextView);
            planNameTextView = itemView.findViewById(R.id.servicePlanTextView);
            dateTextView = itemView.findViewById(R.id.dateTimeTextView);
            reviewButton = itemView.findViewById(R.id.reviewButton);
            userNameTextView = itemView.findViewById(R.id.userNameTextView); // Change to userNameTextView
            userPhoneTextView = itemView.findViewById(R.id.userPhoneTextView); // Change to userPhoneTextView
            userAddressTextView = itemView.findViewById(R.id.userAddressTextView); // Change to userAddressTextView
        }

        public void bind(Order order) {
            if (order != null) {
                serviceNameTextView.setText(order.getServiceName());
                planNameTextView.setText(order.getPlanName());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String formattedDate = dateFormat.format(order.getOrderDate());
                dateTextView.setText(formattedDate);
            }
        }
    }
}
