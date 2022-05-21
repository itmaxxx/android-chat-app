package com.itmax.chatapp.data.model;

import android.graphics.drawable.Drawable;

import androidx.core.app.NotificationCompat;

import com.itmax.chatapp.data.repositories.NotificationsRepository;

public class Notification {

    private int id;
    private String title;
    private String text;
    private int priority;
    private Drawable icon;
    private String groupId;

    public Notification(int id, String title, String text) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.priority = NotificationCompat.PRIORITY_DEFAULT;
        this.groupId = NotificationsRepository.NOTIFICATIONS_CHANNEL_ID;
    }

    public Notification(int id, String title, String text, int priority, String groupId) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.priority = priority;
        this.groupId = groupId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
