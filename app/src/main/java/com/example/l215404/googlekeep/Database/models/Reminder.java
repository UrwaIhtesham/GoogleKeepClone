package com.example.l215404.googlekeep.Database.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders",
        foreignKeys = @ForeignKey(
                entity = Note.class,
                parentColumns = "noteId",
                childColumns = "noteId",
                onDelete = ForeignKey.CASCADE
        ))
public class Reminder {
    @PrimaryKey(autoGenerate = true)
    private int reminderId;

    private int noteId;
    private int reminderTime;
    private boolean isCompleted;

    public Reminder(int noteId, int reminderTime, boolean isCompleted) {
        this.noteId = noteId;
        this.reminderTime = reminderTime;
        this.isCompleted = isCompleted;
    }

    public int getReminderId() {
        return reminderId;
    }

    public void setReminderId(int reminderId) {
        this.reminderId = reminderId;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public int getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(int reminderTime) {
        this.reminderTime = reminderTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
