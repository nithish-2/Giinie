package com.example.Giinie;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> cartItems;

    public CartAdapter() {
        cartItems = new ArrayList<>();
    }

    public void updateCartItems(List<CartItem> cartItems) {
        this.cartItems.clear();
        this.cartItems.addAll(cartItems);
        notifyDataSetChanged();
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.servicePlanTextView.setText(cartItem.getServicePlan());
        holder.dateTimeTextView.setText(formatDateTime(cartItem.getDate()));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    private String formatDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy - HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView servicePlanTextView;
        TextView dateTimeTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            servicePlanTextView = itemView.findViewById(R.id.servicePlanTextView);
            dateTimeTextView = itemView.findViewById(R.id.dateTimeTextView);
        }
    }
}
