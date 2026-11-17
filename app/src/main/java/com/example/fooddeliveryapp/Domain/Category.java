package com.example.fooddeliveryapp.Domain;

public class Category {
    private int id;
    private String name;
    private String image_path;

    public Category() {
    }
    @Override
    public String toString() {
        return name;
    }

    public Category(int id, String name, String image_path) {
        this.id = id;
        this.name = name;
        this.image_path = image_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String imagePath) {
        this.image_path = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String Name) {
        this.name = Name;
    }
}
