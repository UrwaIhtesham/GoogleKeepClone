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

public class DeletedActivity extends AppCompatActivity {

    private RecyclerView deleteRecyclerView;
    private LinearLayout deleteLinearLayout;
    private NoteAdapter noteAdapter;

    private GoogleKeepDatabase database;
    private SessionManager sessionManager;

    List<Note> deleteNoteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deleted);

        deleteRecyclerView = findViewById(R.id.deleteRecyclerView);
        deleteLinearLayout = findViewById(R.id.nodelete);

        sessionManager = new SessionManager(this);
        database = GoogleKeepDatabase.getInstance(this);

        deleteRecyclerView = findViewById(R.id.deleteRecyclerView);
        deleteRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        if (sessionManager.isLoggedIn()) {
            new DeletedActivity.FetchNotesTask().execute();
        }



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DeletedActivity.this, MainActivity.class);
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
            Log.d("DeletedActivity", "Fetched from DB: " + dbNotes.size());
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
                Log.d("DeletedActivity", "Notes: " + notesFinal);
            }


            return notesFinal;
        }

        @Override
        protected void onPostExecute(List<Note> noteList) {
            super.onPostExecute(noteList);
            Log.d("Archive Activity", "Fetched Notes: " + noteList.size());
            if (noteList.isEmpty()) {
                Log.d("ArchiveActivity", "No notes to display");
                deleteLinearLayout.setVisibility(View.VISIBLE);
            }

            List<Note> deletedNotes = new ArrayList<>();

            for(Note note: noteList) {
                if (note.getDeleted() == true) {
                    deletedNotes.add(note);
                }
            }

            if (deletedNotes.isEmpty()) {
                Log.d("ArchiveActivity", "No deleted notes to display");
                deleteLinearLayout.setVisibility(View.VISIBLE); // Show the "No deleted notes" message
                deleteRecyclerView.setVisibility(View.GONE);   // Hide the RecyclerView
            } else {
                // If there are deleted notes, hide the "No deleted notes" message and show the RecyclerView
                deleteLinearLayout.setVisibility(View.GONE);
                deleteRecyclerView.setVisibility(View.VISIBLE);
            }

            Log.d("Archiveactivity", "Archived Notes: " +deletedNotes.size());

            if (noteAdapter == null ){
                deleteRecyclerView = findViewById(R.id.deleteRecyclerView);
                noteAdapter = new NoteAdapter(deletedNotes, DeletedActivity.this, note -> {
                    Intent intent = new Intent(DeletedActivity.this, TextActivity.class);
                    intent.putExtra("noteId", note.getId());
                    intent.putExtra("noteTitle", note.getTitle());
                    intent.putExtra("noteContent", note.getContent());
                    startActivity(intent);
                });
                deleteRecyclerView.setLayoutManager(new GridLayoutManager(DeletedActivity.this, 2));
                deleteRecyclerView.setAdapter(noteAdapter);
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