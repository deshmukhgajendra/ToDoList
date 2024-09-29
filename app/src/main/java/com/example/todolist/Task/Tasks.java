package com.example.todolist.Task;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Database;
import com.example.todolist.MainActivity;
import com.example.todolist.R;
import com.example.todolist.MyDay.model;
import com.example.todolist.MyDay.mydayRecyclerAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.ArrayList;
import java.util.List;

public class Tasks extends AppCompatActivity {
    FloatingActionButton fab;
    Database dbHelper;
    taskRecyclerAdapter adapter;
    String task;
    RecyclerView recyclerView;
    List<taskModel>tasklist= new ArrayList<>();
    MaterialToolbar tasksToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tasks);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fab=findViewById(R.id.fab);
        recyclerView=findViewById(R.id.recyclerView);
        adapter= new taskRecyclerAdapter(tasklist,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbHelper= new Database(this);
        tasksToolBar= findViewById(R.id.tasksToolBar);
        setSupportActionBar(tasksToolBar);


            tasksToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        fetchData();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater= getLayoutInflater();
                View bottomSheetView =inflater.inflate(R.layout.bottonsheetlayout,null);

                EditText editText=bottomSheetView.findViewById(R.id.taskEditText);
                MaterialRadioButton radioButton=bottomSheetView.findViewById(R.id.tasksButton);
                Button addButton=bottomSheetView.findViewById(R.id.addButton);

                BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(Tasks.this);
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        task=editText.getText().toString().trim();
                        insertData(task);
                        bottomSheetDialog.dismiss();
                        fetchData();
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });


    }

    public void insertData(String task){
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put("task",task);
        sqLiteDatabase.insert("task_table",null,values);
    }
    public void fetchData(){
        tasklist.clear();

        Cursor cursor = dbHelper.getAllmydayTask();
        if (cursor.moveToFirst()){
            do {
                String taskname =cursor.getString(cursor.getColumnIndex(Database.column_task));
                tasklist.add(new taskModel(taskname));
            }while (cursor.moveToNext());


        }cursor.close();

        adapter.notifyDataSetChanged();
    }
    public void onBackPressed() {

        super.onBackPressed();
        Intent i = new Intent(Tasks.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}