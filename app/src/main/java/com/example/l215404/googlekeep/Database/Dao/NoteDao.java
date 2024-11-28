package com.example.l215404.googlekeep.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.l215404.googlekeep.Database.models.Label;
import com.example.l215404.googlekeep.Database.models.User;

@Dao
public interface NoteDao {
    @Insert
    void insertNote(Label label);

    @Query("SELECT * FROM notes WHERE noteId = :noteId")
    User getNoteByID(String noteId);

    @Query("SELECT * FROM notes WHERE userId = :userId")
    User getNoteByUserID(String userId);

    @Query("SELECT * FROM notes WHERE noteId = :noteID")
    int getTitle(int noteID);

    @Query("SELECT content FROM notes WHERE  noteId = :noteId")
    String getContent(int noteId);
}
