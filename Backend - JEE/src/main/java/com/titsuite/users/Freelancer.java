package com.titsuite.users;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Freelancer extends User {

    private String activity;
    private Float minimumWage;

    public Freelancer() {}

    public Freelancer(long id, String email, String hashedPassword, String firstName, String lastName,
        String phoneNumber, Date birthDate, String city, String address, String activity, float minimumWage,
        String refreshToken, String verificationCode, int isActive, Date resendTimeout) {
        super(id, email, hashedPassword, firstName, lastName, phoneNumber, birthDate, city, address, refreshToken,
            verificationCode, isActive, resendTimeout);
        setActivity(activity);
        setMinimumWage(minimumWage);
    }

    public Freelancer(User user) {
        super(user.getId(), user.getEmail(), user.getHashedPassword(), user.getFirstName(), user.getLastName(),
            user.getPhoneNumber(), user.getBirthDate(), user.getCity(), user.getAddress(), user.getRefreshToken(),
            user.getVerificationCode(), user.getIsActive(), user.getResendTimeout());
    }

    public Freelancer(User user, String activity, float minimumWage) {
        this(user.getId(), user.getEmail(), user.getHashedPassword(), user.getFirstName(), user.getLastName(),
            user.getPhoneNumber(), user.getBirthDate(), user.getCity(), user.getAddress(), activity, minimumWage,
            user.getRefreshToken(), user.getVerificationCode(), user.getIsActive(), user.getResendTimeout());
    }

    public Float getMinimumWage() { return this.minimumWage; }

    public void setMinimumWage(Float minimumWage) { this.minimumWage = minimumWage; }

    public String getActivity() { return this.activity; }

    public void setActivity(String activity) { this.activity = activity; }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", getId());
        jsonObject.put("email", getEmail());
        jsonObject.put("firstName", getFirstName());
        jsonObject.put("lastName", getLastName());
        jsonObject.put("phoneNumber", getPhoneNumber());
        jsonObject.put("birthDate", getBirthDate().getTime());
        jsonObject.put("city", getCity());
        jsonObject.put("address", getAddress());
        jsonObject.put("activity", getActivity());
        jsonObject.put("minimumWage", getMinimumWage());
        return jsonObject;
    }

}
