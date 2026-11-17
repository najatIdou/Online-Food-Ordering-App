package com.example.fooddeliveryapp.Domain;

public class User {
    private int id;
    private String name;
    private String email;
    private Object phone;
    private String password;
    private String role;
    private String created_at;

    public User() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone== null ? "" : String.valueOf(phone); }
    public void setPhone(Object phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}