package com.example.todoapp;

public class Task {

    int id;
    String task;
    String notes;
    int completed;

    public Task(int id, String task, String notes, int completed) {
        this.id = id;
        this.task = task;
        this.notes = notes;
        this.completed = completed;
    }
}