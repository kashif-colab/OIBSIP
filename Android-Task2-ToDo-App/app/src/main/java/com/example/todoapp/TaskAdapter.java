package com.example.todoapp;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class TaskAdapter extends BaseAdapter {

    Context context;
    ArrayList<Task> taskList;
    DatabaseHelper databaseHelper;
    Runnable refreshList;

    public TaskAdapter(
            Context context,
            ArrayList<Task> taskList,
            DatabaseHelper databaseHelper,
            Runnable refreshList
    ) {
        this.context = context;
        this.taskList = taskList;
        this.databaseHelper = databaseHelper;
        this.refreshList = refreshList;
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return taskList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.task_item, parent, false);
        }

        TextView taskName = view.findViewById(R.id.taskName);
        TextView taskNotes = view.findViewById(R.id.taskNotes);
        Button completeButton = view.findViewById(R.id.completeButton);
        Button deleteButton = view.findViewById(R.id.deleteButton);

        Task currentTask = taskList.get(position);

        taskName.setText(currentTask.task);
        taskNotes.setText(currentTask.notes);

        if (currentTask.completed == 1) {

            taskName.setPaintFlags(
                    taskName.getPaintFlags() |
                            Paint.STRIKE_THRU_TEXT_FLAG
            );

            completeButton.setText("Completed");

        } else {

            taskName.setPaintFlags(
                    taskName.getPaintFlags() &
                            (~Paint.STRIKE_THRU_TEXT_FLAG)
            );

            completeButton.setText("Complete");
        }

        completeButton.setOnClickListener(v -> {

            if (currentTask.completed == 0) {

                databaseHelper.markTaskCompleted(currentTask.id);

                refreshList.run();
            }
        });

        deleteButton.setOnClickListener(v -> {

            databaseHelper.deleteTask(currentTask.id);

            refreshList.run();
        });

        return view;
    }
}