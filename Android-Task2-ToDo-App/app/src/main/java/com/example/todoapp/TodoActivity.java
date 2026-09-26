package com.example.todoapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TodoActivity extends AppCompatActivity {

    TextView welcomeText;
    TextView emptyText;

    Button addTaskButton;
    Button logoutButton;

    ListView taskListView;

    int userId;
    String username;

    DatabaseHelper databaseHelper;

    ArrayList<Task> taskList;
    TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check login session
        boolean loggedIn = getSharedPreferences(
                "TodoSession",
                MODE_PRIVATE
        ).getBoolean("loggedIn", false);

        if (!loggedIn) {

            Intent intent = new Intent(
                    TodoActivity.this,
                    MainActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();

            return;
        }

        setContentView(R.layout.activity_todo);

        // Connect XML components
        welcomeText = findViewById(R.id.welcomeText);
        emptyText = findViewById(R.id.emptyText);

        addTaskButton = findViewById(R.id.addTaskButton);
        logoutButton = findViewById(R.id.logoutButton);

        taskListView = findViewById(R.id.taskListView);

        // Get logged-in user information
        userId = getIntent().getIntExtra(
                "USER_ID",
                -1
        );

        username = getIntent().getStringExtra(
                "USERNAME"
        );

        // If intent data is missing, get it from session
        if (userId == -1) {

            userId = getSharedPreferences(
                    "TodoSession",
                    MODE_PRIVATE
            ).getInt("USER_ID", -1);
        }

        if (username == null) {

            username = getSharedPreferences(
                    "TodoSession",
                    MODE_PRIVATE
            ).getString("USERNAME", "User");
        }

        // Create database helper
        databaseHelper = new DatabaseHelper(this);

        // Show welcome message
        welcomeText.setText(
                "Welcome, " + username
        );

        // Create task list
        taskList = new ArrayList<>();

        // Create adapter
        taskAdapter = new TaskAdapter(
                this,
                taskList,
                databaseHelper,
                this::loadTasks
        );

        taskListView.setAdapter(taskAdapter);

        // Load saved tasks
        loadTasks();

        // Add task button
        addTaskButton.setOnClickListener(
                v -> showAddTaskDialog()
        );

        // Logout button
        logoutButton.setOnClickListener(
                v -> logoutUser()
        );
    }

    // Load tasks belonging to current user
    private void loadTasks() {

        taskList.clear();

        Cursor cursor = databaseHelper.getTasks(userId);

        if (cursor.moveToFirst()) {

            do {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String task = cursor.getString(
                        cursor.getColumnIndexOrThrow("task")
                );

                String notes = cursor.getString(
                        cursor.getColumnIndexOrThrow("notes")
                );

                int completed = cursor.getInt(
                        cursor.getColumnIndexOrThrow("completed")
                );

                taskList.add(
                        new Task(
                                id,
                                task,
                                notes,
                                completed
                        )
                );

            } while (cursor.moveToNext());
        }

        cursor.close();

        // Show or hide empty message
        if (taskList.isEmpty()) {

            emptyText.setVisibility(
                    View.VISIBLE
            );

        } else {

            emptyText.setVisibility(
                    View.GONE
            );
        }

        // Refresh ListView
        taskAdapter.notifyDataSetChanged();
    }

    // Show Add Task dialog
    private void showAddTaskDialog() {

        View dialogView = LayoutInflater.from(this)
                .inflate(
                        R.layout.dialog_add_task,
                        null
                );

        EditText taskInput =
                dialogView.findViewById(
                        R.id.taskInput
                );

        EditText notesInput =
                dialogView.findViewById(
                        R.id.notesInput
                );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Add New Task")
                        .setView(dialogView)
                        .setPositiveButton(
                                "Add",
                                null
                        )
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                dialogInterface -> {

                    Button addButton =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );

                    addButton.setOnClickListener(
                            v -> {

                                String task =
                                        taskInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String notes =
                                        notesInput
                                                .getText()
                                                .toString()
                                                .trim();

                                // Validate task
                                if (task.isEmpty()) {

                                    Toast.makeText(
                                            this,
                                            "Please enter a task",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                // Save task
                                boolean added =
                                        databaseHelper.addTask(
                                                userId,
                                                task,
                                                notes
                                        );

                                if (added) {

                                    Toast.makeText(
                                            this,
                                            "Task added successfully",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    dialog.dismiss();

                                    // Refresh task list
                                    loadTasks();

                                } else {

                                    Toast.makeText(
                                            this,
                                            "Failed to add task",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                    );
                }
        );

        dialog.show();
    }

    // Logout user
    private void logoutUser() {

        // Clear saved session
        getSharedPreferences(
                "TodoSession",
                MODE_PRIVATE
        )
                .edit()
                .clear()
                .apply();

        Toast.makeText(
                this,
                "Logged out successfully",
                Toast.LENGTH_SHORT
        ).show();

        // Go back to login screen
        Intent intent = new Intent(
                TodoActivity.this,
                MainActivity.class
        );

        // Clear dashboard from back stack
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}