package com.example.fooddeliveryapp.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddeliveryapp.Data.Data;
import com.example.fooddeliveryapp.Domain.Order;
import com.example.fooddeliveryapp.Domain.User;
import com.example.fooddeliveryapp.R;

import java.util.List;

public class AdminUsersAdapter extends RecyclerView.Adapter<AdminUsersAdapter.ViewHolder> {

    private Context context;
    private List<User> users;
    private List<Order> orders;
    private OnUserDeleteListener deleteListener;

    // Delete the user
    public interface OnUserDeleteListener {
        void onDeleteUser(User user);
    }

    // Constructor
    public AdminUsersAdapter(Context context, List<User> users, OnUserDeleteListener deleteListener) {
        this.context = context;
        this.users = users;
        this.deleteListener = deleteListener;
    }

    // Create view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       // Loads XML layout for rows
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_user, parent, false);
        return new ViewHolder(view);
    }

    // Bind data
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get current user
        User user = users.get(position);
        orders = Data.getInstance().getOrders();

        // Display users
        if(!user.getRole().equals("admin")){
            holder.tvUserName.setText(user.getName());
            holder.tvUserEmail.setText(user.getEmail());
            holder.tvUserRole.setText("Role: " + user.getRole());
            if(user.getRole().equals("customer")){
                holder.tvUserRole.setText("Role: Customer" );
            }else{
                holder.tvUserRole.setText("Role: Restaurent Owner " );
            }
        }

        // Delete user button
        holder.btnDeleteUser.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("Confirm");
            builder.setMessage("Are you sure to delete the user?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (deleteListener != null) {
                        deleteListener.onDeleteUser(user);
                    }
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    // Holds UI components
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserEmail, tvUserRole;
        ImageButton btnDeleteUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            btnDeleteUser = itemView.findViewById(R.id.btnDeleteUser);
        }
    }
}