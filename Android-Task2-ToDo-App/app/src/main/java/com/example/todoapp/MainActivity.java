package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginButton, registerButton;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);

        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        databaseHelper = new DatabaseHelper(this);

        registerButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {

        String username =
                emailInput.getText().toString().trim();

        String password =
                passwordInput.getText().toString();

        // Check empty fields
        if (username.isEmpty() || password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter username and password",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Check login credentials
        int userId =
                databaseHelper.loginUser(
                        username,
                        password
                );

        if (userId != -1) {

            Toast.makeText(
                    this,
                    "Login successful",
                    Toast.LENGTH_SHORT
            ).show();

            // Save login session
            getSharedPreferences(
                    "TodoSession",
                    MODE_PRIVATE
            )
                    .edit()
                    .putBoolean("loggedIn", true)
                    .putInt("USER_ID", userId)
                    .putString("USERNAME", username)
                    .apply();

            // Open To-Do dashboard
            Intent intent = new Intent(
                    MainActivity.this,
                    TodoActivity.class
            );

            intent.putExtra(
                    "USER_ID",
                    userId
            );

            intent.putExtra(
                    "USERNAME",
                    username
            );

            startActivity(intent);

            // Prevent going back to login screen
            finish();

        } else {

            Toast.makeText(
                    this,
                    "Invalid username or password",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}