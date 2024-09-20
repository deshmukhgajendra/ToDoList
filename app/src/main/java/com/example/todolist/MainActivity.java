package com.example.todolist;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.todolist.List.TextDrawable;
import com.example.todolist.MyDay.MyDay;
import com.example.todolist.Planned.Planned;
import com.example.todolist.Task.Tasks;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    MaterialToolbar toolbar;
    BottomAppBar bottom_app_bar;
    Button mydayButton, tasksButton, plannedButton;
    FloatingActionButton fab;
    String Listname;
    Database dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        bottom_app_bar = findViewById(R.id.bottom_app_bar);
        mydayButton = findViewById(R.id.mydayButton);
        plannedButton = findViewById(R.id.plannedButton);
        tasksButton = findViewById(R.id.tasksButton);
        fab= findViewById(R.id.fab);
        dbHelper= new Database(this);


        fab.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       ListDialogBox();
                   }
               });



        mydayButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MyDay.class);
            startActivity(intent);
        });
        plannedButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Planned.class);
            startActivity(intent);
        });
        tasksButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Tasks.class);
            startActivity(intent);
        });

    }

    public void ListDialogBox(){

        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.listdialogbox,null);

        ImageButton emojiButton = view.findViewById(R.id.emojiButton);
        EditText nameInput = view.findViewById(R.id.nameInput);

        emojiButton.setOnClickListener(v -> showEmojiPicker(emojiButton));

        MaterialAlertDialogBuilder builder= new MaterialAlertDialogBuilder(this);
                builder.setTitle(" New List")
                        .setView(view)
                        .setPositiveButton("Create List",(dialog , which)-> {
                            Listname=nameInput.getText().toString();
                            createdNewList(Listname);
                        })
                        .setNegativeButton("Cancel",(dialog,which)->{
                            dialog.dismiss();
                        })
                        .show();

    }
    private void showEmojiPicker(ImageButton emojiButton) {
        Dialog emojiDialog = new Dialog(this);
        emojiDialog.setContentView(R.layout.emoji_picker_dialog); // Use the new layout

        GridView emojiGrid = emojiDialog.findViewById(R.id.emojiGrid);
        String[] emojis = new String[]{"😀", "😁", "😂","🚀","❤️","😒","🥺","😘"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emojis);
        emojiGrid.setAdapter(adapter);

        emojiGrid.setOnItemClickListener((parent, view, position, id) -> {
            String selectedEmoji = emojis[position];
            emojiButton.setImageDrawable(new TextDrawable(selectedEmoji)); // Ensure TextDrawable is implemented
            emojiDialog.dismiss(); // Close the emoji picker dialog
        });

        emojiDialog.show();
    }

    private void createdNewList(String listName){
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        dbHelper.create_list_table(Listname ,sqLiteDatabase);

        ContentValues values = new ContentValues();
        values.put("list_name",listName);
        sqLiteDatabase.insert("list_master",null,values);
    }





}


