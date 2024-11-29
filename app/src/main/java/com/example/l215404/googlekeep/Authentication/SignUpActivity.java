package com.example.l215404.googlekeep.Authentication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.l215404.googlekeep.Database.GoogleKeepDatabase;
import com.example.l215404.googlekeep.Database.models.User;
import com.example.l215404.googlekeep.HomePage.MainActivity;
import com.example.l215404.googlekeep.R;
import com.example.l215404.googlekeep.SessionManager.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SignUpActivity extends AppCompatActivity {

    private ImageView profileImageplaceholder;
    private EditText nameTextView, emailTextView, passwordTextView;
    private Button nextButton;
    private TextView loginTextView;

    private GoogleKeepDatabase database;

    private static final int PICK_IMAGE_REQUEST = 1;
    private byte[] selectedImageBytes = null;
    private String selectedImagePath = null;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        nameTextView = findViewById(R.id.name_input);
        emailTextView = findViewById(R.id.email_input);
        passwordTextView = findViewById(R.id.password_input);
        profileImageplaceholder = findViewById(R.id.user);
        nextButton = findViewById(R.id.next_button);
        loginTextView = findViewById(R.id.login_account);

        database = GoogleKeepDatabase.getInstance(this);

        sessionManager = new SessionManager(this);

        if(sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        nextButton.setEnabled(false);
        nextButton.setAlpha(0.5f);

        nameTextView.addTextChangedListener(signUpTextWatcher);
        emailTextView.addTextChangedListener(signUpTextWatcher);
        passwordTextView.addTextChangedListener(signUpTextWatcher);

        profileImageplaceholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

    }

    private final TextWatcher signUpTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            nextButton.setEnabled(!TextUtils.isEmpty(nameTextView.getText())
            && !TextUtils.isEmpty(emailTextView.getText())
            && !TextUtils.isEmpty(passwordTextView.getText()));
            nextButton.setAlpha(1.0f);
        }

        @Override
        public void afterTextChanged(Editable s) {  }
    };

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profileImageplaceholder.setImageBitmap(bitmap);
                selectedImagePath = saveImageToExternalStorage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private byte[] convertImageToByteArray(Uri imageUri) throws  IOException {
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private String saveImageToExternalStorage(Bitmap bitmap) throws IOException {
        File directory = new File(getExternalFilesDir(null), "profile_pics");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filename = System.currentTimeMillis()+".png";
        File imageFile = new File(directory, filename);

        FileOutputStream outputStream = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        outputStream.flush();
        outputStream.close();

        return imageFile.getAbsolutePath();
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void signUp() {
        String name = nameTextView.getText().toString().trim();
        String email = emailTextView.getText().toString().trim();
        String password = passwordTextView.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImagePath == null){
            Toast.makeText(this, "Please select a profile picture", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(name, email, password, selectedImagePath);
        Log.d("SignUp", "Attepting sign up ...");
        Log.d("SignUp", "name: "+ name + "Email: "+email);
        new InsertUserTask(sessionManager).execute(user);
    }

    private class InsertUserTask extends AsyncTask<User, Void, String> {

        private SessionManager sessionManager;

        public InsertUserTask(SessionManager sessionManager) {
            this.sessionManager = sessionManager;
        }

        @Override
        protected String doInBackground(User... users) {
            User user = users[0];
            User existingUser = database.userDao().checkIFEmailExists(user.getEmail());
            Log.d("InsertUserTask", "Existing User: "+(existingUser!=null));
            if (existingUser!=null) {
                return "Email already registered";
            } else {
                database.userDao().insertUser(user);
                Log.d("SignUp", "User to signup: " + user.getUserId() + " " + user.getEmail());
                sessionManager.createSession(user.getUserId(), user.getEmail());
                return "Account created successfully!";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(SignUpActivity.this, s, Toast.LENGTH_SHORT).show();
            if (s.equals("Account created successfully!")) {
                if (sessionManager.getEmail() != null ) {
                    Toast.makeText(SignUpActivity.this, "Session Created successfully", Toast.LENGTH_SHORT).show();
                }
                Intent i =new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }
}