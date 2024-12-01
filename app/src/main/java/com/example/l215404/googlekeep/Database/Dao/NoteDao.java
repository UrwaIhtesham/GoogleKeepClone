package com.example.l215404.googlekeep.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.l215404.googlekeep.Database.models.Note;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    void insertNote(Note note);

    @Query("SELECT * FROM notes")
    List<Note> getAllNotes();

    @Update
    void update(Note note);

    @Query("SELECT * FROM notes WHERE noteId = :noteId")
    Note getNoteByID(int noteId);

    @Query("SELECT * FROM notes WHERE userId = :userId")
    Note getNoteByUserID(int userId);

    @Query("SELECT title FROM notes WHERE noteId = :noteID")
    String getTitle(int noteID);

    @Query("SELECT * FROM notes WHERE title = :title AND content = :content")
    Note getNoteByTitleContent(String title, String content);

    @Query("DELETE FROM notes WHERE noteId = :noteId")
    void deleteNoteByID(int noteId);

    @Query("SELECT content FROM notes WHERE  noteId = :noteId")
    String getContent(int noteId);
}
