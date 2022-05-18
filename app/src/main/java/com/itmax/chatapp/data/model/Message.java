package com.itmax.chatapp.data.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {

    private String id;
    private String chatId;
    private String text;
    private User author;
    private boolean isAuthor;
    private int createdAt;
    private int updatedAt;

    public Message(String id, String chatId, String text, User author, boolean isAuthor, int createdAt, int updatedAt) {
        this.id = id;
        this.chatId = chatId;
        this.text = text;
        this.author = author;
        this.isAuthor = isAuthor;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Message(JSONObject messageJsonObject) {
        try {
            this.id = messageJsonObject.getString("id");
            this.chatId = messageJsonObject.getString("chatId");
            this.text = messageJsonObject.getString("text");
            this.author = new User(messageJsonObject.getJSONObject("author"));
            this.isAuthor = messageJsonObject.getBoolean("isAuthor");
            this.createdAt = messageJsonObject.getInt("createdAt");
            this.updatedAt = messageJsonObject.getInt("updatedAt");
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

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public boolean getIsAuthor() {
        return this.isAuthor;
    }

    public void setIsAuthor(boolean isAuthor) {
        this.isAuthor = isAuthor;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(int updatedAt) {
        this.updatedAt = updatedAt;
    }
}
