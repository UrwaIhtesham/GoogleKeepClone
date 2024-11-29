package com.example.l215404.googlekeep.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.l215404.googlekeep.Database.models.Note;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    void insertNote(Note note);

    @Query("SELECT * FROM notes")
    List<Note> getAllNotes();

    @Query("SELECT * FROM notes WHERE noteId = :noteId")
    Note getNoteByID(String noteId);

    @Query("SELECT * FROM notes WHERE userId = :userId")
    Note getNoteByUserID(String userId);

    @Query("SELECT * FROM notes WHERE noteId = :noteID")
    int getTitle(int noteID);

    @Query("SELECT content FROM notes WHERE  noteId = :noteId")
    String getContent(int noteId);
}
