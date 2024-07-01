package com.eazevedo.routeplanning.messaging;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.eazevedo.routeplanning.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String messageBody = "Message:\n";
        Log.i(TAG, "From: " + remoteMessage.getFrom());
        if(remoteMessage.getNotification()!= null){
            messageBody +="\tNotification: \n";
            messageBody += "\t\tTitle: "+ remoteMessage.getNotification().getTitle()+"\n";
            messageBody += "\t\tBody: "+ remoteMessage.getNotification().getBody()+"\n";
        }
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData().toString());
            messageBody += "\tData:\n";
            Map<String, String> data = remoteMessage.getData();
            for (Map.Entry<String,String> entry : data.entrySet()){
                messageBody += "\t\t"+ entry.getKey()+ ":" + entry.getValue()+"\n";
            }
        }
        sendNotification(messageBody);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("MESSAGE",messageBody);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = "fcm_default_channel";
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_firebase)
                        .setContentTitle("FCM Message")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_VIBRATE| Notification.DEFAULT_SOUND)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

}
