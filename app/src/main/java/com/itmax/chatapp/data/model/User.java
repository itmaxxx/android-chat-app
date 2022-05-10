package com.itmax.chatapp.data.model;

public class User {

    private String id;
    private String fullname;
    private String username;
    private String image;

    User(String id, String fullname, String username, String image) {
        this.id = id;
        this.fullname = fullname;
        this.username = username;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
