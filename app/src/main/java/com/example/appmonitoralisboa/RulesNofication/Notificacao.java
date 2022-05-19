package com.example.appmonitoralisboa.RulesNofication;
import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.example.appmonitoralisboa.activities.*;
import androidx.core.app.NotificationCompat;

import com.example.appmonitoralisboa.R;

public class Notificacao {
    private NotificationManager mNotificationManager;
    public void pushNotification(Context context, String title, String message){ NotificationCompat.Builder mBuilder =
    new NotificationCompat.Builder(context.getApplicationContext(), "notify_001");
    Intent ii = new Intent(context.getApplicationContext(), MainActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, PendingIntent.FLAG_MUTABLE);

    NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
    bigText.bigText(message);
    bigText.setBigContentTitle(title);
    bigText.setSummaryText(message);

    mBuilder.setContentIntent(pendingIntent);
    mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
    mBuilder.setContentTitle(title);
    mBuilder.setContentText(message);
    mBuilder.setPriority(android.app.Notification.PRIORITY_MAX);
    mBuilder.setStyle(bigText);

    mNotificationManager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        String channelId = "notify_001";
        NotificationChannel channel = new NotificationChannel(channelId,
                        "notify",
                        NotificationManager.IMPORTANCE_HIGH);
        mNotificationManager.createNotificationChannel(channel);
        mBuilder.setChannelId(channelId);
            }

        mNotificationManager.notify(0, mBuilder.build());

        }

    }


