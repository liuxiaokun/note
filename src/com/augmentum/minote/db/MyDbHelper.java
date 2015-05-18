package com.augmentum.minote.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDbHelper extends SQLiteOpenHelper {

    private String LIST_NOTE_SQL = "create table t_notes(_id integer primary key autoincrement, content, add_time, color, is_folder, folder_name, parent_folder, remind_time, font)";

    public MyDbHelper(Context context, String name, CursorFactory factory, int version) {

        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(LIST_NOTE_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // do nothing!
    }

}
