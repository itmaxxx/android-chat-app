package com.itmax.chatapp.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private User user;
    private String token;

    public LoggedInUser(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public String getUserId() {
        return this.user.getId();
    }

    public String getFullname() {
        return this.user.getFullname();
    }

    public String getUsername() {
        return this.user.getUsername();
    }

    public String getImage() {
        return this.user.getImage();
    }

    public User getUser() {
        return this.user;
    }

    public String getToken() {
        return token;
    }
}