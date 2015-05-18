package com.augmentum.minote.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NoteFolder extends Note implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<Note> notes = new ArrayList<Note>();

    public List<Note> getNotes() {

        return notes;
    }

    public void setNotes(List<Note> notes) {

        this.notes = notes;
    }

}
