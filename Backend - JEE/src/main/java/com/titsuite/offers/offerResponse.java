package com.titsuite.offers;

public class offerResponse {
    private int ID;
    private String response;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "offerResponse{" +
                "ID=" + ID +
                ", response='" + response + '\'' +
                '}';
    }
}
