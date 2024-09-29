

package com.example.todolist;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.List.TextDrawable;
import com.example.todolist.MyDay.MyDay;
import com.example.todolist.Planned.Planned;
import com.example.todolist.Task.Tasks;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    MaterialToolbar toolbar;
    BottomAppBar bottom_app_bar;
    FloatingActionButton fab;
    public static String Listname;
    Database dbHelper;
    RecyclerView recyclerView;
    ListAdapter listAdapter;
    List<String> listNames;
    String selectedEmoji;
    Button mydayButton,plannedButton,taskButton;
    String selectedColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        bottom_app_bar = findViewById(R.id.bottom_app_bar);
        fab = findViewById(R.id.fab);
        dbHelper = new Database(this);
        recyclerView = findViewById(R.id.recyclerView);
        mydayButton=findViewById(R.id.mydayButton);
        plannedButton=findViewById(R.id.plannedButton);
        taskButton=findViewById(R.id.tasksButton);

        // Initialize list and adapter

        listNames = new ArrayList<>();
        listAdapter = new ListAdapter(listNames, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

       mydayButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent i = new Intent(MainActivity.this, MyDay.class);
               startActivity(i);
           }
       });
       plannedButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent i = new Intent(MainActivity.this, Planned.class);
               startActivity(i);
           }
       });
       taskButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent i = new Intent(MainActivity.this, Tasks.class);
               startActivity(i);
           }
       });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListDialogBox();
            }
        });
        dbHelper.create_list_master();

        // Fetch existing lists
        fetchList();
    }

    // Dialog for creating a new list
    public void ListDialogBox() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.listdialogbox, null);

        ImageButton emojiButton = view.findViewById(R.id.emojiButton);
        EditText nameInput = view.findViewById(R.id.nameInput);

        emojiButton.setOnClickListener(v -> showEmojiPicker(emojiButton));

        new MaterialAlertDialogBuilder(this)
                .setTitle("New List")
                .setView(view)
                .setPositiveButton("Create List", (dialog, which) -> {
                    Listname = nameInput.getText().toString();
                    createdNewList(Listname);
                    fetchList();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Show emoji picker dialog
    private void showEmojiPicker(ImageButton emojiButton) {
        Dialog emojiDialog = new Dialog(this);
        emojiDialog.setContentView(R.layout.emoji_picker_dialog);

        emojiDialog.getWindow().setLayout(800, 800);

        GridView emojiGrid = emojiDialog.findViewById(R.id.emojiGrid);
        String[] emojis = new String[]{"🌍","💵","🌻","🏘️","🎵","📅","😂", "🚀", "❤️", "😒", "🥺", "🏢","🧑‍❤️‍👩","🙏","💃","🎂","🙇"};

        EmojiAdapter adapter = new EmojiAdapter(this, emojis);
        emojiGrid.setAdapter(adapter);

        emojiGrid.setOnItemClickListener((parent, view, position, id) -> {
            selectedEmoji = emojis[position];
            emojiButton.setImageDrawable(new TextDrawable(selectedEmoji));
            emojiDialog.dismiss();
        });

        emojiDialog.show();
    }

    // Method to create a new list
    private void createdNewList(String listName) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        // Append the selected emoji to the list name for display purposes
        String listNameWithEmoji = selectedEmoji + " " + listName;

        // Sanitize the table name (remove the emoji)
        String sanitizedListName = listName.replaceAll("[^a-zA-Z0-9_]", "");  // Allow only alphanumeric characters and underscores

        // Create the list table with the sanitized name
        dbHelper.create_list_table(sanitizedListName, sqLiteDatabase);

        // Insert the list name with emoji into list_master for display purposes
        ContentValues values = new ContentValues();
        values.put("list_name", listNameWithEmoji);  // Save the list name with the emoji for user display
        sqLiteDatabase.insert("list_master", null, values);

        // Refresh the list of lists
        fetchList();
    }

    // Fetch all list names from list_master and update the RecyclerView
    public void fetchList() {
        listNames.clear();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT list_name FROM list_master", null);

        Log.d("MainActivity", "Total rows fetched: " + cursor.getCount()); // Log the number of rows
        if (cursor.moveToFirst()) {
            do {
                String listName = cursor.getString(cursor.getColumnIndex("list_name"));
                listNames.add(listName);
                Log.d(TAG, "fetchList: "+ listNames);
            } while (cursor.moveToNext());
        }
        cursor.close();
        listAdapter.notifyDataSetChanged();
    }
}
