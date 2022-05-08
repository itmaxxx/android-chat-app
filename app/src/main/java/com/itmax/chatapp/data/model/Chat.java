package com.itmax.chatapp.data.model;

public class Chat {

    private String name;
    private String image;
    private String lastMessage;

    public Chat(String name, String image, String lastMessage) {
        this.name = name;
        this.image = image;
        this.lastMessage = lastMessage;
    }

    public String getName() {
        return this.name;
    }

    public String getImage() {
        return this.image;
    }

    public String getLastMessage() {
        return this.lastMessage;
    }

}
