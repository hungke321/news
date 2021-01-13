package com.example.news.model;

import java.io.Serializable;

public class Video implements Serializable {

    private String id;

    private String publishedAt;

    private String channelId;

    private String playListId;

    private String title;

    private String description;

    private String thumbnail;

    private boolean isLove;

    public Video() {
    }

    /**
     * @param id
     * @param publishedAt
     * @param channelId
     * @param playListId
     * @param title
     * @param description
     * @param thumbnail
     * @param isLove
     */
    public Video(String id, String publishedAt, String channelId, String playListId,
                 String title, String description, String thumbnail, boolean isLove) {
        this.id = id;
        this.publishedAt = publishedAt;
        this.channelId = channelId;
        this.playListId = playListId;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.isLove = isLove;
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

    public String getPublishedAt() {
        return publishedAt;
    }

    /**
     * @param publishedAt
     */
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
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

    public String getPlayListId() {
        return playListId;
    }

    /**
     * @param playListId
     */
    public void setPlayListId(String playListId) {
        this.playListId = playListId;
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

    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * @param thumbnail
     */
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
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
        return super.toString();
    }
}
