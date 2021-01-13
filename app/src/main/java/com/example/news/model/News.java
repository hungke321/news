package com.example.news.model;

import java.io.Serializable;

public class News implements Serializable {

    private String title;

    private String content;

    private String link;

    private String image;

    private boolean isLove;

    public News() {

    }

    /**
     * @param title
     * @param content
     * @param link
     * @param image
     * @param isLove
     */
    public News(String title, String content, String link,
                String image, boolean isLove) {
        this.title = title;
        this.content = content;
        this.link = link;
        this.image = image;
        this.isLove = isLove;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getContent() {
        return content;
    }

    /**
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    /**
     * @param image
     */
    public void setImage(String image) {
        this.image = image;
    }

    public boolean isLove() {
        return isLove;
    }

    /**
     * @param love
     */
    public void setLove(boolean love) {
        isLove = love;
    }

    @Override
    public String toString() {
        return title + "-" + content + "-" + image + "-" + isLove;
    }
}
