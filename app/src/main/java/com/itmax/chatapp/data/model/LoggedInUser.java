package com.itmax.chatapp.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String fullname;

    public LoggedInUser(String userId, String fullname) {
        this.userId = userId;
        this.fullname = fullname;
    }

    public String getUserId() {
        return userId;
    }

    public String getFullname() {
        return fullname;
    }
}