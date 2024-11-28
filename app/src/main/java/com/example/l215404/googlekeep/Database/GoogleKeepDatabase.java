package com.example.l215404.googlekeep.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.l215404.googlekeep.Database.models.Label;
import com.example.l215404.googlekeep.Database.models.Note;
import com.example.l215404.googlekeep.Database.models.Reminder;
import com.example.l215404.googlekeep.Database.models.User;

@Database(entities = {User.class, Label.class, Note.class, Reminder.class}, version = 1)
public abstract class GoogleKeepDatabase extends RoomDatabase {
}
