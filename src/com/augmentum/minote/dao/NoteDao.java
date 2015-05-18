package com.augmentum.minote.dao;

import java.util.List;

import com.augmentum.minote.model.Note;

public interface NoteDao {

    public long add(Note note);

    public List<Note> findAll();

    public Note findById(Integer id);

    public Note findFolderLastAdded(String folderName);

    public List<Note> findAllNoteAndFolder();

    public List<Note> findAllByFolder(String folderName);

    public boolean delete(Integer id);

    public boolean deleteAll();

    public boolean update(Note note);

    public List<Note> findAllFolder();

    public int getNoteCount();

    public int getCountInFolder(String folder);

    public int getFolderCount();
}
