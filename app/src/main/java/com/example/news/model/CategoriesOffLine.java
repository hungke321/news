package com.example.news.model;

import java.io.Serializable;

public class CategoriesOffLine implements Serializable {

    private String title;

    private String time;

    private String link;

    public CategoriesOffLine() {
    }

    /**
     * @param title
     * @param time
     */
    public CategoriesOffLine(String title, String time) {
        this.title = title;
        this.time = time;
    }

    /**
     * @param title
     * @param time
     * @param link
     */
    public CategoriesOffLine(String title, String time, String link) {
        this.title = title;
        this.time = time;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    /**
     * @param time
     */
    public void setTime(String time) {
        this.time = time;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
