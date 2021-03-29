package com.titsuite.models;

import java.util.Date;

public class AuthCredentials {

    private String email;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Date birthDate;
    private String address;
    private String city;
    private String subscription;
    private String activity;
    private Float minimumWage;

    public AuthCredentials() {}

    public AuthCredentials(String email, String password, String role, String firstName, String lastName,
        String phoneNumber, Date birthDate, String address, String city, String subscription, String activity,
        Float minimumWage) {
        setEmail(email);
        setPassword(password);
        setRole(role);
        setFirstName(firstName);
        setLastName(lastName);
        setPhoneNumber(phoneNumber);
        setBirthDate(birthDate);
        setAddress(address);
        setCity(city);
        setSubscription(subscription);
        setActivity(activity);
        setMinimumWage(minimumWage);
    }

    public String getEmail() { return this.email; }

    public void setEmail(String email) {
        if (email != null)
            email = email.toLowerCase();
        this.email = email;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public String getFirstName() { return this.firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return this.lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNumber() { return this.phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Date getBirthDate() { return this.birthDate; }

    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

    public String getAddress() { return this.address; }

    public void setAddress(String address) { this.address = address; }

    public String getCity() { return this.city; }

    public void setCity(String city) { this.city = city; }

    public String getSubscription() { return this.subscription; }

    public void setSubscription(String subscription) { this.subscription = subscription; }

    public String getActivity() { return this.activity; }

    public void setActivity(String activity) { this.activity = activity; }

    public Float getMinimumWage() { return this.minimumWage; }

    public void setMinimumWage(Float minimumWage) { this.minimumWage = minimumWage; }

}
