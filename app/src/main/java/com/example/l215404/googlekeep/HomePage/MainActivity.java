package com.example.l215404.googlekeep.HomePage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RenderEffect;
import android.hardware.biometrics.BiometricManager;
import android.media.AsyncPlayer;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l215404.googlekeep.Authentication.SignUpActivity;
import com.example.l215404.googlekeep.Database.GoogleKeepDatabase;
import com.example.l215404.googlekeep.Database.models.User;
import com.example.l215404.googlekeep.Editing.TextActivity;
import com.example.l215404.googlekeep.R;
import com.example.l215404.googlekeep.SessionManager.SessionManager;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView pinnedRecyclerView, OthersRecyclerView;
    private NoteAdapter noteAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private ImageView profileImageView, hamburgerMenu, viewAgendaImageView, profileImage;
    private ImageView addButton;
    private LinearLayout buttonContainer, profileContainer;
    private LinearLayout textLinearLayout;
    private GoogleKeepDatabase database;

    private Button logoutButton;

    private TextView nameTextView, emailtextView;

    private ImageView cross;

    private List<Note> note_List = new ArrayList<>();

    private SessionManager sessionManager;

    private boolean isAddButtonClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        LinearLayout linearLayout = findViewById(R.id.linearLayout);

        if(linearLayout != null) {
            Log.d("MainActivity", "Linear layout is initialized");
        } else {
            Log.d("MainActivity", "Linear layout is not initialized");
        }

        hamburgerMenu = findViewById(R.id.hamburgermenu);
        profileImageView = findViewById(R.id.profilepic);
        profileImage = findViewById(R.id.profile_image);
        viewAgendaImageView = findViewById(R.id.viewagenda);
        addButton = findViewById(R.id.add);
        cross = findViewById(R.id.cross);

        logoutButton = findViewById(R.id.logout);

        nameTextView = findViewById(R.id.name);
        emailtextView = findViewById(R.id.email);

        buttonContainer = findViewById(R.id.buttonContainer);
        profileContainer = findViewById(R.id.profilelayout);
        textLinearLayout = findViewById(R.id.textLayout);

        navigationView = findViewById(R.id.navigationDrawer);

        sessionManager = new SessionManager(this);
        database = GoogleKeepDatabase.getInstance(this);

        pinnedRecyclerView = findViewById(R.id.pinnedRecyclerView);
        pinnedRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        OthersRecyclerView = findViewById(R.id.OthersRecyclerView);
        OthersRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        loadProfileImage();

        if (sessionManager.isLoggedIn()) {
            new MainActivity.FetchNotesTask().execute();
        }

        noteAdapter = new NoteAdapter(note_List, this, "Page1");
        pinnedRecyclerView.setAdapter(noteAdapter);

        ImageView blurOverlay = findViewById(R.id.blurOverlay);
        blurOverlay.setOnClickListener(v -> {
            blurOverlay.setVisibility(View.GONE);
            findViewById(R.id.navigationDrawer).setVisibility(View.GONE);
            buttonContainer.setVisibility(View.GONE);
            profileContainer.setVisibility(View.GONE);
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAddButtonClicked) {
                    blurOverlay.setVisibility(View.GONE);
                    buttonContainer.setVisibility(View.GONE);
                    addButton.setImageResource(R.drawable.plus_add);
                    buttonContainer.bringToFront();
                    isAddButtonClicked = false;
                } else {
                    blurOverlay.setVisibility(View.VISIBLE);
                    buttonContainer.setVisibility(View.VISIBLE);
                    addButton.setImageResource(R.drawable.cross_square);
                    buttonContainer.bringToFront();
                    isAddButtonClicked = true;
                }
            }
        });

        addButton.bringToFront();
        hamburgerMenu.setOnClickListener(v -> {
            applyBlur();
            findViewById(R.id.blurOverlay).setVisibility(View.VISIBLE);
            findViewById(R.id.navigationDrawer).setVisibility(View.VISIBLE);
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int noteId = item.getItemId();

                if (noteId == R.id.noteslayout) {
                    //do nothing
                } else if (noteId == R.id.reminderslayout) {

                } else if (noteId == R.id.archivelayout) {

                } else if (noteId == R.id.deletedlayout) {

                } else if (noteId == R.id.settingslayout) {

                } else if (noteId == R.id.helplayout) {

                }
                return true;
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blurOverlay.setVisibility(View.VISIBLE);
                profileContainer.setVisibility(View.VISIBLE);
                profileContainer.bringToFront();
            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blurOverlay.setVisibility(View.GONE);
                profileContainer.setVisibility(View.GONE);
            }
        });

        textLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, TextActivity.class);
                startActivity(i);
                finish();
            }
        });

        new FetchUserDetailsTask(nameTextView, emailtextView).execute();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                Intent i = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private class FetchUserDetailsTask extends AsyncTask<Void, Void, User> {
        private TextView nameTextView, emailTextView;

        public FetchUserDetailsTask(TextView nameTextView, TextView emailTextView) {
            this.nameTextView = nameTextView;
            this.emailTextView = emailTextView;
        }

        @Override
        protected User doInBackground(Void... voids) {
            int userId = sessionManager.getUserId();
            return database.userDao().getUserByID(userId);
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                nameTextView.setText(user.getName());
                emailTextView.setText(user.getEmail());
            } else {
                Toast.makeText(MainActivity.this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FetchNotesTask extends AsyncTask<Void, Void, List<Note>> {
        @Override
        protected List<Note> doInBackground(Void... voids) {
            List<com.example.l215404.googlekeep.Database.models.Note> dbNotes = database.noteDao().getAllNotes();
            Log.d("MainActivity", "Fetched from DB: " + dbNotes.size());
            List<Note> notesFinal = new ArrayList<>();

            for (com.example.l215404.googlekeep.Database.models.Note dbnote : dbNotes) {
                Note adapterNote = new Note(
                        dbnote.getNoteId(),
                        dbnote.getTitle(),
                        dbnote.getContent(),
                        dbnote.isArchived(),
                        dbnote.isPinned(),
                        dbnote.isDeleted(),
                        dbnote.getLabelId(),
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
            Log.d("Main Activity", "Fetched Products: " + noteList.size());
            if (noteList.isEmpty()) {
                Log.d("MainActivity", "No products to display");
            }
            note_List.clear();
            note_List.addAll(noteList);
            Log.d("MainActivity", "onPoseExecute: " + noteList);
            noteAdapter.notifyDataSetChanged();
        }
    }

    private void loadProfileImage() {
        String profilePicturePath;
        int userId = sessionManager.getUserId();

        new LoadProfileImageTask(userId).execute();
    }

    public int dptoPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private class LoadProfileImageTask extends AsyncTask<Void, Void, Bitmap> {
        private int userId;

        public LoadProfileImageTask(int userId) {
            this.userId = userId;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            String profilePicturePath = null;

            User user = database.userDao().getUserByID(userId);
            if (user != null) {
                profilePicturePath = user.getProfilepic();

            }

            if (profilePicturePath != null) {
                File imgfile = new File(profilePicturePath);
                if (imgfile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgfile.getAbsolutePath());

                    return getCircularBitmap(getScaledBitmap(bitmap, dptoPx(48), dptoPx(48)));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap != null) {
                profileImageView.setImageBitmap(bitmap);
                profileImage.setImageBitmap(bitmap);
            } else {
                profileImageView.setImageResource(R.drawable.user);
                Toast.makeText(MainActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap getScaledBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int diameter = Math.min(width, height);

        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        Rect rect = new Rect(0, 0, diameter, diameter);
        canvas.drawCircle(diameter / 2, diameter / 2, diameter / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return output;
    }

    private void applyBlur() {
        View rootView = findViewById(R.id.main);
        Bitmap screenshot = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(screenshot);
        rootView.draw(canvas);

        RenderScript rs = RenderScript.create(this);
        Allocation input = Allocation.createFromBitmap(rs, screenshot);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        blurScript.setRadius(10f);
        blurScript.setInput(input);
        blurScript.forEach(output);
        output.copyTo(screenshot);

        ImageView blurOverlay = findViewById(R.id.blurOverlay);
        blurOverlay.setImageBitmap(screenshot);
        blurOverlay.setVisibility(View.VISIBLE);

        input.destroy();
        output.destroy();
        blurScript.destroy();
        rs.destroy();
    }
}