package com.example.Giinie;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeServiceAdapter extends RecyclerView.Adapter<HomeServiceAdapter.ViewHolder> {

    private List<Service> services;
    private OnItemClickListener listener;
    private List<Service> allServices;
    private List<Service> filteredServices;


    public HomeServiceAdapter(List<Service> services, OnItemClickListener listener) {
        this.services = services;
        this.listener = listener;
        this.allServices = services;
        this.filteredServices = new ArrayList<>(services);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Service service = services.get(position);
        holder.serviceNameTextView.setText(service.getName());

        // Set the image resource for the service icon
        int imageResource = getImageResourceForService(service.getName());
        holder.serviceIconImageView.setImageResource(imageResource);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(service);
            }
        });
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceNameTextView;
        ImageView serviceIconImageView; // Add this line

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.serviceNameTextView);
            serviceIconImageView = itemView.findViewById(R.id.serviceIconImageView); // Add this line
        }
    }


    public interface OnItemClickListener {
        void onItemClick(Service service);
    }

    public void updateServices(List<Service> services) {
        this.services = services;
        notifyDataSetChanged();
    }

    private int getImageResourceForService(String serviceName) {
        switch (serviceName.toLowerCase()) {
            case "plumbing":
                return R.drawable.plumbing;
            case "cleaning":
                return R.drawable.cleaning;
            case "repairs":
                return R.drawable.repairs;
            case "gardening":
                return R.drawable.gardening;
            case "home spa":
                return R.drawable.homespa;
            case "electrical":
                return R.drawable.electrical;
            default:
                return R.drawable.ic_service_placeholder; // Default placeholder image
        }
    }




}
