package com.example.todolist.Task;

import android.content.ContentValues;
import android.content.Context;
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

import com.example.todolist.Database;
import com.example.todolist.R;
import com.example.todolist.mydayRecyclerAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.radiobutton.MaterialRadioButton;

public class Tasks extends AppCompatActivity {
    FloatingActionButton fab;
    Database dbHelper;
    mydayRecyclerAdapter adapter;
    String task;

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
                        bottomSheetDialog.dismiss();
                    }
                });
            }
        });


    }

    public void insertData(){
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        ContentValues values= new ContentValues();
        //values
    }
}