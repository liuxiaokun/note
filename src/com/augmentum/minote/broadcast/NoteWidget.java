package com.augmentum.minote.broadcast;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.augmentum.minote.R;
import com.augmentum.minote.dao.NoteDao;
import com.augmentum.minote.dao.impl.NoteDaoImpl;
import com.augmentum.minote.model.Note;
import com.augmentum.minote.ui.AddNoteActivity;
import com.augmentum.minote.ui.ConfigActivity;

public class NoteWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        System.out.println("onUpdate");
        // 1
        // ComponentName cn = new ComponentName(context, NoteWidget.class);

        // end 1
        // Intent intent = new Intent(context, AddNoteActivity.class);
        // PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
        // intent, 0);
        // Intent intent = new Intent();
        // intent.setClass(context, AddNoteActivity.class);
        // PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
        // intent, 0);
        //
        // RemoteViews views = new RemoteViews(context.getPackageName(),
        // R.layout.widget);
        // views.setOnClickPendingIntent(R.id.widget_tv, pendingIntent);

        // appWidgetManager.updateAppWidget(appWidgetIds, views);
//        SharedPreferences sp = context.getSharedPreferences("SP", Context.MODE_PRIVATE);
//        long id = sp.getLong("id", -1);
//        NoteDao noteDao = new NoteDaoImpl(context);
//        Note note = noteDao.findById((int) id);
//        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget);
//        Intent resp = new Intent(context,AddNoteActivity.class);
//        resp.putExtra("id", note.getId());
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resp, PendingIntent.FLAG_UPDATE_CURRENT);
//        remoteView.setOnClickPendingIntent(R.id.widget_tv, pendingIntent);
//        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // appWidgetManager.updateAppWidget(cn, views);
    }

    @Override
    public void onEnabled(Context context) {

        System.out.println("onEnabled");
        super.onEnabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        System.out.println("onDeleted");
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {

        System.out.println("onDisabled");
        super.onDisabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        // System.out.println("onReceive");
        // if (AddNoteActivity.ACTION.equals(intent.getAction())) {
        // RemoteViews remoteView = new RemoteViews(context.getPackageName(),
        // R.layout.widget);
        // AppWidgetManager appWidgetManager =
        // AppWidgetManager.getInstance(context);
        // ComponentName componentName = new ComponentName(context,
        // NoteWidget.class);
        // appWidgetManager.updateAppWidget(componentName, remoteView);
        // } else {
        // super.onReceive(context, intent);
        // }
    }

}
