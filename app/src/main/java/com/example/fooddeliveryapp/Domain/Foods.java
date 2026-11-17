package com.example.fooddeliveryapp.Domain;

import java.io.Serializable;

public class Foods implements Serializable {

    private int id;
    private String name;
    private String description;
    private Double price;
    private String image_path;
    private int duration_id;
    private int best_food;
    private int category_id;
    private Double star;
    private int rating_count;
    private int restaurant_id;

    public Foods() {}

    public Foods(int id, String name, String description, Double price, String image_path, int duration_id, int best_food, int category_id, Double star, int restaurant_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image_path = image_path;
        this.duration_id = duration_id;
        this.best_food = best_food;
        this.category_id = category_id;
        this.star = star;
        this.restaurant_id = restaurant_id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getImage_path() { return image_path; }
    public void setImage_path(String image_path) { this.image_path = image_path; }

    public int getDuration_id() { return duration_id; }
    public void setDuration_id(int duration_id) { this.duration_id = duration_id; }

    public int getBest_food() { return best_food; }
    public void setBest_food(int best_food) { this.best_food = best_food; }

    public int getCategory_id() { return category_id; }
    public void setCategory_id(int category_id) { this.category_id = category_id; }

    public Double getStar() { return star; }
    public void setStar(Double star) { this.star = star; }
    public int getRating_count() { return rating_count; }
    public void setRating_count(int rating_count) { this.rating_count = rating_count; }
    public int getRestaurant_id() { return restaurant_id; }
    public void setRestaurant_id(int restaurant_id) { this.restaurant_id = restaurant_id; }
}