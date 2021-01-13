package com.example.news.model;

import java.io.Serializable;

public class Categories implements Serializable {

    private int resoure;

    private String name;

    private String link;

    private boolean isSelect;

    /*

     */
    public Categories() {
    }

    /**
     * @param resoure
     * @param name
     * @param link
     * @param isSelect
     */

    public Categories(int resoure, String name,
                      String link, boolean isSelect) {
        this.resoure = resoure;
        this.name = name;
        this.link = link;
        this.isSelect = isSelect;
    }

    public int getResoure() {
        return resoure;
    }

    /**
     * @param resoure
     */
    public void setResoure(int resoure) {
        this.resoure = resoure;
    }

    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    /**
     * @param select
     */
    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getLink() {
        return link;
    }

    /**
     * @param link
     */
    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
