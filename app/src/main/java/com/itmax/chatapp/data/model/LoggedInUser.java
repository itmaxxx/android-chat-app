package com.itmax.chatapp.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String fullname;
    private String token;

    public LoggedInUser(String userId, String fullname, String token) {
        this.userId = userId;
        this.fullname = fullname;
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public String getFullname() {
        return fullname;
    }

    public String getToken() {
        return token;
    }
}