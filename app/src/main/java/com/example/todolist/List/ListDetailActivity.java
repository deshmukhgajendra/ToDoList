package com.example.todolist.List;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.ListAdapter;
import com.example.todolist.Database;
import com.example.todolist.MainActivity;
import com.example.todolist.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ListDetailActivity extends AppCompatActivity {

    RecyclerView taskRecyclerView;
    String listName;
    String taskName;
    Database dbHelper;
    List<ListModel> listNames;
    ListViewRecyclerAdapter adapter;
    FloatingActionButton fabButton;
    MaterialToolbar listToolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        dbHelper = new Database(this);
        listNames = new ArrayList<>();
        adapter = new ListViewRecyclerAdapter(listNames, this);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskRecyclerView.setAdapter(adapter);
        fabButton = findViewById(R.id.fabButton);
        listToolBar= findViewById(R.id.listToolBar);

        listToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListDetailActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

       // listName = getIntent().getStringExtra("LIST_NAME");
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialogBox();
            }
        });


        // Get the list name from Intent
        listName = getIntent().getStringExtra("list_name");
        if (listName == null || listName.isEmpty()) {
            Log.e("ListDetailActivity", "List name is null or empty!");
            // Handle the error (e.g., show a message to the user)
            return; // Exit if there's no valid list name
        }


        fetchData();
        listToolBar.setTitle(listName);

    }

    private void fetchData() {
        listNames.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT task FROM " + listName, null); // Use listName
        if (cursor.moveToFirst()) {
            do {
                String name=cursor.getString(cursor.getColumnIndex(Database.column_task));
                listNames.add(new ListModel(name));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }


    public void showDialogBox(){
        Log.d("ListDetailActivity", "showDialogBox called");
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.bottonsheetlayout, null);
        EditText taskEditText = view.findViewById(R.id.taskEditText);
        MaterialButton addButton = view.findViewById(R.id.addButton);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskName = taskEditText.getText().toString();
                insertDataInList();
                bottomSheetDialog.dismiss();
                fetchData();
            }
        });
    }

    public void insertDataInList(){
        SQLiteDatabase sqLiteDatabase= dbHelper.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put("task",taskName);
        sqLiteDatabase.insert(listName,null,values);
    }
}