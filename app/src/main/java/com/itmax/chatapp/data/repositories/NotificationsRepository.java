package com.itmax.chatapp.data.repositories;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.itmax.chatapp.MainActivity;
import com.itmax.chatapp.R;
import com.itmax.chatapp.data.model.Notification;

public class NotificationsRepository {

    private static volatile NotificationsRepository instance;
    private MainActivity mainActivity;
    public static String NOTIFICATIONS_CHANNEL_ID = "ChatAppNotifications";

    public NotificationsRepository(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.createNotificationChannel(NOTIFICATIONS_CHANNEL_ID);
    }

    public static NotificationsRepository createInstance(MainActivity mainActivity) {
        if (instance == null) {
            instance = new NotificationsRepository(mainActivity);
        }
        return instance;
    }

    public static NotificationsRepository getInstance() {
        return instance;
    }

    public void createNotificationChannel(String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mainActivity.getString(R.string.channel_name);
            String description = mainActivity.getString(R.string.channel_description);

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = mainActivity.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showNotification(Notification notification) {
        try {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(mainActivity, NOTIFICATIONS_CHANNEL_ID)
                            .setSmallIcon(R.drawable.telegram)
                            .setContentTitle(notification.getTitle())
                            .setContentText(notification.getText())
                            .setPriority(notification.getPriority());

            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(mainActivity);

            // Here we should pass unique notification id
            notificationManager.notify(notification.getId(), builder.build());
        }
        catch (Exception ex) {
            Log.e("NotificationsService", ex.getMessage());
        }
    }
}
