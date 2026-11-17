package com.example.fooddeliveryapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddeliveryapp.Data.Cart;
import com.example.fooddeliveryapp.Data.Data;
import com.example.fooddeliveryapp.Domain.Address;
import com.example.fooddeliveryapp.Domain.Restaurant;
import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.databinding.ActivityPaymentBinding;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class PaymentActivity extends AppCompatActivity {

    // Connect XML UI
    private ActivityPaymentBinding binding;

    // Firebase reference
    private DatabaseReference userRef;
    private DatabaseReference addressesRef;
    private  DatabaseReference ordersRef;
    private DatabaseReference paymentRef;
    private DatabaseReference itemsRef;
    private String selectedMethod = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.brown_light));
        // Load data and setup UI
        loadCheckoutItems();
        setupTotals();
        setupPaymentSelection();
        setupButtons();

        // Disable back button
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing
            }
        });
    }

    // Validation
    private void validateUserBeforePayment(int userId) {
        userRef = FirebaseDatabase.getInstance().getReference("users").child(String.valueOf(userId));
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                if (!userSnapshot.exists()) {
                    toast("User not found!");
                    return;
                }

                // Check user phone number
                String phone = userSnapshot.child("phone").getValue(String.class);
                if (phone == null || phone.trim().isEmpty()) {
                    toast("Please add your phone number before paying!");
                    return;
                }

                // Check user's default address
                addressesRef = FirebaseDatabase.getInstance().getReference("addresses");
                addressesRef.orderByChild("user_id").equalTo(userId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot addressSnapshot) {
                                boolean hasDefault = false;
                                for (DataSnapshot addrSnap : addressSnapshot.getChildren()) {
                                    Boolean isDefault = addrSnap.child("is_default").getValue(Boolean.class);
                                    if (isDefault != null && isDefault) {
                                        hasDefault = true;
                                        break;
                                    }
                                }

                                if (!hasDefault) {
                                    // Show message and open AddAddressActivity
                                    Toast.makeText(PaymentActivity.this,
                                            "You must add a default delivery address before paying!", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(PaymentActivity.this, AddAddressActivity.class);
                                    intent.putExtra("TOOLBAR_TITLE", "Add Delivery Address");
                                    startActivity(intent);
                                    return;
                                }

                                // Proceed with payment
                                proceedWithPayment(userId);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                toast("Error checking addresses!");
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toast("Error checking user data!");
            }
        });
    }

    private void loadCheckoutItems() {
        binding.orderContainer.removeAllViews();
        // Group food items by restau
        Map<Integer, List<Cart.CartItem>> grouped = groupItemsByRestaurant();

        for (Map.Entry<Integer, List<Cart.CartItem>> entry : grouped.entrySet()) {
            int restaurantId = entry.getKey();
            List<Cart.CartItem> items = entry.getValue();

            View cardView = getLayoutInflater().inflate(R.layout.item_checkout_restaurant, binding.orderContainer, false);
            TextView restaurantName = cardView.findViewById(R.id.restaurantName);
            LinearLayout itemsContainer = cardView.findViewById(R.id.itemsContainer);

            restaurantName.setText(getRestaurantName(restaurantId));

            for (Cart.CartItem item : items) {
                View foodView = getLayoutInflater().inflate(R.layout.item_checkout_food, itemsContainer, false);
                TextView foodName = foodView.findViewById(R.id.foodName);
                TextView foodQty = foodView.findViewById(R.id.foodQty);
                TextView foodPrice = foodView.findViewById(R.id.foodPrice);

                int qty = item.getQuantity();
                double price = item.getFood().getPrice() * qty;

                foodName.setText(item.getFood().getName());
                foodQty.setText("Quantity: " + qty);
                foodPrice.setText("$" + String.format("%.2f", price));

                itemsContainer.addView(foodView);
            }
            // Add food items inside cart
            binding.orderContainer.addView(cardView);
        }
    }

    private void setupTotals() {
        // Calculate the prices
        double subtotal = Cart.getInstance().getTotalPrice();
        double delivery = 2.99;
        double total = subtotal + delivery;

        binding.subtotal.setText("Subtotal: $" + String.format("%.2f", subtotal));
        binding.delivery.setText("Delivery: $" + String.format("%.2f", delivery));
        binding.total.setText("Total: $" + String.format("%.2f", total));
    }

    // Check payment method
    private void setupPaymentSelection() {
        binding.paymentGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.cashOption) {
                selectedMethod = "cash";
                binding.cardLayout.setVisibility(View.GONE);
            } else {
                selectedMethod = "card";
                binding.cardLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private int getLoggedUserId() {
        return getSharedPreferences("UserData", MODE_PRIVATE).getInt("userId", -1);
    }

    // Buttons
    private void setupButtons() {
        // Pay button
        binding.btnPay.setOnClickListener(v -> {
            if (Cart.getInstance().getItems().isEmpty()) {
                toast("Cart is empty!");
                return;
            }
            if (selectedMethod.isEmpty()) {
                toast("Select payment method");
                return;
            }
            if (selectedMethod.equals("card") && !validateCard()){
                return;
            }

            int userId = getLoggedUserId();
            if (userId == -1) {
                toast("User not logged in");
                return;
            }

            // Validates
            validateUserBeforePayment(userId);
        });

        // Cancel button
        binding.btnCancel.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Cancel order?")
                    .setPositiveButton("YES", (d, w) -> {
                        Cart.getInstance().clear();
                        finish();
                    })
                    .setNegativeButton("NO", (d, w) -> d.dismiss())
                    .show();
        });
    }

    // Payment process
    private void proceedWithPayment(int userId) {
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Read all orders to generate new Id
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int startOrderId = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Integer id = child.child("id").getValue(Integer.class);
                        if (id != null && id >= startOrderId) {
                            startOrderId = id + 1;
                        }
                    }
                }
                // Save new order
                saveOrder(userId, "pending", startOrderId);

                toast("Order placed successfully!");
                // Redirect to main activity
                Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    // Save order
    private void saveOrder(int userId, String status, int startOrderId) {
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        Map<Integer, List<Cart.CartItem>> grouped = groupItemsByRestaurant();

        // Create a list of entries to process sequentially
        List<Map.Entry<Integer, List<Cart.CartItem>>> entries = new ArrayList<>(grouped.entrySet());
        processNextOrder(entries, 0, userId, status, startOrderId);
    }

    private void processNextOrder(List<Map.Entry<Integer, List<Cart.CartItem>>> entries,
                                  int index, int userId, String status, int currentOrderId) {
        if (index >= entries.size()) {
            // All orders processed – clear cart
            Cart.getInstance().clear();
            return;
        }

        // Group food items by restaurant
        Map.Entry<Integer, List<Cart.CartItem>> entry = entries.get(index);
        int restaurantId = entry.getKey();
        List<Cart.CartItem> items = entry.getValue();

        // Create order object
        Map<String, Object> order = new HashMap<>();
        order.put("id", currentOrderId);
        order.put("order_number", String.format("ORD-%03d", currentOrderId));
        order.put("user_id", userId);
        order.put("restaurant_id", restaurantId);
        order.put("restaurant_name", getRestaurantName(restaurantId));
        order.put("delivery_address_id", getDefaultAddressId(userId));
        order.put("items_summary", buildItemsSummary(items));
        order.put("total", calculateTotal(items) + 2.99);
        order.put("delivery_fee", 2.99);
        order.put("status", status);
        order.put("created_at", getCurrentDateTime());

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Save order
        ordersRef.child(String.valueOf(currentOrderId)).setValue(order)
                .addOnSuccessListener(aVoid -> {
                    // Save order items and payment method
                    saveOrderItems(currentOrderId, items, new SaveCallback() {
                        @Override
                        public void onComplete() {
                            savePayment(userId, currentOrderId, new SaveCallback() {
                                @Override
                                public void onComplete() {
                                    // Move to next order
                                    processNextOrder(entries, index + 1, userId, status, currentOrderId + 1);
                                }

                                @Override
                                public void onError(Exception e) {
                                    toast("Error saving payment: " + e.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            toast("Error saving items: " + e.getMessage());
                        }
                    });
                })
                .addOnFailureListener(e -> toast("Error saving order: " + e.getMessage()));
    }

    // Callback interface
    interface SaveCallback {
        void onComplete();
        void onError(Exception e);
    }

    // Save payment
    private void savePayment(int userId, int orderId, SaveCallback callback) {
        paymentRef = FirebaseDatabase.getInstance().getReference("payment");

        // Read payments to generate new Id
        paymentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int maxId = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Integer id = child.child("id").getValue(Integer.class);
                    if (id != null && id > maxId){
                        maxId = id;
                    }
                }

                int nextId = maxId + 1;

                // Create order object
                Map<String, Object> payment = new HashMap<>();
                payment.put("id", nextId);
                payment.put("user_id", userId);
                payment.put("order_id", orderId);
                payment.put("type", selectedMethod);

                // Payment method
                if (selectedMethod.equals("card")) {
                    String cardNum = binding.cardNumber.getText().toString().replaceAll("\\s+", "");
                    payment.put("card_last4", cardNum.length() >= 4 ? cardNum.substring(cardNum.length() - 4) : cardNum);
                    payment.put("expiry", binding.expiry.getText().toString());
                }

                // Save to firebase
                paymentRef.child(String.valueOf(nextId)).setValue(payment)
                        .addOnSuccessListener(aVoid -> callback.onComplete())
                        .addOnFailureListener(callback::onError);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    // Save order items
    private void saveOrderItems(int orderId, List<Cart.CartItem> items, SaveCallback callback) {
        itemsRef = FirebaseDatabase.getInstance().getReference("order_items");

        // Read payments to generate new Id
        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int maxId = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Integer id = child.child("id").getValue(Integer.class);
                    if (id != null && id > maxId){
                        maxId = id;
                    }
                }

                int nextId = maxId + 1;
                // Save items
                saveNextItem(itemsRef, items, 0, nextId, orderId, callback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    private void saveNextItem(DatabaseReference itemsRef, List<Cart.CartItem> items, int itemIndex, int currentId, int orderId, SaveCallback callback) {
        // Finished all items
        if (itemIndex >= items.size()) {
            callback.onComplete();
            return;
        }

        // Create items object
        Cart.CartItem item = items.get(itemIndex);
        Map<String, Object> data = new HashMap<>();
        data.put("id", currentId);
        data.put("order_id", orderId);
        data.put("food_id", item.getFood().getId());
        data.put("quantity", item.getQuantity());

        // Save to firebase
        itemsRef.child(String.valueOf(currentId)).setValue(data)
                .addOnSuccessListener(aVoid -> {
                    saveNextItem(itemsRef, items, itemIndex + 1, currentId + 1, orderId, callback);
                })
                .addOnFailureListener(callback::onError);
    }

    // Get the restau name from id
    private String getRestaurantName(int id) {
        Restaurant r = Data.getInstance().getRestaurantById(id);
        return r != null ? r.getName() : "";
    }

    // Sum of items
    private double calculateTotal(List<Cart.CartItem> items) {
        double total = 0;
        for (Cart.CartItem item : items) {
            total += item.getFood().getPrice() * item.getQuantity();
        }
        return total;
    }

    // Splits cart into multiple orders
    private Map<Integer, List<Cart.CartItem>> groupItemsByRestaurant() {
        Map<Integer, List<Cart.CartItem>> map = new HashMap<>();
        for (Cart.CartItem item : Cart.getInstance().getItems().values()) {
            int restId = item.getFood().getRestaurant_id();
            map.computeIfAbsent(restId, k -> new ArrayList<>()).add(item);
        }
        return map;
    }

    // Build items summary
    private String buildItemsSummary(List<Cart.CartItem> items) {
        StringBuilder sb = new StringBuilder();
        for (Cart.CartItem item : items) {
            sb.append(item.getFood().getName()).append(", ");
        }
        if (sb.length() > 0) sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    // Check default address
    private int getDefaultAddressId(int userId) {
        for (Address a : Data.getInstance().getAddresses()) {
            if (a.getUser_id() == userId && a.is_default()) {
                return a.getId();
            }
        }
        return 0;
    }

    // Validating of the card
    private boolean validateCard() {
        String number = binding.cardNumber.getText().toString().replaceAll("\\s+", "");
        String name = binding.cardName.getText().toString().trim();
        String exp = binding.expiry.getText().toString().trim();
        String cvvVal = binding.cvv.getText().toString().trim();

        if (!number.matches("\\d{16}")) {
            toast("Invalid card");
            return false;
        }
        if (!name.matches("[a-zA-Z ]{3,}")) {
            toast("Invalid name");
            return false;
        }
        if (!exp.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            toast("Invalid expiry");
            return false;
        }
        if (!cvvVal.matches("\\d{3}")) {
            toast("Invalid CVV");
            return false;
        }
        return true;
    }

    private String getCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        // Disable back press
    }
}