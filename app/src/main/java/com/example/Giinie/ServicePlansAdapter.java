package com.example.Giinie;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServicePlansAdapter extends RecyclerView.Adapter<ServicePlansAdapter.ViewHolder> {

    private List<ServicePlan> servicePlans;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnPlanSelectedListener listener;

    public ServicePlansAdapter(List<ServicePlan> servicePlans, OnPlanSelectedListener listener) {
        this.servicePlans = servicePlans;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ServicePlan plan = servicePlans.get(position);
        holder.planTextView.setText(plan.getName());

        // Display the service plan name and cost
        double planPrice = plan.getPrice(); // Assuming there is a getPrice() method in ServicePlan class
        String formattedPrice = String.format("$%.2f", planPrice);
        String planDetails = plan.getName() + " " + formattedPrice;
        holder.planTextView.setText(planDetails);

        // Set the background of the selected item
        holder.itemView.setBackgroundResource(selectedPosition == holder.getAdapterPosition() ? R.drawable.selected_plan_background : R.drawable.default_plan_background);

        // Set click listener for the plan item to notify when a plan is selected
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the selected position and notify the listener when a plan is selected
                if (listener != null) {
                    int clickedPosition = holder.getAdapterPosition();
                    if (selectedPosition != clickedPosition) {
                        selectedPosition = clickedPosition;
                        listener.onPlanSelected(plan);
                        notifyDataSetChanged();
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return servicePlans.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView planTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            planTextView = itemView.findViewById(R.id.planTextView);
        }
    }

    // Define the OnPlanSelectedListener interface
    public interface OnPlanSelectedListener {
        void onPlanSelected(ServicePlan plan);
    }

    // Get the selected plan
    public ServicePlan getSelectedPlan() {
        if (selectedPosition != RecyclerView.NO_POSITION) {
            return servicePlans.get(selectedPosition);
        }
        return null;
    }
}
