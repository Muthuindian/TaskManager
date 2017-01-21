package com.tech42.mari.taskmanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

/**
 * Created by mari on 12/20/16.
 */
public class MyAlarmService extends Service{

    private NotificationManager manager;
    Uri soundUri;
    PendingIntent resultPendingIntent;
    NotificationCompat.Builder mBuilder;
    Intent resultIntent;
    TaskStackBuilder stackBuilder;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.todo);
        mBuilder.setContentTitle("TodoList Alert");
        mBuilder.setContentText("One task to be Completed!Click to View");
        mBuilder.setSound(soundUri);
        mBuilder.setAutoCancel(true);
        //mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        resultIntent = new Intent(this, MainActivity.class);
        stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, mBuilder.build());

    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
