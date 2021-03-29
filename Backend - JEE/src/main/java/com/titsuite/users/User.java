package com.titsuite.users;

import com.titsuite.utils.JsonSerializable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class User implements JsonSerializable {

    protected long id;
    protected String email;
    protected String hashedPassword;
    protected String firstName;
    protected String lastName;
    protected String phoneNumber;
    protected Date birthDate;
    protected String city;
    protected String address;
    protected String refreshToken;
    protected String verificationCode;
    protected int isActive;
    protected Date resendTimeout;

    public User() {}

    public User(long id, String email, String hashedPassword, String firstName, String lastName,
        String phoneNumber, Date birthDate, String city, String address, String refreshToken,
        String verificationCode, int isActive, Date resendTimeout) {
        this.setId(id);
        this.setEmail(email);
        this.setHashedPassword(hashedPassword);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setPhoneNumber(phoneNumber);
        this.setBirthDate(birthDate);
        this.setCity(city);
        this.setAddress(address);
        this.setRefreshToken(refreshToken);
        this.setVerificationCode(verificationCode);
        this.setIsActive(isActive);
        this.setResendTimeout(resendTimeout);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) { this.id = id; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null)
            email = email.toLowerCase();
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getFirstName() { return this.firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return this.lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getCity() { return this.city; }

    public void setCity(String city) { this.city = city; }

    public String getAddress() { return this.address; }

    public void setAddress(String address) { this.address = address; }

    public String getRefreshToken() { return this.refreshToken; }

    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getVerificationCode() { return this.verificationCode; }

    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }

    public int getIsActive() { return this.isActive; }

    public void setIsActive(int isActive) {
        if (isActive == 0 || isActive == 1)
            this.isActive = isActive;
    }

    public Date getResendTimeout() { return this.resendTimeout; }

    public void setResendTimeout(Date resendTimeout) { this.resendTimeout = resendTimeout; }

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
        return jsonObject;
    }

}
