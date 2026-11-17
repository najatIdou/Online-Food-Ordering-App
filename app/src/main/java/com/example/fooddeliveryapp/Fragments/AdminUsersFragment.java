package com.example.fooddeliveryapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddeliveryapp.Adapter.AdminUsersAdapter;
import com.example.fooddeliveryapp.Data.Data;
import com.example.fooddeliveryapp.Domain.User;
import com.example.fooddeliveryapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdminUsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminUsersAdapter adapter;
    private DatabaseReference root;

    public AdminUsersFragment() {}

    // Create view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_users, container, false);
        // Setup Recyclerview
        recyclerView = view.findViewById(R.id.usersRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Data.getInstance().setDataLoadedListener(() -> {
            if (getActivity() == null) return;
            // Get users
            List<User> users = Data.getInstance().getUsers();
            adapter = new AdminUsersAdapter(getContext(), users, user -> {
                int userId = user.getId();
                root = FirebaseDatabase.getInstance().getReference();

                // Delete user
                root.child("users").child(String.valueOf(userId)).removeValue();

                // Delete orders
                root.child("orders").orderByChild("user_id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    Integer orderId = snap.child("id").getValue(Integer.class);

                                    // Delete order itself
                                    snap.getRef().removeValue();

                                    if (orderId != null) {

                                        // Delete order items
                                        root.child("order_items").orderByChild("order_id").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot s : snapshot.getChildren()) {
                                                            s.getRef().removeValue();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {}
                                                });

                                        // Delete payments
                                        root.child("payment").orderByChild("user_id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot s : snapshot.getChildren()) {
                                                            s.getRef().removeValue();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {}
                                                });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });

                // Delete addresses
                root.child("addresses").orderByChild("user_id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    snap.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });

                // Delete restaurant ratings
                root.child("restaurant_ratings").orderByChild("user_id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    snap.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });

                // Delete food ratings
                root.child("food_ratings").orderByChild("user_id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    snap.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });

                // Update UI
                users.remove(user);
                adapter.notifyItemRemoved(users.indexOf(user));
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "User deleted", Toast.LENGTH_SHORT).show();

            });
            recyclerView.setAdapter(adapter);
        });

        return view;
    }
}