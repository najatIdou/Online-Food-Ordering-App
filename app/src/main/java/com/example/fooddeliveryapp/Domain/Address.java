package com.example.fooddeliveryapp.Domain;

public class Address {

    private int id;
    private int user_id;
    private String address_text;
    private String label;
    public boolean is_default;

    public Address() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getAddress_text() {
        return address_text;
    }

    public void setAddress_text(String address_text) {
        this.address_text = address_text;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean is_default() {
        return is_default;
    }

    public void setis_default(boolean is_default) {
        this.is_default = is_default;
    }
}