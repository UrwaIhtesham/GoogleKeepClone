package com.example.l215404.googlekeep.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.l215404.googlekeep.Database.models.Reminder;
import com.example.l215404.googlekeep.Database.models.User;

@Dao
public interface ReminderDao {
    @Insert
    void insertReminder(Reminder reminder);

    @Query("SELECT * FROM reminders WHERE reminderId = :reminderId")
    User getReminderByID(int reminderId);

    @Query("SELECT * FROM reminders WHERE noteId = :noteId")
    User getReminderByNoteID(int noteId);
}
