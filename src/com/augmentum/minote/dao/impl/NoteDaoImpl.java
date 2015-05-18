package com.augmentum.minote.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.augmentum.minote.dao.NoteDao;
import com.augmentum.minote.db.MyDbHelper;
import com.augmentum.minote.model.Note;

public class NoteDaoImpl implements NoteDao {

    MyDbHelper myDbHelper;

    public NoteDaoImpl(Context context) {

        myDbHelper = new MyDbHelper(context, "db_note.db", null, 1);

    }

    @Override
    public long add(Note note) {

        SQLiteDatabase db = myDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("content", note.getContent());
        values.put("add_time", System.currentTimeMillis());
        values.put("color", note.getColor());
        values.put("is_folder", note.getIsFolder());
        values.put("folder_name", note.getFolderName());
        values.put("parent_folder", note.getParentFolder());
        values.put("remind_time", note.getRemindTime());
        values.put("font", note.getFont());

        long rowid = db.insert("t_notes", null, values);
        db.close();
        return rowid;
    }

    @Override
    public List<Note> findAll() {

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query("t_notes", null, "parent_folder is null", null, null, null, "is_folder desc");
        List<Note> notes = new ArrayList<Note>();

        while (cursor.moveToNext()) {

            Note note = new Note();

            note.setId(cursor.getInt(0));
            note.setContent(cursor.getString(1));
            note.setAddTime(cursor.getString(2));
            note.setColor(cursor.getInt(3));
            note.setIsFolder((cursor.getInt(4)));
            note.setFolderName(cursor.getString(5));
            note.setParentFolder(cursor.getString(6));
            note.setRemindTime(cursor.getString(7));
            note.setFont(cursor.getString(8));

            notes.add(note);
        }
        cursor.close();
        db.close();
        return notes;
    }

    @Override
    public Note findById(Integer id) {

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query("t_notes", null, "_id = ?", new String[] { String.valueOf(id) }, null, null, null);
        Note note = new Note();
        while (cursor.moveToNext()) {

            note.setId(cursor.getInt(0));
            note.setContent(cursor.getString(1));
            note.setAddTime(cursor.getString(2));
            note.setColor(cursor.getInt(3));
            note.setIsFolder((cursor.getInt(4)));
            note.setFolderName(cursor.getString(5));
            note.setParentFolder(cursor.getString(6));
            note.setRemindTime(cursor.getString(7));
            note.setFont(cursor.getString(8));
        }
        cursor.close();
        db.close();
        return note;
    }

    @Override
    public List<Note> findAllNoteAndFolder() {

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query("t_notes", null, null, null, null, null, "is_folder desc");
        List<Note> notes = new ArrayList<Note>();

        while (cursor.moveToNext()) {

            Note note = new Note();

            note.setId(cursor.getInt(0));
            note.setContent(cursor.getString(1));
            note.setAddTime(cursor.getString(2));
            note.setColor(cursor.getInt(3));
            note.setIsFolder((cursor.getInt(4)));
            note.setFolderName(cursor.getString(5));
            note.setParentFolder(cursor.getString(6));
            note.setRemindTime(cursor.getString(7));
            note.setFont(cursor.getString(8));

            notes.add(note);
        }
        cursor.close();
        db.close();
        return notes;
    }

    @Override
    public List<Note> findAllByFolder(String folderName) {

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query("t_notes", null, "parent_folder = ?", new String[] { folderName }, null, null, null);
        List<Note> notes = new ArrayList<Note>();

        while (cursor.moveToNext()) {

            Note note = new Note();

            note.setId(cursor.getInt(0));
            note.setContent(cursor.getString(1));
            note.setAddTime(cursor.getString(2));
            note.setColor(cursor.getInt(3));
            note.setIsFolder((cursor.getInt(4)));
            note.setFolderName(cursor.getString(5));
            note.setParentFolder(cursor.getString(6));
            note.setRemindTime(cursor.getString(7));
            note.setFont(cursor.getString(8));

            notes.add(note);
        }
        cursor.close();
        db.close();
        return notes;
    }

    @Override
    public boolean delete(Integer id) {

        SQLiteDatabase db = myDbHelper.getWritableDatabase();
        String idStr = String.valueOf(id);
        int result = db.delete("t_notes", "_id=?", new String[] { idStr });
        db.close();
        return result == 0 ? false : true;
    }

    @Override
    public boolean deleteAll() {

        SQLiteDatabase db = myDbHelper.getWritableDatabase();
        int result = db.delete("t_notes", null, null);
        db.close();
        return result == 0 ? false : true;
    }

    @Override
    public boolean update(Note note) {

        SQLiteDatabase db = myDbHelper.getWritableDatabase();
        String idStr = String.valueOf(note.getId());
        ContentValues values = new ContentValues();
        values.put("content", note.getContent());
        values.put("add_time", System.currentTimeMillis());
        values.put("color", note.getColor());
        values.put("font", note.getFont());
        values.put("is_folder", note.getIsFolder());
        values.put("folder_name", note.getFolderName());
        values.put("parent_folder", note.getParentFolder());
        values.put("remind_time", note.getRemindTime());
        int result = db.update("t_notes", values, "_id=?", new String[] { idStr });
        db.close();
        return result > 0 ? true : false;
    }

    @Override
    public List<Note> findAllFolder() {

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query("t_notes", null, "is_folder = 1", null, null, null, null);
        // String aa = "1";
        // Cursor cursor = db.query("t_notes", null, "is_folder = ?", new
        // String[]{aa}, null, null, null);
        List<Note> notes = new ArrayList<Note>();

        while (cursor.moveToNext()) {

            Note note = new Note();

            note.setId(cursor.getInt(0));
            note.setContent(cursor.getString(1));
            note.setAddTime(cursor.getString(2));
            note.setColor(cursor.getInt(3));
            note.setIsFolder((cursor.getInt(4)));
            note.setFolderName(cursor.getString(5));
            note.setParentFolder(cursor.getString(6));
            note.setRemindTime(cursor.getString(7));
            note.setFont(cursor.getString(8));

            notes.add(note);
        }
        cursor.close();
        db.close();
        return notes;
    }

    @Override
    public int getNoteCount() {

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query("t_notes", null, "parent_folder is null and is_folder = 0", null, null, null, null);
        int a = (null != cursor) ? cursor.getCount() : 0;
        return a;
    }

    @Override
    public int getCountInFolder(String folder) {

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query("t_notes", null, "parent_folder = ?", new String[] { folder }, null, null, null);
        int a = (null != cursor) ? cursor.getCount() : 0;
        return a;
    }

    @Override
    public int getFolderCount() {

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query("t_notes", null, "is_folder = 1", null, null, null, null);
        int a = (null != cursor) ? cursor.getCount() : 0;
        return a;
    }

    @Override
    public Note findFolderLastAdded(String folderName) {

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query("t_notes", null, "parent_folder = ?", new String[] { folderName }, null, null,
                "add_time desc");
        List<Note> notes = new ArrayList<Note>();

        while (cursor.moveToNext()) {

            Note note = new Note();

            note.setId(cursor.getInt(0));
            note.setContent(cursor.getString(1));
            note.setAddTime(cursor.getString(2));
            note.setColor(cursor.getInt(3));
            note.setIsFolder((cursor.getInt(4)));
            note.setFolderName(cursor.getString(5));
            note.setParentFolder(cursor.getString(6));
            note.setRemindTime(cursor.getString(7));
            note.setFont(cursor.getString(8));

            notes.add(note);
        }
        cursor.close();
        db.close();
        if (null != notes && notes.size() != 0) {
            return notes.get(0);
        } else {
            return null;
        }
    }

}
