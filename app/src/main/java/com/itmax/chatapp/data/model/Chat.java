package com.itmax.chatapp.data.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Chat {

    private String id;
    private String name;
    private String image;
    private Message lastMessage;

    public Chat(String id, String name, String image, Message lastMessage) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.lastMessage = lastMessage;
    }

    public Chat(JSONObject chatJsonObject) {
        try {
            this.id = chatJsonObject.getString("id");
            this.name = chatJsonObject.getString("name");
            this.image = chatJsonObject.getString("image");
            this.lastMessage = new Message(chatJsonObject.getJSONObject("lastMessage"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
