package com.titsuite.jobs;

import java.util.Date;

public class Job {

   private String city;
   private int minimumWage;
   private String period;
   private String first_name;
   private String last_name;
   private int rate;
   private String review;
   private String description;

   public Job(String city, int minimumWage, String period, String first_name, String last_name, int rate, String review, String description) {
      this.city = city;
      this.minimumWage = minimumWage;
      this.period = period;
      this.first_name = first_name;
      this.last_name = last_name;
      this.rate = rate;
      this.review = review;
      this.description = description;
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

   public String getPeriod() {
      return period;
   }

   public void setPeriod(String period) {
      this.period = period;
   }

   public String getFirst_name() {
      return first_name;
   }

   public void setFirst_name(String first_name) {
      this.first_name = first_name;
   }

   public String getLast_name() {
      return last_name;
   }

   public void setLast_name(String last_name) {
      this.last_name = last_name;
   }

   public int getRate() {
      return rate;
   }

   public void setRate(int rate) {
      this.rate = rate;
   }

   public String getReview() {
      return review;
   }

   public void setReview(String review) {
      this.review = review;
   }
}

