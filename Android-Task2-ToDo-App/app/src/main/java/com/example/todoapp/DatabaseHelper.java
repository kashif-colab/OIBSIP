package com.example.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TodoApp.db";
    private static final int DATABASE_VERSION = 1;

    // Users table
    private static final String USERS_TABLE = "users";

    // Tasks table
    private static final String TASKS_TABLE = "tasks";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsersTable = "CREATE TABLE " + USERS_TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL)";

        db.execSQL(createUsersTable);

        String createTasksTable = "CREATE TABLE " + TASKS_TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "task TEXT NOT NULL, " +
                "notes TEXT, " +
                "completed INTEGER DEFAULT 0, " +
                "FOREIGN KEY(user_id) REFERENCES users(id))";

        db.execSQL(createTasksTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        onCreate(db);
    }

    // Register a new user
    public boolean registerUser(String username, String password) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", hashPassword(password));

        long result = db.insert(USERS_TABLE, null, values);

        return result != -1;
    }

    // Check whether username already exists
    public boolean userExists(String username) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                USERS_TABLE,
                new String[]{"id"},
                "username = ?",
                new String[]{username},
                null,
                null,
                null
        );

        boolean exists = cursor.moveToFirst();

        cursor.close();

        return exists;
    }

    // Login authentication
    public int loginUser(String username, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                USERS_TABLE,
                new String[]{"id"},
                "username = ? AND password = ?",
                new String[]{username, hashPassword(password)},
                null,
                null,
                null
        );

        int userId = -1;

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }

        cursor.close();

        return userId;
    }

    // Hash password using SHA-256
    private String hashPassword(String password) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    // Add a task for a specific user
    public boolean addTask(int userId, String task, String notes) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("task", task);
        values.put("notes", notes);
        values.put("completed", 0);

        long result = db.insert(TASKS_TABLE, null, values);

        return result != -1;
    }

    // Get all tasks belonging to a specific user
    public Cursor getTasks(int userId) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                TASKS_TABLE,
                new String[]{"id", "task", "notes", "completed"},
                "user_id = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                "id DESC"
        );
    }

    // Mark a task as completed
    public boolean markTaskCompleted(int taskId) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("completed", 1);

        int result = db.update(
                TASKS_TABLE,
                values,
                "id = ?",
                new String[]{String.valueOf(taskId)}
        );

        return result > 0;
    }

    // Delete a task
    public boolean deleteTask(int taskId) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TASKS_TABLE,
                "id = ?",
                new String[]{String.valueOf(taskId)}
        );

        return result > 0;
    }
}