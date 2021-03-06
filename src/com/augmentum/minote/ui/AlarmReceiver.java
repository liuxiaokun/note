package com.augmentum.minote.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String content = intent.getStringExtra("content");
        Intent in = new Intent();
        in.setClass(context, AlarmActivity.class);
        in.putExtra("content", content);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(in);
        
    }

}
