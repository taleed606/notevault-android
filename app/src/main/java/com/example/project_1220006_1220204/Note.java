package com.example.project_1220006_1220204;

public class Note {

    private int id;
    private String userEmail;
    private String title;
    private String content;
    private String creationDate;
    private boolean favorite;
    private int tagId;

    public Note(int id, String userEmail, String title, String content,
                String creationDate, boolean favorite, int tagId) {

        this.id = id;
        this.userEmail = userEmail;
        this.title = title;
        this.content = content;
        this.creationDate = creationDate;
        this.favorite = favorite;
        this.tagId = tagId;
    }

    public int getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public int getTagId() {
        return tagId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }
}