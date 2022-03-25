package com.live.programming.an21_functionalapp;

// model class

import java.io.Serializable;

public class Study implements Serializable {
    private String studyId;
    private String title;
    private String desc;
    private String image;
    private String level;

    public Study(String studyId, String title, String desc, String image, String level) {
        this.studyId = studyId;
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.level = level;
    }

    public String getStudyId() {
        return studyId;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getImage() {
        return image;
    }

    public String getLevel() {
        return level;
    }
}
