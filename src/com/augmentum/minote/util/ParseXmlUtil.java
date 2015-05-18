package com.augmentum.minote.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.text.TextUtils;
import android.util.Xml;

import com.augmentum.minote.model.Note;

public class ParseXmlUtil {

    public static List<Note> parse(InputStream in) {

        XmlPullParser parser = Xml.newPullParser();
        List<Note> list = null;
        Note note = null;

        try {
            parser.setInput(in, "utf-8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {

                switch (type) {
                case XmlPullParser.START_TAG:

                    if ("notes".equals(parser.getName())) {
                        list = new ArrayList<Note>();
                    } else if ("note".equals(parser.getName())) {
                        note = new Note();
                    } else if ("id".equals(parser.getName())) {
                        //String s = parser.nextText();
                        //note.setId(Integer.parseInt(s));
                    } else if ("content".equals(parser.getName())) {
                        String s = parser.nextText();
                        if (!TextUtils.isEmpty(s)) {
                            note.setContent(s);
                        }
                    } else if ("addTime".equals(parser.getName())) {
                        String s = parser.nextText();
                        if (!TextUtils.isEmpty(s)) {
                            note.setAddTime(s);
                        }
                    } else if ("color".equals(parser.getName())) {
                        String s = parser.nextText();
                        if (!TextUtils.isEmpty(s)) {
                            note.setColor(Integer.parseInt(s));
                        }
                    } else if ("isFolder".equals(parser.getName())) {
                        String s = parser.nextText();
                        if (!TextUtils.isEmpty(s)) {
                            note.setIsFolder(Integer.parseInt(s));
                        }
                    } else if ("folderName".equals(parser.getName())) {
                        String s = parser.nextText();
                        if (!TextUtils.isEmpty(s)) {
                            note.setFolderName(s);
                        }
                    } else if ("parentFolder".equals(parser.getName())) {
                        String s = parser.nextText();
                        if (!TextUtils.isEmpty(s)) {
                            note.setParentFolder(s);
                        }
                    } else if ("remindTime".equals(parser.getName())) {
                        String s = parser.nextText();
                        if (!TextUtils.isEmpty(s)) {
                            note.setRemindTime(s);
                        }
                    } else if ("font".equals(parser.getName())) {
                        String s = parser.nextText();
                        if (!TextUtils.isEmpty(s)) {
                            note.setFont(s);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:

                    if ("note".equals(parser.getName())) {
                        list.add(note);
                        note = null;
                    }
                default:
                    break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
