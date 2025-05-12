package com.onlineradiofm.trancemusicradio.firebasemsg;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.onlineradiofm.trancemusicradio.R;
import com.onlineradiofm.trancemusicradio.SplashActivity;
import com.onlineradiofm.trancemusicradio.ypylibs.utils.YPYLog;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class FBMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FBMessagingService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        try {
            // TODO(developer): Handle FCM messages here.
            YPYLog.d(TAG, "From: " + remoteMessage.getFrom());
            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                YPYLog.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
                sendNotification(remoteMessage.getNotification().getBody());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */

    private void sendNotification(String messageBody) {
        try {
            if (TextUtils.isEmpty(messageBody)) return;

            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            String channelId = getPackageName() + ".N3";
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.ic_radio_noti)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        getPackageName() + "_CHANNEL1",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            Notification mNotification = notificationBuilder.build();
            notificationManager.notify(0, mNotification);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}
