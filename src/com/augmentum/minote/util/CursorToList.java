package com.augmentum.minote.util;

import java.util.ArrayList;
import java.util.List;

import com.augmentum.minote.model.Note;

import android.database.Cursor;
import android.util.Log;

public class CursorToList {

    public static List<Note> convert(Cursor cursor) {

        List<Note> notes = new ArrayList<Note>();

        while (cursor.moveToNext()) {
            Note note = new Note();
            note.setId(cursor.getInt(0));
            note.setContent(cursor.getString(1));
            note.setAddTime(cursor.getString(2));
            note.setColor(cursor.getInt(3));
            note.setIsFolder((cursor.getInt(4)));
            note.setRemindTime(cursor.getString(5));
            note.setFont(cursor.getString(6));

            notes.add(note);
        }
        Log.i("lxk", notes.size()+"");
        return notes;
    }
}
