package com.rccardirector;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

public class NotificationUtils {
    private static final String NOTIFICATION_CHANNEL_ID = "id";
    private static final int SIMPLE_MSG_TEXT_INTENT_ID = 1;

    public static void notifyStop(Context context) {
        String text = "Stop!";
        notifyMessage(context, text);
    }

    public static void notifyBump(Context context) {
        String text = "Bump";
        notifyMessage(context, text);
    }

    public static void notifyNoMoving(Context context) {
        String text = "No Moving";
        notifyMessage(context, text);
    }

    public static void notifyNoRight(Context context) {
        String text = "No Right way";
        notifyMessage(context, text);
    }

    private static void notifyMessage(Context context, String text) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_ID,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(text)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        text))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);


        // set the notification's priority to PRIORITY_HIGH.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(SIMPLE_MSG_TEXT_INTENT_ID, notificationBuilder.build());
    }
}
