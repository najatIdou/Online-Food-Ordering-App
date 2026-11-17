package com.example.fooddeliveryapp.Domain;

import java.util.List;

public class Order {
    private int id;
    private int user_id;
    private int restaurant_id;
    private String restaurant_name;
    private String restaurant_image;
    private String status;
    private String created_at;
    private double total;
    private String items_summary;
    private String order_number;
    private Integer delivery_address_id;

    // Customer details
    private String customer_name;
    private String customer_phone;
    private String delivery_address;

    private List<Foods> foods;

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_CANCELED = "cancelled";
    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_REJECTED = "rejected";
    public static final String STATUS_COMPLETED = "completed";

    public Order() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUser_id() { return user_id; }
    public void setUser_id(int user_id) { this.user_id = user_id; }

    public int getRestaurant_id() { return restaurant_id; }
    public void setRestaurant_id(int restaurant_id) { this.restaurant_id = restaurant_id; }

    public String getRestaurant_name() { return restaurant_name; }
    public void setRestaurant_name(String restaurant_name) { this.restaurant_name = restaurant_name; }

    public String getRestaurant_image() { return restaurant_image; }
    public void setRestaurant_image(String restaurant_image) { this.restaurant_image = restaurant_image; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getItems_summary() { return items_summary; }
    public void setItems_summary(String items_summary) { this.items_summary = items_summary; }

    public String getOrder_number() { return order_number; }
    public void setOrder_number(String order_number) { this.order_number = order_number; }

    public Integer getDelivery_address_id() { return delivery_address_id; }
    public void setDelivery_address_id(Integer delivery_address_id) { this.delivery_address_id = delivery_address_id; }

    public String getCustomer_name() { return customer_name; }
    public void setCustomer_name(String customer_name) { this.customer_name = customer_name; }

    public String getCustomer_phone() { return customer_phone; }
    public void setCustomer_phone(String customer_phone) { this.customer_phone = customer_phone; }

    public String getDelivery_address() { return delivery_address; }
    public void setDelivery_address(String delivery_address) { this.delivery_address = delivery_address; }

    public List<Foods> getFoods() { return foods; }
    public void setFoods(List<Foods> foods) { this.foods = foods; }
}