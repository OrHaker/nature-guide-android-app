package com.example.natureguide;

import java.util.ArrayList;

public class User {

    //fields
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private ArrayList<NatureLocation> favLocation;

    //constructor
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.favLocation = new ArrayList<NatureLocation>();
    }

    public User() {
    }

    //getters and setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<NatureLocation> getFavLocation() {
        return favLocation;
    }

    public void setFavLocation(ArrayList<NatureLocation> favLocation) {
        this.favLocation = favLocation;
    }

    //methods
    @Override
    public String toString() {
        return "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'';
    }

}
