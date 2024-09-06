package com.example.todolist.MyDay;

import android.content.ContentValues;
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
import com.example.todolist.R;
import com.example.todolist.model;
import com.example.todolist.mydayRecyclerAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.ArrayList;
import java.util.List;

public class MyDay extends AppCompatActivity {

    FloatingActionButton fab;
    public String task;
    Database dbHelper;
    private List<model> taskList;
    mydayRecyclerAdapter adapter;
    RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_myday);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fab=findViewById(R.id.fab);
        dbHelper= new Database(this);
        taskList= new ArrayList<>();
        adapter= new mydayRecyclerAdapter(taskList,MyDay.this);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchData();

//        LayoutInflater inflater=getLayoutInflater();
//        View view= inflater.inflate(R.layout.bottonsheetlayout,null,false);
//        RadioButton taskButton = view .findViewById(R.id.tasksButton);
//        Button addButton= view .findViewById(R.id.addButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inflate the bottom sheet layout
                LayoutInflater inflater = getLayoutInflater();
                View bottomSheetView = inflater.inflate(R.layout.bottonsheetlayout, null);

                // Access the views from the bottomSheetView, not the main view
                EditText taskEditText = bottomSheetView.findViewById(R.id.taskEditText);
                MaterialRadioButton taskButton = bottomSheetView.findViewById(R.id.tasksButton);
                Button addButton = bottomSheetView.findViewById(R.id.addButton);

                // Create and show the BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MyDay.this);
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

                // Set the onClickListener for the add button
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        task = taskEditText.getText().toString();
                        insertTask(task);
                        bottomSheetDialog.dismiss();
                        fetchData();
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public void insertTask(String task){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put("task",task);
        db.insert("task_table",null,values);
    }

    public void fetchData(){
        taskList.clear();

        Cursor cursor = dbHelper.getAllTask();

        if (cursor.moveToFirst()){
            do {
                String taskname=cursor.getString(cursor.getColumnIndex(Database.column_task));
                taskList.add(new model(taskname));
            }while (cursor.moveToNext());

        }
        cursor.close();

        adapter.notifyDataSetChanged();
    }
}