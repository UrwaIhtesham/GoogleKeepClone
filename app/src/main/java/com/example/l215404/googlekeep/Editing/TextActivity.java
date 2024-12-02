package com.example.l215404.googlekeep.Editing;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ReplacementSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.l215404.googlekeep.Database.GoogleKeepDatabase;
import com.example.l215404.googlekeep.Database.models.Note;
import com.example.l215404.googlekeep.HomePage.MainActivity;
import com.example.l215404.googlekeep.R;
import com.example.l215404.googlekeep.SessionManager.SessionManager;

import java.util.List;

public class TextActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText contentEdittext;
    private GoogleKeepDatabase database;
    private int noteId = -1;
    private ImageView listImageView;
    private ImageView pinImageView, archiveImageView;

    private ImageView saveButton;
    private ImageView backImageView;

    SessionManager sessionManager;

    private ImageView dotsImageView;

    private LinearLayout dotslayout;

    private int noteIdforSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_text);

        titleEditText = findViewById(R.id.title);
        contentEdittext = findViewById(R.id.content);
        listImageView = findViewById(R.id.list);
        saveButton = findViewById(R.id.save);
        pinImageView = findViewById(R.id.pin);
        backImageView = findViewById(R.id.back);
        archiveImageView = findViewById(R.id.archive);

        database = GoogleKeepDatabase.getInstance(this);

        sessionManager = new SessionManager(this);

        dotsImageView = findViewById(R.id.dots);

        dotslayout = findViewById(R.id.dotslayout);

        Intent intent = getIntent();
        Log.d("TextActivity", "intent" + intent);
        if (intent != null){

            int noteId = intent.getIntExtra("noteId", -1);
            noteIdforSave = noteId;
            String noteTitle = intent.getStringExtra("noteTitle");
            String noteContent = intent.getStringExtra("noteContent");
            Log.d("TextActivity", "noteId: " +noteId + " noteTitle: " + noteTitle + " noteContent: " + noteContent);
            new FetchNoteTask(noteId).execute();

        }

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TextActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        if (sessionManager.isLoggedIn()) {
            int userId = sessionManager.getUserId();
            Log.d("TextActivity", "User id: " + userId);
        }

        dotsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dotslayout.setVisibility(View.VISIBLE);
                dotslayout.requestFocus();
            }
        });

        dotslayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    dotslayout.setVisibility(View.GONE);
                }
            }
        });

        dotslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteNoteTask(noteIdforSave).execute();
            }
        });

        pinImageView.setOnClickListener(v -> togglePinState());

        saveButton.setOnClickListener(v -> saveNote(noteIdforSave));

        archiveImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ArchiveTask(noteIdforSave).execute();
            }
        });
    }

    private void togglePinState() {
        String title = titleEditText.getText().toString();
        String content = Html.toHtml(contentEdittext.getText());

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(TextActivity.this, "Please save the note first.", Toast.LENGTH_SHORT).show();
            return;
        }

        new togglePinTask(title, content).execute();
    }

    private class ArchiveTask extends AsyncTask<Void, Void, Boolean> {
        private int noteId;

        public ArchiveTask(int noteId) {
            this.noteId = noteId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Note note = database.noteDao().getNoteByID(noteId);

            if (note != null) {
                note.setArchived(true);

                database.noteDao().update(note);

                return true;
            }

            return false;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                Toast.makeText(TextActivity.this, "Note archived", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TextActivity.this, "An error occurred. Try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class togglePinTask extends AsyncTask<Void, Void,Note> {
        private int noteId;
        private String title, content;

        public togglePinTask(String title, String content) {
            this.title = title;
            this.content = content;
        }

        @Override
        protected Note doInBackground(Void... voids) {
            Note note = database.noteDao().getNoteByTitleContent(title, content);
            if(note!=null) {
                note.setPinned(!note.isPinned());
                database.noteDao().update(note);
            } return note;
        }

        @Override
        protected void onPostExecute(Note note) {
            if (note != null) {
                if (note.isPinned()) {
                    pinImageView.setImageResource(R.drawable.pin);
                    Toast.makeText(TextActivity.this, "Pinned", Toast.LENGTH_SHORT).show();
                } else {
                    pinImageView.setImageResource(R.drawable.not_pin);
                    Toast.makeText(TextActivity.this, "Unpinned", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(TextActivity.this, "Save the note first", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveNote(int noteId) {
        String title = titleEditText.getText().toString();
        String content = Html.toHtml(contentEdittext.getText());

        int userId = sessionManager.getUserId();
        Log.d("TextActivity", "saveNote: "+userId);

        new SaveNoteTask(noteId, userId, title, content).execute();
    }

    private class DeleteNoteTask extends AsyncTask<Void, Void, Boolean> {
        private int noteId;

        public DeleteNoteTask(int noteId) {
            this.noteId = noteId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Note existingnote =null;
            if (noteId != -1) {
                existingnote = database.noteDao().getNoteByID(noteId);
            }

            if (existingnote != null) {
                existingnote.setDeleted(true);
                database.noteDao().update(existingnote);
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                Toast.makeText(TextActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TextActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class SaveNoteTask extends AsyncTask<Void, Void, Void> {
        private String title, content;
        private int userId, noteId;
        public SaveNoteTask(int noteId, int userId, String title, String content) {
            this.noteId = noteId;
            this.userId = userId;
            this.title = title;
            this.content = content;
            Log.d("TextActivity", "noteId: "+ noteId);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Note existingNote = null;
            if (noteId != -1) {
                existingNote = database.noteDao().getNoteByID(noteId);
            }

            if (existingNote != null) {
                // Update existing note
                existingNote.setTitle(title);
                existingNote.setContent(content);
                database.noteDao().update(existingNote);
            } else {
                // Create and save a new note
                Note newNote = new Note(userId, title, content);
                database.noteDao().insertNote(newNote);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Toast.makeText(TextActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
        }
    }

    private class FetchNoteTask extends AsyncTask<Void, Void, Note> {
        private int noteId;

        public FetchNoteTask(int noteId) {
            this.noteId = noteId;
        }

        @Override
        protected Note doInBackground(Void... voids) {
            Note note = database.noteDao().getNoteByID(noteId);
            return note;
        }

        @Override
        protected void onPostExecute(Note note) {
            if (note != null) {
                titleEditText.setText(note.getTitle());
                String content = note.getContent();
                String plainTextContent = removeHtmlTags(content);
                contentEdittext.setText(plainTextContent);
                boolean isPinned = note.isPinned();
                if (isPinned == true) {
                    pinImageView.setImageResource(R.drawable.pin);
                } else {
                    pinImageView.setImageResource(R.drawable.not_pin);
                }
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