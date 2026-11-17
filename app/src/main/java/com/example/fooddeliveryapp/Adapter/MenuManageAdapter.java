package com.example.fooddeliveryapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fooddeliveryapp.Domain.Foods;
import com.example.fooddeliveryapp.R;

import java.util.List;

public class MenuManageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CATEGORY = 0;
    private static final int TYPE_FOOD = 1;

    private Context context;
    private List<Object> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Foods food);
        void onDeleteClick(Foods food);
    }

    // Constructor
    public MenuManageAdapter(Context context, List<Object> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_CATEGORY : TYPE_FOOD;
    }

    // Create view
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_CATEGORY) {
            View view = inflater.inflate(R.layout.item_category_header, parent, false);
            return new CategoryViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_food_manage, parent, false);
            return new FoodViewHolder(view);
        }
    }

    // Bind data
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CategoryViewHolder) {
            CategoryViewHolder catHolder = (CategoryViewHolder) holder;
            String categoryName = (String) items.get(position);
            catHolder.categoryName.setText(categoryName);
        } else if (holder instanceof FoodViewHolder) {
            FoodViewHolder foodHolder = (FoodViewHolder) holder;

            // Display data
            Foods food = (Foods) items.get(position);
            foodHolder.name.setText(food.getName());
            foodHolder.description.setText(food.getDescription());
            foodHolder.price.setText("$" + food.getPrice());

            // Load image
            Glide.with(context)
                    .load(food.getImage_path())
                    .placeholder(R.drawable.placeholder)
                    .into(foodHolder.image);

            // Delete, edit buttons
            foodHolder.editIcon.setOnClickListener(v -> listener.onEditClick(food));
            foodHolder.deleteIcon.setOnClickListener(v -> listener.onDeleteClick(food));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
        }
    }

    // Holds UI components
    static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView image, editIcon, deleteIcon;
        TextView name, description, price;
        FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.foodImage);
            name = itemView.findViewById(R.id.foodName);
            description = itemView.findViewById(R.id.foodDescription);
            price = itemView.findViewById(R.id.foodPrice);
            editIcon = itemView.findViewById(R.id.editBtn);
            deleteIcon = itemView.findViewById(R.id.deleteBtn);
        }
    }
}