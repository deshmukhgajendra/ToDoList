package com.example.todolist.List;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
    ListAdapter listAdapter;


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


        setSupportActionBar(listToolBar);
        listToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListDetailActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });


        listToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.renamelistButton) {
                    // Handle rename list action
                    renamelist();
                    return true;
                } else if (itemId == R.id.changeThemeButton) {
                    // Handle change theme action
                    return true;
                } else if (itemId == R.id.printlistButton) {
                    // Handle print list action
                    return true;
                } else if (itemId == R.id.deletelistButton) {
                    String tableName= listName.replaceAll("[^a-zA-Z0-9_]", "");
                    Log.d(TAG, "onMenuItemClick: "+ tableName);
                    dbHelper.delete_list_table(tableName);
                    dbHelper.delete_list_master_row(listName);
                    Intent i = new Intent(ListDetailActivity.this, MainActivity.class);
                    startActivity(i);

                    return true;
                } else {
                    return false;
                }
            }
        });

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar_menu, menu);
        return true;
    }




    private void fetchData() {
        listNames.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Sanitize listName before using it
        String sanitizedListName = listName.replaceAll("[^a-zA-Z0-9_]", "");  // Simple example: Replace special characters with '_'

        try {
            Cursor cursor = db.rawQuery("SELECT task FROM " + sanitizedListName, null);
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(Database.column_task));
                    listNames.add(new ListModel(name));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("ListDetailActivity", "Error fetching data from table: " + sanitizedListName, e);
            // Optionally show a message to the user if the table doesn't exist
        }

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

    public void insertDataInList() {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("task", taskName);

        // Sanitize listName before using it
        String sanitizedListName = listName.replaceAll("[^a-zA-Z0-9_]", "");

        try {
            sqLiteDatabase.insert(sanitizedListName, null, values);
        } catch (Exception e) {
            Log.e("ListDetailActivity", "Error inserting data into table: " + sanitizedListName, e);
            // Optionally handle insertion error
        }
    }
    public void renamelist(){
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.listdialogbox,null);
        ImageButton emojiButton = view.findViewById(R.id.emojiButton);
        EditText nameInput = view.findViewById(R.id.nameInput);
        nameInput.setHint(listName);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Rename List")
                .setView(view)
                .setPositiveButton("Rename", (dialog, which) -> {
                    // add rename logic here
                    String newTableName=nameInput.getText().toString();
                    String oldTablename=listName.replaceAll("[^a-zA-Z0-9_]", "");
                    dbHelper.renameList(oldTablename,newTableName,listName,newTableName);

                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

}