package com.example.fooddeliveryapp.Data;
import com.example.fooddeliveryapp.Domain.Foods;

import java.util.HashMap;
import java.util.Map;
public class Cart {
    // One cart in app
    private static Cart instance;
    private Map<Integer, CartItem> items = new HashMap<>();

    private Cart() {}

    // Access cart anywhere
    public static synchronized Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }
    // Sum all qtys
    public int getTotalItems() {
        int total = 0;
        for (CartItem item : items.values()) {
            total += item.getQuantity();
        }
        System.out.println("TOTAL ITEMS: " + total);

        return total;
    }
    public void addItem(Foods food, int quantity) {
        if (items.containsKey(food.getId())) {
            // If exists increase qty
            CartItem existing = items.get(food.getId());
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            // Create new item
            items.put(food.getId(), new CartItem(food, quantity));
        }
    }
    // Replace quantity
    public void setItemQuantity(Foods food, int quantity) {
        if (quantity <= 0) {
            items.remove(food.getId());
        } else {
            items.put(food.getId(), new CartItem(food, quantity));
        }
    }

    // Remove from cart
    public void removeItem(int foodId) {
        items.remove(foodId);
    }

    // Modify existing item
    public void updateQuantity(int foodId, int quantity) {
        if (items.containsKey(foodId)) {
            if (quantity <= 0) {
                items.remove(foodId);
            } else {
                items.get(foodId).setQuantity(quantity);
            }
        }
    }

    public Map<Integer, CartItem> getItems() {
        return items;
    }

    // Calc pric x qty
    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : items.values()) {
            total += item.getFood().getPrice() * item.getQuantity();
        }
        return total;
    }

    // Empty cart
    public void clear() {
        items.clear();
    }

    // Inner class
    public static class CartItem {
        private Foods food;
        private int quantity;

        public CartItem(Foods food, int quantity) {
            this.food = food;
            this.quantity = quantity;
        }

        // Access data
        public Foods getFood() { return food; }
        public int getQuantity() { return quantity; }

        // Modify quantity
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}