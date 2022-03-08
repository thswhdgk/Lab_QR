package com.example.lab_qr;

public class User_Info {

    private String name;
    private String id;

    public User_Info() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User_Info(String name, String id) {
        this.name = name;
        this.id = id;
    }
}
