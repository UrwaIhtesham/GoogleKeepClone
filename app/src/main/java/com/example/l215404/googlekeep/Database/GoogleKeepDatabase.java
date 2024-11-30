package com.example.l215404.googlekeep.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.l215404.googlekeep.Database.Dao.LabelDao;
import com.example.l215404.googlekeep.Database.Dao.NoteDao;
import com.example.l215404.googlekeep.Database.Dao.ReminderDao;
import com.example.l215404.googlekeep.Database.Dao.UserDao;
import com.example.l215404.googlekeep.Database.models.Label;
import com.example.l215404.googlekeep.Database.models.Note;
import com.example.l215404.googlekeep.Database.models.Reminder;
import com.example.l215404.googlekeep.Database.models.User;

@Database(entities = {User.class, Label.class, Note.class, Reminder.class}, version = 3)
public abstract class GoogleKeepDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract LabelDao labelDao();
    public abstract NoteDao noteDao();
    public abstract ReminderDao reminderDao();

    private static volatile GoogleKeepDatabase INSTANCE;

    public static synchronized GoogleKeepDatabase getInstance(final Context context){
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    GoogleKeepDatabase.class, "googlekeep_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
