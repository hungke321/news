package com.example.news.model;

import java.io.Serializable;

public class PlayList implements Serializable {

    private String id;

    private String channelId;

    private String title;

    private String description;

    private String thumnail;

    /**
     *
     */
    public PlayList() {

    }

    /**
     * @param id
     * @param channelId
     * @param title
     * @param description
     * @param thumnail
     */
    public PlayList(String id, String channelId, String title,
                    String description, String thumnail) {
        this.id = id;
        this.channelId = channelId;
        this.title = title;
        this.description = description;
        this.thumnail = thumnail;
    }

    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    public String getChannelId() {
        return channelId;
    }

    /**
     * @param channelId
     */
    public void setChannelId(String channelId) {
        this.channelId = channelId;
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

    public String getDescription() {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumnail() {
        return thumnail;
    }

    /**
     * @param thumnail
     */
    public void setThumnail(String thumnail) {
        this.thumnail = thumnail;
    }
}
