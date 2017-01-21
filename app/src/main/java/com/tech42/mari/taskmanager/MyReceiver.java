package com.tech42.mari.taskmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by mari on 12/21/16.
 */

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service1 = new Intent(context,MyAlarmService.class);
        context.startService(service1);
    }
}
