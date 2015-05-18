package com.augmentum.minote.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import android.os.Environment;
import android.util.Xml;

import com.augmentum.minote.model.Note;

public class BackUpUtil {

    public static boolean backup(List<Note> notes) {

        XmlSerializer xmlSerializer = Xml.newSerializer();

        File dir = new File(Environment.getExternalStorageDirectory().toString()+ File.separator + "xm_notes" + File.separator);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File backupFile = new File(dir, "notes.xml");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(backupFile);

            xmlSerializer.setOutput(fos, "utf-8");
            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.startTag(null, "notes");

            for (Note tem : notes) {
                xmlSerializer.startTag(null, "note");

                xmlSerializer.startTag(null, "id");
                xmlSerializer.text(tem.getId() + "");
                xmlSerializer.endTag(null, "id");

                xmlSerializer.startTag(null, "content");
                xmlSerializer.text(null != tem.getContent() ? tem.getContent() : "");
                xmlSerializer.endTag(null, "content");

                xmlSerializer.startTag(null, "addTime");
                xmlSerializer.text(null != tem.getAddTime() ? tem.getAddTime() : "");
                xmlSerializer.endTag(null, "addTime");

                xmlSerializer.startTag(null, "color");
                xmlSerializer.text(null != tem.getColor() ? tem.getColor()+"" : "");
                xmlSerializer.endTag(null, "color");

                xmlSerializer.startTag(null, "isFolder");
                xmlSerializer.text(null != tem.getIsFolder() ? tem.getIsFolder()+"" : "");
                xmlSerializer.endTag(null, "isFolder");

                xmlSerializer.startTag(null, "folderName");
                xmlSerializer.text(null != tem.getFolderName() ? tem.getFolderName() : "");
                xmlSerializer.endTag(null, "folderName");

                xmlSerializer.startTag(null, "parentFolder");
                xmlSerializer.text(null != tem.getParentFolder() ? tem.getParentFolder() : "");
                xmlSerializer.endTag(null, "parentFolder");

                xmlSerializer.startTag(null, "remindTime");
                xmlSerializer.text(null != tem.getRemindTime() ? tem.getRemindTime() : "");
                xmlSerializer.endTag(null, "remindTime");

                xmlSerializer.startTag(null, "font");
                xmlSerializer.text(null != tem.getFont() ? tem.getFont() : "");
                xmlSerializer.endTag(null, "font");

                xmlSerializer.endTag(null, "note");
            }

            xmlSerializer.endTag(null, "notes");
            xmlSerializer.endDocument();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
