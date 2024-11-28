package com.example.l215404.googlekeep.Database.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes",
        foreignKeys = {
        @ForeignKey(
                        entity = User.class,
                        parentColumns = "userId",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Label.class,
                        parentColumns = "labelId",
                        childColumns = "labelId",
                        onDelete = ForeignKey.CASCADE

                )
})
public class Note {
    @PrimaryKey(autoGenerate = true)
    private int noteId;

    private int userId;
    private int labelId;
    private String title;
    private String content;
    private boolean isArchived;
    private boolean isPinned;
    private boolean isDeleted;
    private long createdAt;
    private long updatedAt;

    public Note(int userId, int labelId, String title, String content) {
        this.userId = userId;
        this.labelId = labelId;
        this.title = title;
        this.content = content;
        this.isArchived = false;
        this.isPinned = false;
        this.isDeleted = false;
        this.createdAt = System.currentTimeMillis();
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getLabelId() {
        return labelId;
    }

    public void setLabelId(int labelId) {
        this.labelId = labelId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
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
