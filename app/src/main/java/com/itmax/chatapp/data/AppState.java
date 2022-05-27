package com.itmax.chatapp.data;

public class AppState {

    private static volatile AppState instance;

    private String currentChatId;

    public AppState() {
        currentChatId = null;
    }

    public static AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }

    public String getCurrentChatId() {
        return currentChatId;
    }

    public void setCurrentChatId(String currentChatId) {
        this.currentChatId = currentChatId;
    }
}
