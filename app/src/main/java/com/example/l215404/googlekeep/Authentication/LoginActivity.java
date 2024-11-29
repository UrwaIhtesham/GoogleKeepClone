package com.example.l215404.googlekeep.Authentication;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private GoogleKeepDatabase database;
    SessionManager sessionManager;
    private TextView signupTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        database = GoogleKeepDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        emailEditText = findViewById(R.id.email_input);
        passwordEditText = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.next_button);

        signupTextView = findViewById(R.id.login_account);

        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });

        loginButton.setEnabled(false);
        loginButton.setAlpha(0.5f);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                loginButton.setEnabled(!email.isEmpty() && !password.isEmpty());
                loginButton.setAlpha(1.0f);
            }

            @Override
            public void afterTextChanged(Editable s) {  }
        };

        emailEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            new LoginTask(email, password, sessionManager).execute();
        });
    }

    private class LoginTask extends AsyncTask<Void, Void, Boolean> {
        private String email, password;
        private int userId;
        private SessionManager sessionManager;

        public LoginTask(String email, String password, SessionManager sessionManager) {
            this. email = email;
            this.password = password;
            this.sessionManager = sessionManager;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            User user = database.userDao().getUserByEmail(email);

            if(user != null && user.getPassword().equals(password)) {
                userId = user.getUserId();
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                sessionManager.createSession(userId, email);
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(LoginActivity.this, "Login Failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}