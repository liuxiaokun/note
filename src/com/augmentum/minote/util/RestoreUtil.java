package com.augmentum.minote.util;

import java.util.List;

import android.content.Context;

import com.augmentum.minote.dao.NoteDao;
import com.augmentum.minote.dao.impl.NoteDaoImpl;
import com.augmentum.minote.model.Note;


public class RestoreUtil {

    public static void restore(List<Note> list, Context context){
        NoteDao noteDao = new NoteDaoImpl(context);

        for (Note tem : list) {
            noteDao.add(tem);
        }
        
    }
}
