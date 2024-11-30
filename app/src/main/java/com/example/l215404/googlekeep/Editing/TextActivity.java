package com.example.l215404.googlekeep.Editing;

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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.l215404.googlekeep.Database.GoogleKeepDatabase;
import com.example.l215404.googlekeep.Database.models.Note;
import com.example.l215404.googlekeep.R;
import com.example.l215404.googlekeep.SessionManager.SessionManager;

import java.util.List;

public class TextActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText contentEdittext;
    private GoogleKeepDatabase database;
    private int noteId = -1;
    private ImageView listImageView;
    private ImageView pinImageView;

    private ImageView saveButton;

    SessionManager sessionManager;

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

        database = GoogleKeepDatabase.getInstance(this);

        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            int userId = sessionManager.getUserId();
            Log.d("TextActivity", "User id: " + userId);
        }

        saveButton.setOnClickListener(v -> saveNote());
    }

    private void saveNote() {
        String title = titleEditText.getText().toString();
        String content = Html.toHtml(contentEdittext.getText());

        int userId = sessionManager.getUserId();
        Log.d("TextActivity", "saveNote: "+userId);

        new SaveNoteTask(userId, title, content).execute();
    }

    private class SaveNoteTask extends AsyncTask<Void, Void, Void> {
        private String title, content;
        private int userId;
        public SaveNoteTask(int userId, String title, String content) {
            this.userId = userId;
            this.title = title;
            this.content = content;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Note note = new Note(userId, title, content);
            database.noteDao().insertNote(note);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Toast.makeText(TextActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
        }
    }
}