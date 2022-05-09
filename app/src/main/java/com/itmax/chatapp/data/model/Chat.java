package com.itmax.chatapp.data.model;

public class Chat {

    private String id;
    private String name;
    private String image;
    private String lastMessage;

    public Chat(String id, String name, String image, String lastMessage) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.lastMessage = lastMessage;
    }

    public String getId() {
        return this.id;
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
