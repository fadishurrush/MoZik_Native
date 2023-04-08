package com.example.mozik;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.VoiceInteractor;
import android.content.Context;


import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class App extends Application {
    public static final String CHANNEL_ID = "Channel";
    public static final String Channel_Previous = "actionprevious";
    public static final String CHANNEL_Play = "actionplay";
    public static final String CHANNEL_Next = "actionnext";
    public static Notification notification;
    public static void createNotification(Context context,AudioModel audioModel, int playButton , int pos , int size){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat( context , "tag");

            notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(audioModel.getTitle())
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();

            notificationManagerCompat.notify(1,notification);
        }
    }


}
