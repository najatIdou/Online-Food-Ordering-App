package com.example.fooddeliveryapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fooddeliveryapp.Activity.CategoryFoodsActivity;
import com.example.fooddeliveryapp.Domain.Category;
import com.example.fooddeliveryapp.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.viewholder> {

    private List<Category> categoryList;
    Context context;

    // Constructor
    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    // Create view
    @NonNull
    @Override
    public CategoryAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate= LayoutInflater.from(context).inflate(R.layout.viewholder_category,parent,false);

        return new viewholder(inflate);
    }

    // Bind data
    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.viewholder holder, int position) {
        Category category;
        Log.d("ADAPTER", "Binding position " + position + " of " + getItemCount());

        switch (position){
            case 0:{
                holder.pic.setBackgroundResource(R.drawable.cart0_background);
                break;
            }
            case 1:{
                holder.pic.setBackgroundResource(R.drawable.cart1_background);
                break;
            }
            case 2:{
                holder.pic.setBackgroundResource(R.drawable.cart2_background);
                break;
            }
            case 3:{
                holder.pic.setBackgroundResource(R.drawable.cart3_background);
                break;
            }
            case 4:{
                holder.pic.setBackgroundResource(R.drawable.cart4_background);
                break;
            }
            case 5:{
                holder.pic.setBackgroundResource(R.drawable.cart5_background);
                break;
            }
            case 6:{
                holder.pic.setBackgroundResource(R.drawable.cart6_background);
                break;
            }
            case 7:{
                holder.pic.setBackgroundResource(R.drawable.cart7_background);
                break;
            }
        }

        category=categoryList.get(position);

        // Display name
        holder.titleTxt.setText(category.getName());

        // Load image
        Glide.with(holder.itemView.getContext())
                .load(category.getImage_path())
                .placeholder(R.drawable.placeholder)
                .into(holder.pic);

        // Category button
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CategoryFoodsActivity.class);
            intent.putExtra("categoryId", category.getId());
            intent.putExtra("categoryName", category.getName());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    // Holds UI components
    public class viewholder extends RecyclerView.ViewHolder{
        TextView titleTxt;
        ImageView pic;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.cartNametxt);
            pic = itemView.findViewById(R.id.imgcart);
        }
    }
}
