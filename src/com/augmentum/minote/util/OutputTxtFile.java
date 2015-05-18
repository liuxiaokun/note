package com.augmentum.minote.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

import com.augmentum.minote.model.Note;

public class OutputTxtFile {

    public static boolean toSdCard(List<Note> allNotes) {

        List<Note> folders = new ArrayList<Note>();
        List<Note> notes = new ArrayList<Note>();
        for (Note tem : allNotes) {

            if (tem.getIsFolder() == 0) {
                notes.add(tem);
            } else {
                folders.add(tem);
            }
        }
        String newLine = "\r\n";
        StringBuilder sb = new StringBuilder();
        sb.append("小米便签" + DateUtil.getMonthAndTime(System.currentTimeMillis()));
        sb.append("共输出" + newLine + folders.size() + "个文件夹和" + notes.size() + "条便条" + newLine);

        for (int i = 0; i < folders.size(); i++) {
            String folderName = folders.get(i).getFolderName();
            sb.append("-----------" + newLine);
            sb.append(folderName + newLine);
            sb.append("-----------" + newLine);

            for (int j = 0; j < notes.size(); j++) {
                if (folderName.equals(notes.get(j).getParentFolder())) {
                    Note note = notes.get(j);
                    sb.append(DateUtil.getMonthAndTime(Long.parseLong(note.getAddTime())) + newLine);
                    sb.append(note.getContent() + newLine);
                }
            }
        }
        sb.append("-----------------"+ newLine);
        sb.append("小米便签" + newLine);
        sb.append("-----------------"+ newLine);
        for (Note tem : notes) {
            if (null == tem.getParentFolder()) {
                sb.append(DateUtil.getMonthAndTime(Long.parseLong(tem.getAddTime())) + newLine);
                sb.append(tem.getContent()+ newLine);
            }
        }
        try {
            File sdDir = Environment.getExternalStorageDirectory();
            String dir = sdDir.toString() + File.separator + "xm_notes";
            File dirFile = new File(dir + File.separator);
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }
            File file = new File(dirFile, "notes_" + DateUtil.getYearAndDate(System.currentTimeMillis()) + ".txt");
            OutputStream out = new FileOutputStream(file, true);
            out.write(sb.toString().getBytes());

            out.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
