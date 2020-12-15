package com.example.fevertracker.Classes;

public class Member {
    private String name, email, phone, passport, address,state;
//    List<String> tracker = new ArrayList<>();
//    public void setTracker(ArrayList<String> tracker) {
//        this.tracker = tracker;
//    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Member() {
    }
}
