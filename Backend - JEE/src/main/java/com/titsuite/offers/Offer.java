package com.titsuite.offers;

import java.util.Date;

public class Offer{

    private int ID;
    private String description;
    private String city;
    private int minimumWage;

    private static String status="en attente";
    private String refCustomer;
    private  Date  startDay;
    private String activity;


    public Offer(){};
    public Offer( String description, String city, int minimumWage, String status, String refCustomer, Date startDay, String activity) {

        this.description = description;
        this.city = city;
        this.minimumWage = minimumWage;
        this.status = status;
        this.refCustomer = refCustomer;
        this.startDay = startDay;
        this.activity=activity;
    }

    public Offer(int ID, String description, String city, int minimumWage, String status, String refCustomer, Date startDay, String activity) {
        this.ID = ID;
        this.description = description;
        this.city = city;
        this.minimumWage = minimumWage;
        this.status = status;
        this.refCustomer = refCustomer;
        this.startDay = startDay;
        this.activity=activity;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getMinimumWage() {
        return minimumWage;
    }

    public void setMinimumWage(int minimumWage) {
        this.minimumWage = minimumWage;
    }

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        Offer.status = status;
    }

    public String getRefCustomer() {
        return refCustomer;
    }

    public void setRefCustomer(String refCustomer) {
        this.refCustomer = refCustomer;
    }

    public Date getStartDay() {
        return startDay;
    }

    public void setStartDay(Date startDay) {
        this.startDay = startDay;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "ID=" + ID +
                ", description='" + description + '\'' +
                ", city='" + city + '\'' +
                ", minimumWage=" + minimumWage +
                ", refCustomer=" + refCustomer +
                ", startDay='" + startDay + '\'' +
                '}';
    }
}
