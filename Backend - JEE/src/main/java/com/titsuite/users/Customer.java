package com.titsuite.users;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Customer extends User {

    private static final String[] subscriptions = { "BASIC", "PREMIUM" };

    private String subscription;

    public Customer() {}

    public Customer(long id, String email, String hashedPassword, String firstName, String lastName,
        String phoneNumber, Date birthDate, String city, String address, String subscription, String refreshToken,
        String verificationCode, int isActive, Date resendTimeout) {
        super(id, email, hashedPassword, firstName, lastName, phoneNumber, birthDate, city, address, refreshToken,
        verificationCode, isActive, resendTimeout);
        setSubscription(subscription);
    }

    public Customer(User user) {
        super(user.getId(), user.getEmail(), user.getHashedPassword(), user.getFirstName(), user.getLastName(),
            user.getPhoneNumber(), user.getBirthDate(), user.getCity(), user.getAddress(), user.getRefreshToken(),
            user.getVerificationCode(), user.getIsActive(), user.getResendTimeout());
    }

    public Customer(User user, String subscription) {
        this(user.getId(), user.getEmail(), user.getHashedPassword(), user.getFirstName(), user.getLastName(),
            user.getPhoneNumber(), user.getBirthDate(), user.getCity(), user.getAddress(), subscription,
            user.getRefreshToken(), user.getVerificationCode(), user.getIsActive(), user.getResendTimeout());
    }

    public String getSubscription() {
        if (this.subscription == null)
            this.subscription = subscriptions[0];

        return this.subscription;
    }

    public void setSubscription(String subscription) {
        for (String sub : subscriptions) {
            if (sub.equals(subscription)) {
                this.subscription = subscription;
                return;
            }
        }
    }

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
        jsonObject.put("subscription", getSubscription());
        return jsonObject;
    }

}
