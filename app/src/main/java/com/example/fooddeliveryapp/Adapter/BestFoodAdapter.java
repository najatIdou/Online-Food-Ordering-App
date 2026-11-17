package com.example.fooddeliveryapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fooddeliveryapp.Activity.DishDetailActivity;
import com.example.fooddeliveryapp.Data.Cart;
import com.example.fooddeliveryapp.Domain.Foods;
import com.example.fooddeliveryapp.R;

import java.util.List;
import java.util.Map;

public class BestFoodAdapter extends RecyclerView.Adapter<BestFoodAdapter.viewholder> {
    private List<Foods> BestFoodList;
    private Context context;
    private Map<Integer, String> durationMap;
    private OnCartUpdatedListener listener;

    // Constructor
    public BestFoodAdapter(Context context, List<Foods> BestFoodList) {
        this.context = context;
        this.BestFoodList = BestFoodList;
    }
    public void setOnCartUpdatedListener(OnCartUpdatedListener listener) {
        this.listener = listener;
    }
    public void setDurationMap(Map<Integer, String> durationMap) {
        this.durationMap = durationMap;
        notifyDataSetChanged();
    }
    public interface OnCartUpdatedListener {
        void onCartUpdated();
    }

    // Create view
    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholde_best_food, parent, false);
        return new viewholder(inflate);
    }

    // Bind data
    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        Foods food = BestFoodList.get(position);
        holder.titleTxt.setText(food.getName());
        holder.priceTxt.setText("$" + food.getPrice());
        holder.starTxt.setText(String.format("%.1f", food.getStar()));

        // Duration text
        String durationText = "";
        if (durationMap != null) {
            durationText = durationMap.get(food.getDuration_id());
            if (durationText == null) durationText = "";
        }
        holder.timeTxt.setText(durationText);

        // Load image
        Glide.with(context)
                .load(food.getImage_path())
                .placeholder(R.drawable.placeholder)
                .into(holder.image);

        // Open food details button
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DishDetailActivity.class);
            intent.putExtra("foodId", food.getId());
            context.startActivity(intent);
        });

        // Add to cart button
        holder.addBtn.setOnClickListener(v -> {
            Cart.getInstance().addItem(food, 1);
            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();

            if (listener != null) {
                listener.onCartUpdated();
            }
        });
    }

    @Override
    public int getItemCount() {
        return BestFoodList.size();
    }

    // Holds UI components
    public static class viewholder extends RecyclerView.ViewHolder {
        TextView titleTxt, priceTxt, starTxt, timeTxt;
        ImageView image;
        TextView addBtn;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            starTxt = itemView.findViewById(R.id.starTxt);
            timeTxt = itemView.findViewById(R.id.timeTxt);
            image = itemView.findViewById(R.id.Image);
            addBtn = itemView.findViewById(R.id.textView12);
        }
    }

}