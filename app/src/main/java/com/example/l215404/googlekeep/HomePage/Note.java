package com.example.l215404.googlekeep.HomePage;

public class Note {
    private int Id;
    private String Title;
    private String content;
    private Boolean isArchived;
    private Boolean isPinned;
    private Boolean isDeleted;
    private int labelId;
    private long createdAt;
    private long updatedAt;

    public Note(int id, String Title, String content, Boolean isArchived, Boolean isPinned, Boolean isDeleted, int labelId, Long createdAt, Long updatedAt) {
        this.Id = id;
        this.Title = Title;
        this.content = content;
        this.isArchived = isArchived;
        this.isPinned = isPinned;
        this.isDeleted = isDeleted;
        this.labelId = labelId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }

    public Boolean getPinned() {
        return isPinned;
    }

    public void setPinned(Boolean pinned) {
        isPinned = pinned;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public int getLabelId() {
        return labelId;
    }

    public void setLabelId(int labelId) {
        this.labelId = labelId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
