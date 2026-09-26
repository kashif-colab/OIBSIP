package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText registerUsername;
    EditText registerPassword;
    EditText registerConfirmPassword;
    Button registerAccountButton;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerUsername = findViewById(R.id.registerUsername);
        registerPassword = findViewById(R.id.registerPassword);
        registerConfirmPassword = findViewById(R.id.registerConfirmPassword);
        registerAccountButton = findViewById(R.id.registerAccountButton);

        databaseHelper = new DatabaseHelper(this);

        registerAccountButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String username = registerUsername.getText().toString().trim();
        String password = registerPassword.getText().toString();
        String confirmPassword =
                registerConfirmPassword.getText().toString();

        // Empty field validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Password length validation
        if (password.length() < 8) {
            Toast.makeText(
                    this,
                    "Password must be at least 8 characters",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Password confirmation
        if (!password.equals(confirmPassword)) {
            Toast.makeText(
                    this,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Duplicate username check
        if (databaseHelper.userExists(username)) {
            Toast.makeText(
                    this,
                    "Username or email already exists",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Save user
        boolean registered =
                databaseHelper.registerUser(username, password);

        if (registered) {

            Toast.makeText(
                    this,
                    "Registration successful",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    RegisterActivity.this,
                    MainActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
            finish();

        } else {

            Toast.makeText(
                    this,
                    "Registration failed",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}