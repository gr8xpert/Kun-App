package com.example.kunworld.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.kunworld.MainActivity;
import com.example.kunworld.R;

/**
 * Firebase Cloud Messaging service for handling push notifications.
 *
 * To enable Firebase Push Notifications:
 * 1. Create a Firebase project at https://console.firebase.google.com/
 * 2. Add your Android app to the project
 * 3. Download google-services.json and place it in app/ folder
 * 4. Uncomment the Firebase dependencies in build.gradle
 * 5. Uncomment this class to extend FirebaseMessagingService
 *
 * This is a placeholder implementation that can be activated once Firebase is configured.
 */
public class KunWorldMessagingService {
    // Uncomment and extend FirebaseMessagingService when Firebase is configured
    // extends FirebaseMessagingService

    private static final String TAG = "KunWorldFCM";
    private static final String CHANNEL_ID = "kunworld_notifications";
    private static final String CHANNEL_NAME = "KunWorld Updates";

    /**
     * Called when a new FCM token is generated.
     * Send this token to your server to enable push notifications.
     */
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "FCM Token: " + token);
        // TODO: Send token to your backend server
        sendTokenToServer(token);
    }

    /**
     * Called when a message is received from FCM.
     */
    // @Override
    // public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
    //     Log.d(TAG, "From: " + remoteMessage.getFrom());
    //
    //     // Handle notification payload
    //     if (remoteMessage.getNotification() != null) {
    //         String title = remoteMessage.getNotification().getTitle();
    //         String body = remoteMessage.getNotification().getBody();
    //         showNotification(title, body);
    //     }
    //
    //     // Handle data payload
    //     if (remoteMessage.getData().size() > 0) {
    //         handleDataMessage(remoteMessage.getData());
    //     }
    // }

    private void sendTokenToServer(String token) {
        // TODO: Implement server-side token storage
        // This allows your server to send targeted notifications to this device
    }

    /**
     * Display a notification to the user.
     */
    public static void showNotification(Context context, String title, String body) {
        NotificationManager notificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for new courses, updates, and reminders");
            notificationManager.createNotificationChannel(channel);
        }

        // Create intent for notification tap
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_quote)
            .setContentTitle(title != null ? title : "KunWorld")
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show notification
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
