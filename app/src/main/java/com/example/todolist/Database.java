package com.example.todolist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    public static int version = 2;
    public static String db_name = "db";
    public static String myday_table = "myday_table";  // Table for "My Day" tasks
    public static String task_table = "task_table";   // Table for general tasks
    public static String column_id = "id";
    public static String column_task = "task";

    public Database(@Nullable Context context) {
        super(context, db_name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        create_myDay_table(sqLiteDatabase);
        create_task_table(sqLiteDatabase);
        create_list_master(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop older tables if they exist and recreate them
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + myday_table);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + task_table);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS list_master");
        onCreate(sqLiteDatabase);
    }

    public Cursor getAllmydayTask() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + task_table, null);  // Select from the correct table
    }
    public Cursor getAllTask(){
        SQLiteDatabase db = this .getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+ myday_table,null);
    }

    // Method to create "myday_table"
    public void create_myDay_table(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE IF NOT EXISTS " + myday_table + " ("
                + column_id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + column_task + " TEXT)";
        sqLiteDatabase.execSQL(query);
    }

    // Method to create "task_table"
    public void create_task_table(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE IF NOT EXISTS " + task_table + " ("
                + column_id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + column_task + " TEXT)";
        sqLiteDatabase.execSQL(query);
    }
    public void create_list_master(SQLiteDatabase sqLiteDatabase){
        String query = " CREATE TABLE IF NOT EXISTS list_master (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "list_name TEXT UNIQUE)";
        sqLiteDatabase.execSQL(query);
    }
    public void create_list_table(String tableName ,SQLiteDatabase sqLiteDatabase){
        String query = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + column_id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +column_task + " TEXT) ";
        sqLiteDatabase.execSQL(query);

    }
}
