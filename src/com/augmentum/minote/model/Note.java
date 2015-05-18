package com.augmentum.minote.model;

import java.io.Serializable;

public class Note implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String content;
    private String addTime;
    private Integer color;
    private Integer isFolder;
    private String folderName;
    private String parentFolder;
    private String remindTime;
    private String font;

    public Note() {
        //empty constructor.
    }

    public Note(String addTime, Integer isFolder, String folderName) {

        super();
        this.addTime = addTime;
        this.isFolder = isFolder;
        this.folderName = folderName;
    }

    public Note(String content, String addTime, Integer color,
            Integer isFolder, String parentFolder, String remindTime,
            String font) {

        this.content = content;
        this.addTime = addTime;
        this.color = color;
        this.isFolder = isFolder;
        this.parentFolder = parentFolder;
        this.remindTime = remindTime;
        this.font = font;
    }

    public Integer getId() {

        return id;
    }

    public void setId(Integer id) {

        this.id = id;
    }

    public String getContent() {

        return content;
    }

    public void setContent(String content) {

        this.content = content;
    }

    public String getAddTime() {

        return addTime;
    }

    public void setAddTime(String addTime) {

        this.addTime = addTime;
    }

    public Integer getColor() {

        return color;
    }

    public void setColor(Integer color) {

        this.color = color;
    }

    public Integer getIsFolder() {

        return isFolder;
    }

    public void setIsFolder(Integer isFolder) {

        this.isFolder = isFolder;
    }

    public String getFolderName() {

        return folderName;
    }

    public void setFolderName(String folderName) {

        this.folderName = folderName;
    }

    public String getParentFolder() {

        return parentFolder;
    }

    public void setParentFolder(String parentFolder) {

        this.parentFolder = parentFolder;
    }

    public String getRemindTime() {

        return remindTime;
    }

    public void setRemindTime(String remindTime) {

        this.remindTime = remindTime;
    }

    public String getFont() {

        return font;
    }

    public void setFont(String font) {

        this.font = font;
    }

}
