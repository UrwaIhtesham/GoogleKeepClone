package com.example.l215404.googlekeep.ArchiveDelete;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l215404.googlekeep.Database.GoogleKeepDatabase;
import com.example.l215404.googlekeep.Editing.TextActivity;
import com.example.l215404.googlekeep.HomePage.MainActivity;
import com.example.l215404.googlekeep.HomePage.Note;
import com.example.l215404.googlekeep.HomePage.NoteAdapter;
import com.example.l215404.googlekeep.R;
import com.example.l215404.googlekeep.SessionManager.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ArchiveActivity extends AppCompatActivity {

    private RecyclerView archiveRecyclerView;
    private LinearLayout archiveLinearLayout;
    private NoteAdapter noteAdapter;

    private GoogleKeepDatabase database;
    private SessionManager sessionManager;

    List<Note> archiveNoteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_archive);

        archiveRecyclerView = findViewById(R.id.archiveRecyclerView);
        archiveLinearLayout = findViewById(R.id.noarchive);

        sessionManager = new SessionManager(this);
        database = GoogleKeepDatabase.getInstance(this);

        archiveRecyclerView = findViewById(R.id.archiveRecyclerView);
        archiveRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        if (sessionManager.isLoggedIn()) {
            new ArchiveActivity.FetchNotesTask().execute();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ArchiveActivity.this, MainActivity.class);
        // Optionally, you can add flags to clear the back stack if needed
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Finish the current activity to prevent navigating back to it
        finish();
    }

    private class FetchNotesTask extends AsyncTask<Void, Void, List<Note>> {
        @Override
        protected List<Note> doInBackground(Void... voids) {
            List<com.example.l215404.googlekeep.Database.models.Note> dbNotes = database.noteDao().getAllNotes();
            Log.d("MainActivity", "Fetched from DB: " + dbNotes.size());
            List<Note> notesFinal = new ArrayList<>();

            for (com.example.l215404.googlekeep.Database.models.Note dbnote : dbNotes) {
                String noteContent = dbnote.getContent();
                String plainTextContent = removeHtmlTags(noteContent);

                Note adapterNote = new Note(
                        dbnote.getNoteId(),
                        dbnote.getTitle(),
                        plainTextContent,
                        dbnote.isArchived(),
                        dbnote.isPinned(),
                        dbnote.isDeleted(),
                        dbnote.getCreatedAt(),
                        dbnote.getUpdatedAt()
                );

                notesFinal.add(adapterNote);
                Log.d("MainActivity", "Notes: " + notesFinal);
            }


            return notesFinal;
        }

        @Override
        protected void onPostExecute(List<Note> noteList) {
            super.onPostExecute(noteList);
            Log.d("Archive Activity", "Fetched Notes: " + noteList.size());
            if (noteList.isEmpty()) {
                Log.d("ArchiveActivity", "No notes to display");
            }

            List<Note> archivedNotes = new ArrayList<>();

            for(Note note: noteList) {
                if (note.getArchived() == true && note.getDeleted() == false) {
                    archivedNotes.add(note);
                }
            }

            if (archivedNotes.isEmpty()) {
                Log.d("ArchiveActivity", "No deleted notes to display");
                archiveLinearLayout.setVisibility(View.VISIBLE); // Show the "No deleted notes" message
                archiveRecyclerView.setVisibility(View.GONE);   // Hide the RecyclerView
            } else {
                // If there are deleted notes, hide the "No deleted notes" message and show the RecyclerView
                archiveLinearLayout.setVisibility(View.GONE);
                archiveRecyclerView.setVisibility(View.VISIBLE);
            }

            Log.d("Archiveactivity", "Archived Notes: " +archivedNotes.size());

            if (noteAdapter == null ){
                archiveRecyclerView = findViewById(R.id.archiveRecyclerView);
                noteAdapter = new NoteAdapter(archivedNotes, ArchiveActivity.this, note -> {
                    Intent intent = new Intent(ArchiveActivity.this, TextActivity.class);
                    intent.putExtra("noteId", note.getId());
                    intent.putExtra("noteTitle", note.getTitle());
                    intent.putExtra("noteContent", note.getContent());
                    startActivity(intent);
                });
                archiveRecyclerView.setLayoutManager(new GridLayoutManager(ArchiveActivity.this, 2));
                archiveRecyclerView.setAdapter(noteAdapter);
            } else {
                noteAdapter.notifyDataSetChanged();
            }

        }

        private String removeHtmlTags(String content) {
            if (content == null) {
                return "";
            }
            return content.replaceAll("<[^>]*>", "");
        }
    }
}